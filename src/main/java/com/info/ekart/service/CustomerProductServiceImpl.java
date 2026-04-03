package com.info.ekart.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.info.ekart.dto.request.ProductCreateRequestDTO;
import com.info.ekart.dto.response.ProductResponseDTO;
import com.info.ekart.entity.Product;
import com.info.ekart.exception.EKartException;
import com.info.ekart.repository.ProductRepository;

@Service
public class CustomerProductServiceImpl implements CustomerProductService {

	
	private final  ProductRepository productRepository;
	

	public CustomerProductServiceImpl(ProductRepository productRepository) {
		super();
		this.productRepository = productRepository;
	}

	// Get all products
	@Override
	@Cacheable(value = "allProducts")
	public List<ProductResponseDTO> getAllProducts() throws EKartException {

		List<Product> products = productRepository.findAll();

		if (products.isEmpty()) {
			throw new EKartException("ProductService.NO_PRODUCTS_AVAILABLE");
		}

		return products.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
	}

	// Get product by ID
	@Override
	@Cacheable(value = "products", key = "#productId")
	public ProductResponseDTO getProductById(Integer productId) throws EKartException {

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new EKartException("ProductService.PRODUCT_NOT_AVAILABLE"));

		return convertToResponseDTO(product);
	}

	// Reduce quantity after order placement
	@Override

	@Caching(evict = {
			@CacheEvict(value = "products", key = "#productId"),
			@CacheEvict(value = "allProducts", allEntries = true) 
			})
	public void reduceAvailableQuantity(Integer productId, Integer quantity) throws EKartException {

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new EKartException("ProductService.PRODUCT_NOT_AVAILABLE"));

		if (product.getAvailableQuantity() < quantity) {
			throw new EKartException("ProductService.INSUFFICIENT_STOCK");
		}

		product.setAvailableQuantity(product.getAvailableQuantity() - quantity);

		productRepository.save(product);
	}

	@Override
	public ProductResponseDTO addProduct(ProductCreateRequestDTO request) throws EKartException {

		Product product = new Product();

		product.setName(request.getName());
		product.setAvailableQuantity(request.getAvailableQuantity());
		product.setBrand(request.getBrand());
		product.setCategory(request.getCategory());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());

		Product savedProduct = productRepository.save(product);

		return convertToResponseDTO(savedProduct);
	}



	@Override
	@Caching(evict = {
			@CacheEvict(value = "products", key = "#productId"),
			@CacheEvict(value = "allProducts", allEntries = true) 
			})
	public void deleteProduct(Integer productId) throws EKartException {
		// TODO Auto-generated method stub
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new EKartException("ProductService.PRODUCT_NOT_AVAILABLE"));
		productRepository.delete(product);

	}
	
	
	// 🔥 Centralized Mapper
	private ProductResponseDTO convertToResponseDTO(Product product) {

		ProductResponseDTO dto = new ProductResponseDTO();

		dto.setProductId(product.getProductId());
		dto.setName(product.getName());
		dto.setDescription(product.getDescription());
		dto.setCategory(product.getCategory());
		dto.setBrand(product.getBrand());
		dto.setPrice(product.getPrice());
		dto.setAvailableQuantity(product.getAvailableQuantity());

		return dto;
	}
}
