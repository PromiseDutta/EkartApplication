package com.info.ekart.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.info.ekart.utility.ErrorInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Component
// @Component registers this as a Spring bean so it can be
// injected into SecurityConfig and added to the filter chain
public class RateLimitFilter extends OncePerRequestFilter {
    // OncePerRequestFilter is a Spring base class that guarantees
    // doFilterInternal() runs exactly once per HTTP request.
    // Your JwtAuthenticationFilter also extends this same class.
    // Without it, filters can run multiple times on
    // internal forwards/redirects inside Spring.

    private static final int DEFAULT_LIMIT = 10;
    // Applied to: cart, products, order history, profile
    // 10 requests/minute is generous for normal usage

    private static final long WINDOW_MINUTES = 1;
    // The time window. Every minute, all counters reset naturally
    // because the Redis key includes the current minute in its name.

    private final RedisTemplate<String, String> redisTemplate;
    // The bean we added in RedisConfig — used to call INCR and EXPIRE

    private final ObjectMapper objectMapper;
    // Spring Boot auto-creates this bean (part of Jackson)
    // Used to serialize ErrorInfo → JSON string for the 429 response

    public RateLimitFilter(RedisTemplate<String, String> redisTemplate,
                           ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        // Constructor injection — Spring injects both beans automatically
    }

    // -----------------------------------------------------------
    // MAIN METHOD — runs on every single HTTP request
    // -----------------------------------------------------------
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // STEP A — Who is making this request?
        String identifier = resolveIdentifier(request);
        // Returns email for authenticated users (JWT already processed)
        // Returns IP address for unauthenticated requests (login, register)

        // STEP B — What limit applies to this endpoint?
        String uri = request.getRequestURI();
        int limit = getLimitForUri(uri);
        // /login → 5, /register → 3, /payment-api → 3, else → 10

        // STEP C — Build the Redis key
        String minuteWindow = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        // e.g. "2024-01-15T10:30"
        // At 10:31 this becomes "2024-01-15T10:31" — a brand new key
        // That is how the window resets: the key name changes every minute

        String redisKey = "rate_limit:" + identifier + ":" + uri + ":" + minuteWindow;
        // Full example:
        // "rate_limit:alice@gmail.com:/payment-api/customer/alice@gmail.com/order/5:2024-01-15T10:30"
        // Each user + each endpoint + each minute = independent counter

        // STEP D — Atomically increment the counter in Redis
        Long requestCount = redisTemplate.opsForValue().increment(redisKey);
        // This maps directly to the Redis command: INCR <key>
        // If key doesn't exist → Redis creates it with value 0, then increments to 1
        // If key exists → just increments
        // INCR is atomic — thread-safe even under heavy concurrent load
        // Return value = the new count after incrementing

        // STEP E — Set expiry ONLY on the first request of this window
        if (requestCount != null && requestCount == 1) {
            redisTemplate.expire(redisKey, WINDOW_MINUTES + 1, TimeUnit.MINUTES);
            // Maps to Redis command: EXPIRE <key> 120
            // Sets a 2-minute TTL (1 min window + 1 min buffer)
            // After 2 minutes Redis auto-deletes this key — no cleanup needed
            // We only set TTL when count == 1 (first hit) so we don't
            // accidentally reset the expiry and extend the window
        }

        // STEP F — Check if limit is exceeded
        if (requestCount != null && requestCount > limit) {
            writeRateLimitResponse(response);
            return;
            // IMPORTANT: return here without calling filterChain.doFilter()
            // This stops the request dead — it never reaches your controller
        }

        // STEP G — Add informational headers to allowed responses
        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining",
                String.valueOf(Math.max(0, limit - requestCount)));
        response.setHeader("X-RateLimit-Window", WINDOW_MINUTES + "m");
        // These headers let your Angular frontend read:
        // "user has 2 payment attempts remaining this minute"
        // Math.max(0,...) prevents negative values when count == limit

        // STEP H — Allow the request through to the next filter/controller
        filterChain.doFilter(request, response);
        // Without this line the request would silently hang
        // This passes control to the next filter in the chain
        // Eventually reaches your @RestController methods
    }

    // -----------------------------------------------------------
    // WHO IS THIS REQUEST FROM?
    // -----------------------------------------------------------
    private String resolveIdentifier(HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // SecurityContextHolder was populated by JwtAuthenticationFilter
        // which runs before this filter in the chain

        if (auth != null
                && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
            // auth.getName() returns the JWT subject = email address
            // e.g. "alice@gmail.com"
            // "anonymousUser" is what Spring sets when no JWT was provided
        }

        // No valid JWT → unauthenticated request (login, register)
        // Fall back to IP address
        String ip = request.getHeader("X-Forwarded-For");
        // X-Forwarded-For is set by reverse proxies (Nginx, AWS ALB)
        // It contains the real client IP that the proxy received
        // Without this check, getRemoteAddr() would return the proxy's IP
        // and every user would share the same rate limit bucket

        return (ip != null && !ip.isBlank())
                ? ip.split(",")[0].trim()
                // X-Forwarded-For can be "clientIP, proxy1IP, proxy2IP"
                // Take only the first one — that's the original client
                : request.getRemoteAddr();
                // Direct connection (no proxy) → use socket IP directly
    }

    // -----------------------------------------------------------
    // WHAT LIMIT APPLIES TO THIS ENDPOINT?
    // -----------------------------------------------------------
    private int getLimitForUri(String uri) {

        if (uri.contains("/customer-api/login")) {
            return 5;
            // Brute-force password guessing protection
            // 5 wrong passwords/min is enough for legitimate typos
            // but makes dictionary attacks take years
        }

        if (uri.contains("/customer-api/register")) {
            return 3;
            // Prevents bots from mass-creating fake accounts
        }

        if (uri.contains("/payment-api/")) {
            return 3;
            // CVV is only 3 digits (000-999)
            // Without limiting, all 1000 values could be tried in seconds
            // With 3/min it takes 5+ hours — plus your DB logs every failure
        }

        if (uri.contains("/order-api/") && uri.contains("place-order")) {
            return 5;
            // Prevents inventory drain via automated order placement
        }

        return DEFAULT_LIMIT;
        // 10/min for: browsing products, cart operations,
        // viewing orders, updating profile, changing password
    }

    // -----------------------------------------------------------
    // WRITE THE 429 RESPONSE
    // -----------------------------------------------------------
    private void writeRateLimitResponse(HttpServletResponse response)
            throws IOException {

        ErrorInfo error = new ErrorInfo();
        // Your existing ErrorInfo class — already has errorMessage,
        // errorCode, and timestamp fields with getters/setters

        error.setErrorMessage("Too many requests. Please try again after 1 minute.");
        error.setErrorCode(HttpStatus.TOO_MANY_REQUESTS.value());
        // HttpStatus.TOO_MANY_REQUESTS.value() = 429
        // 429 is the standard HTTP status for rate limiting

        error.setTimestamp(LocalDateTime.now());

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        // Sets the HTTP status line to "429 Too Many Requests"

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Sets Content-Type: application/json
        // Without this, the client doesn't know how to parse the body

        response.getWriter().write(objectMapper.writeValueAsString(error));
        // objectMapper.writeValueAsString(error) converts ErrorInfo to:
        // {"errorMessage":"Too many requests...","errorCode":429,"timestamp":"2024-01-15T10:30:00"}
        // Writes it directly to the HTTP response body
    }
}