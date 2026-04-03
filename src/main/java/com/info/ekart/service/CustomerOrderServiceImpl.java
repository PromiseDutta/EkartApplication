
package com.info.ekart.service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.info.ekart.dto.OrderStatus;
import com.info.ekart.dto.PaymentThrough;
import com.info.ekart.dto.request.PlaceOrderRequestDTO;
import com.info.ekart.dto.response.OrderResponseDTO;
import com.info.ekart.dto.response.OrderedItemResponseDTO;
import com.info.ekart.dto.response.ProductResponseDTO;
import com.info.ekart.entity.CartProduct;
import com.info.ekart.entity.Customer;
import com.info.ekart.entity.CustomerCart;
import com.info.ekart.entity.Order;
import com.info.ekart.entity.OrderedProduct;
import com.info.ekart.entity.Product;
import com.info.ekart.exception.EKartException;
import com.info.ekart.repository.CartRepository;
import com.info.ekart.repository.CustomerRepository;
import com.info.ekart.repository.OrderRepository;
import com.info.ekart.repository.ProductRepository;

@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

    
    private  final OrderRepository orderRepository;

    
    private  final CartRepository cartRepository;

    
    private  final ProductRepository productRepository;

    
    private  final  CustomerRepository customerRepository;


    public CustomerOrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository,
			ProductRepository productRepository, CustomerRepository customerRepository) {
		super();
		this.orderRepository = orderRepository;
		this.cartRepository = cartRepository;
		this.productRepository = productRepository;
		this.customerRepository = customerRepository;
	}


	@Override
    public Long placeOrder(String customerEmailId,
                           PlaceOrderRequestDTO request)
            throws EKartException {

        // ============================================================
        // 1️⃣ Validate Customer
        // ============================================================
        // - Check if customer exists
        // - Ensure shipping address is available
        // ============================================================

        Customer customer = customerRepository.findById(customerEmailId)
                .orElseThrow(() ->
                        new EKartException("CustomerService.CUSTOMER_NOT_FOUND"));

        if (customer.getAddress() == null || customer.getAddress().isBlank()) {
            throw new EKartException("OrderService.ADDRESS_NOT_AVAILABLE");
        }


        // ============================================================
        // 2️⃣ Fetch Customer Cart
        // ============================================================
        // - Ensure cart exists
        // - Ensure cart contains products
        // ============================================================

        CustomerCart cart = cartRepository.findByCustomerEmailId(customerEmailId)
                .orElseThrow(() ->
                        new EKartException("CustomerCartService.NO_CART_FOUND"));

        if (cart.getCartProducts().isEmpty()) {
            throw new EKartException("CustomerCartService.NO_PRODUCT_ADDED_TO_CART");
        }


        // ============================================================
        // 3️⃣ Initialize Order Entity
        // ============================================================
        // - Create new order
        // - Set order metadata (date, delivery, payment mode)
        // - Set initial status
        // ============================================================

        Order order = new Order();
        order.setCustomer(customer);
        order.setDateOfOrder(LocalDateTime.now());
        order.setDateOfDelivery(request.getDateOfDelivery());
        order.setPaymentThrough(request.getPaymentThrough());
        order.setOrderStatus(OrderStatus.PLACED);
        order.setDeliveryAddress(customer.getAddress());

        BigDecimal total = BigDecimal.ZERO;
        List<OrderedProduct> orderedProducts = new ArrayList<>();


        // ============================================================
        // 4️⃣ Convert Cart Items → Ordered Products
        // ============================================================
        // For each product in cart:
        // - Validate stock availability
        // - Reduce product stock
        // - Create OrderedProduct entity
        // - Calculate running total
        // ============================================================

        for (CartProduct cartProduct : cart.getCartProducts()) {

            Product product = cartProduct.getProduct();

            // 🔹 Check stock availability
            if (product.getAvailableQuantity() < cartProduct.getQuantity()) {
                throw new EKartException("OrderService.INSUFFICIENT_STOCK");
            }

            // 🔹 Reduce available stock
            product.setAvailableQuantity(
                    product.getAvailableQuantity() - cartProduct.getQuantity());
            productRepository.save(product);

            // 🔹 Create ordered product entry
            OrderedProduct orderedProduct = new OrderedProduct();
            orderedProduct.setProduct(product);
            orderedProduct.setQuantity(cartProduct.getQuantity());
            orderedProduct.setOrder(order);

            orderedProducts.add(orderedProduct);

            // 🔹 Calculate item total
            BigDecimal itemTotal = BigDecimal.valueOf(product.getPrice())
                    .multiply(BigDecimal.valueOf(cartProduct.getQuantity()));

            total = total.add(itemTotal);
        }

        // Attach ordered products to order
        order.setOrderedProducts(orderedProducts);


        // ============================================================
        // 5️⃣ Apply Discount Based on Payment Method
        // ============================================================
        // - Credit Card → 10% discount
        // - Other Payment → 5% discount
        // - Calculate final payable amount
        // ============================================================

        BigDecimal discountPercent;

        if (order.getPaymentThrough() == PaymentThrough.CREDIT_CARD) {
            discountPercent = BigDecimal.valueOf(10);
        } else {
            discountPercent = BigDecimal.valueOf(5);
        }

        order.setDiscount(discountPercent);

        BigDecimal finalAmount = total
                .multiply(BigDecimal.valueOf(100).subtract(discountPercent))
                .divide(BigDecimal.valueOf(100));

        order.setTotalPrice(finalAmount);


        // ============================================================
        // 6️⃣ Persist Order to Database
        // ============================================================
        // - Inserts into EK_ORDER
        // - Inserts into EK_ORDERED_PRODUCT (via cascade)
        // ============================================================

        orderRepository.save(order);


        // ============================================================
        // 7️⃣ Clear Cart After Successful Order Placement
        // ============================================================
        // - Remove all cart items
        // - Persist cart update
        // ============================================================

        cart.getCartProducts().clear();
        cartRepository.save(cart);


        // ============================================================
        // 8️⃣ Return Generated Order ID
        // ============================================================

        return order.getOrderId();
    }


    @Override
    public OrderResponseDTO getOrderDetails(Long orderId)
            throws EKartException {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EKartException("OrderService.ORDER_NOT_FOUND"));

        return convertToResponse(order);
    }

    @Override
    public List<OrderResponseDTO> findOrdersByCustomerEmailId(String emailId)
            throws EKartException {

        List<Order> orders = orderRepository.findByCustomer_EmailId(emailId);

        if (orders.isEmpty()) {
            throw new EKartException("OrderService.NO_ORDERS_FOUND");
        }

        return orders.stream()
                .map(this::convertToResponse)
                .toList();
    }

    // 🔄 Mapper
    private OrderResponseDTO convertToResponse(Order order) {

        OrderResponseDTO dto = new OrderResponseDTO();

        dto.setOrderId(order.getOrderId());
        
        dto.setDateOfOrder(order.getDateOfOrder());
        dto.setDateOfDelivery(order.getDateOfDelivery());
        dto.setOrderStatus(order.getOrderStatus().toString());
        dto.setPaymentThrough(order.getPaymentThrough().toString());
        dto.setDiscount(order.getDiscount());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setDeliveryAddress(order.getDeliveryAddress());

        List<OrderedItemResponseDTO> items = new ArrayList<>();

        for (OrderedProduct op : order.getOrderedProducts()) {

            OrderedItemResponseDTO item = new OrderedItemResponseDTO();
            item.setOrderedProductId(op.getOrderedProductId());
            item.setQuantity(op.getQuantity());

            ProductResponseDTO productDTO = new ProductResponseDTO();
            productDTO.setProductId(op.getProduct().getProductId());
            productDTO.setName(op.getProduct().getName());
            productDTO.setBrand(op.getProduct().getBrand());
            productDTO.setPrice(op.getProduct().getPrice());

            item.setProduct(productDTO);

            items.add(item);
        }

        dto.setItems(items);



        return dto;
    }
}
