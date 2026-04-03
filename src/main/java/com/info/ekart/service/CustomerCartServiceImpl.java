package com.info.ekart.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.info.ekart.dto.request.AddCartItemRequestDTO;
import com.info.ekart.dto.response.CartItemResponseDTO;
import com.info.ekart.dto.response.CartResponseDTO;
import com.info.ekart.dto.response.ProductResponseDTO;
import com.info.ekart.entity.CartProduct;
import com.info.ekart.entity.Customer;
import com.info.ekart.entity.CustomerCart;
import com.info.ekart.entity.Product;
import com.info.ekart.exception.EKartException;
import com.info.ekart.repository.CartProductRepository;
import com.info.ekart.repository.CartRepository;
import com.info.ekart.repository.CustomerRepository;
import com.info.ekart.repository.ProductRepository;

import jakarta.transaction.Transactional;
@Transactional
@Service
public class CustomerCartServiceImpl implements CustomerCartService {

    
    private  final CartRepository cartRepository;

    
    private  final CartProductRepository cartProductRepository;

    
    private  final ProductRepository productRepository;
    
    
    private  final CustomerRepository customerRepository;

   
    
    
    
    public CustomerCartServiceImpl(CartRepository cartRepository, CartProductRepository cartProductRepository,
			ProductRepository productRepository, CustomerRepository customerRepository) {
		super();
		this.cartRepository = cartRepository;
		this.cartProductRepository = cartProductRepository;
		this.productRepository = productRepository;
		this.customerRepository = customerRepository;
	}




	@Override
    public Integer addProductToCart(String customerEmailId, AddCartItemRequestDTO request)
            throws EKartException {

        // 1️⃣ Validate customer
        Customer customer = customerRepository.findById(customerEmailId.toLowerCase())
                .orElseThrow(() -> new EKartException("CustomerService.CUSTOMER_NOT_FOUND"));

        // 2️⃣ Validate product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EKartException("ProductService.PRODUCT_NOT_AVAILABLE"));

        // 3️⃣ Fetch existing cart or create new
        CustomerCart cart = cartRepository.findByCustomerEmailId(customerEmailId)
                .orElseGet(() -> {
                    CustomerCart newCart = new CustomerCart();
                    newCart.setCustomer(customer);
                    return cartRepository.save(newCart);
                });

        // 4️⃣ Check if product already exists in cart, if already there just increase the quantity
        for (CartProduct cp : cart.getCartProducts()) {
            if (cp.getProduct().getProductId().equals(request.getProductId())) {
                cp.setQuantity(cp.getQuantity() + request.getQuantity());
                cartRepository.save(cart);
                return cart.getCartId();
            }
        }

        // 5️⃣ Add new product to cart
        CartProduct newCartProduct = new CartProduct();
        newCartProduct.setProduct(product);
        newCartProduct.setQuantity(request.getQuantity());
        newCartProduct.setCart(cart);

        cart.getCartProducts().add(newCartProduct);

        cartRepository.save(cart);

        return cart.getCartId();
    }

    
    
    
    
    


    // 📦 Get Cart
    @Override
    public CartResponseDTO getProductsFromCart(String customerEmailId) throws EKartException {

        CustomerCart cart = cartRepository.findByCustomerEmailId(customerEmailId)
                .orElseThrow(() -> new EKartException("CustomerCartService.NO_CART_FOUND"));

        if (cart.getCartProducts().isEmpty()) {
            throw new EKartException("CustomerCartService.NO_PRODUCT_ADDED_TO_CART");
        }

        List<CartItemResponseDTO> items = cart.getCartProducts()
                .stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());

