package com.productmanagement.service;

import com.productmanagement.dto.ProductRequest;
import com.productmanagement.dto.ProductResponse;
import com.productmanagement.dto.UpdateProductRequest;
import com.productmanagement.entity.Category;
import com.productmanagement.entity.Inventory;
import com.productmanagement.entity.Product;
import com.productmanagement.exception.ResourceNotFoundException;
import com.productmanagement.repository.CategoryRepository;
import com.productmanagement.repository.InventoryRepository;
import com.productmanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
                          InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setEnabled(true);
        product.setCategory(category);

        product = productRepository.save(product);

        // Create inventory
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(request.getQuantity() != null ? request.getQuantity() : 0);
        inventoryRepository.save(inventory);

        return convertToResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findByEnabledTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndEnabledTrue(categoryId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        if (!product.isEnabled()) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        return convertToResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }
        if (request.getEnabled() != null) {
            product.setEnabled(request.getEnabled());
        }

        product = productRepository.save(product);

        // Update inventory if quantity is provided
        if (request.getQuantity() != null) {
            Inventory inventory = inventoryRepository.findByProductId(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + id));
            inventory.setQuantity(request.getQuantity());
            inventoryRepository.save(inventory);
        }

        return convertToResponse(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
    }

    public Integer getProductInventory(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));
        return inventory.getQuantity();
    }

    @Transactional
    public void reduceInventory(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setEnabled(product.isEnabled());
        response.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        response.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);

        Inventory inventory = inventoryRepository.findByProductId(product.getId()).orElse(null);
        response.setInventoryQuantity(inventory != null ? inventory.getQuantity() : 0);

        return response;
    }
}