        // 🔥 Calculate totalAmount
        BigDecimal totalAmount = cart.getCartProducts()
                .stream()
                .map(cp -> BigDecimal.valueOf(cp.getProduct().getPrice())
                        .multiply(BigDecimal.valueOf(cp.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartResponseDTO response = new CartResponseDTO();
        response.setCartId(cart.getCartId());
        response.setItems(items);
        response.setTotalAmount(totalAmount);

        return response;
    }


    // ✏ Modify Quantity
    @Override
    public void modifyQuantityOfProductInCart(String customerEmailId, Integer productId, Integer quantity)
            throws EKartException {

        CustomerCart cart = cartRepository.findByCustomerEmailId(customerEmailId)
                .orElseThrow(() -> new EKartException("CustomerCartService.NO_CART_FOUND"));

        CartProduct cartProduct = cart.getCartProducts()
                .stream()
                .filter(cp -> cp.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new EKartException("CustomerCartService.PRODUCT_NOT_AVAILABLE"));

        cartProduct.setQuantity(quantity);
        cartRepository.save(cart);
    }

    // ❌ Delete Single Product
    @Override
    public void deleteProductFromCart(String customerEmailId, Integer productId) throws EKartException {

        CustomerCart cart = cartRepository.findByCustomerEmailId(customerEmailId)
                .orElseThrow(() -> new EKartException("CustomerCartService.NO_CART_FOUND"));

        CartProduct cartProduct = cart.getCartProducts()
                .stream()
                .filter(cp -> cp.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new EKartException("CustomerCartService.PRODUCT_ALREADY_NOT_AVAILABLE"));

//        cart.getCartProducts().remove(cartProduct); //this will not delete the record from database
//        cartRepository.save(cart);
        
        
        /*Since orphanRemoval = true is NOT present in CustomerCart for cartProducts
And you're only removing from collection,The row in ek_cart_product table is NOT deleted
orphanRemoval = true will work 

@OneToMany(mappedBy = "cart",
           cascade = CascadeType.ALL,
           orphanRemoval = true,
           fetch = FetchType.LAZY)
private Set<CartProduct> cartProducts;    Then this works: cart.getCartProducts().remove(cartProduct);

For clarity and safety:
👉 Use cartProductRepository.delete(cartProduct);
👉 Keep logic explicit
👉 Avoid relying on orphanRemoval unless intentional
         * */
        
     // 🔥 Remove from parent collection first
        cart.getCartProducts().remove(cartProduct);  

        // 🔥 Then delete explicitly
        cartProductRepository.delete(cartProduct); // if we only do this, the deletion will not persist it will resaved by parent class
/*        Parent still holds reference
//But child is marked for delete,This inconsistency can prevent proper delete or cause weird behavior.       
Imagine:
Cart says:
"I own these 3 products"
Then you secretly delete one product behind its back.
Cart still thinks:
"I own 3 products"
That inconsistency must be fixed.
So you update cart's collection. cart.getCartProducts().remove(cartProduct);  
    */    
    }

    // ❌ Delete All
    @Override
    public void deleteAllProductsFromCart(String customerEmailId) throws EKartException {

        CustomerCart cart = cartRepository.findByCustomerEmailId(customerEmailId)
                .orElseThrow(() -> new EKartException("CustomerCartService.NO_CART_FOUND"));

        if (cart.getCartProducts().isEmpty()) {
            throw new EKartException("CustomerCartService.NO_PRODUCT_ADDED_TO_CART");
        }

//        cart.getCartProducts().clear();
//        cartRepository.save(cart);
        
     // 1️⃣ Delete from DB
        cartProductRepository.deleteAll(cart.getCartProducts());

        // 2️⃣ Clear parent collection
        cart.getCartProducts().clear();

    }

    // 🔄 Mapper
    private CartItemResponseDTO convertToCartItemDTO(CartProduct cp) {

        Product product = cp.getProduct();

        ProductResponseDTO productDTO = new ProductResponseDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setCategory(product.getCategory());
        productDTO.setBrand(product.getBrand());
        productDTO.setPrice(product.getPrice());
        productDTO.setAvailableQuantity(product.getAvailableQuantity());

        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setCartProductId(cp.getCartProductId());
        dto.setProduct(productDTO);
        dto.setQuantity(cp.getQuantity());

        return dto;
    }
}
