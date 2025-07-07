package com.example.store.controllers;

import com.example.store.dtos.ProductDto;
import com.example.store.entities.Product;
import com.example.store.mappers.ProductMapper;
import com.example.store.repositories.CategoryRepository;
import com.example.store.repositories.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "Products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    //! Get all products
    @Operation(summary = "Get all products", description = "Returns a list of all products in the system, optionally filtered by category ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Successfully retrieved the list of products",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class)
                            )
                    )
            }
    )
    @GetMapping
    public List<ProductDto> getAllProducts (
            @RequestParam(required = false, name = "categoryId") Byte categoryId
    ) {
        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else {
            products = productRepository.findAllWithCategory();
        }
        return products.stream().map(productMapper :: toDto).toList();
    }

    //! Get product by category ID
    @Operation(summary = "Get product by category ID", description = "Returns a product by category ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Successfully retrieved the product",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Product not found"
                    )
            }
    )
    @GetMapping("/{categoryId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long categoryId) {
        var product = productRepository.findById(categoryId).orElse(null);
        if(product == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(productMapper.toDto(product));
    }

    //! Create a new product
    @Operation(summary = "Create a new product", description = "Creates a new product in the system.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201", description = "Successfully created the product",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Bad request, category not found"
                    )
            }
    )
    @PostMapping("/create")
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto productDto,
            UriComponentsBuilder uriBuilder
    ) {
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        var product = productMapper.toEntity(productDto);
        product.setCategory(category);
        productRepository.save(product);
        productDto.setId(product.getId());

        var uri = uriBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri();

        return ResponseEntity.created(uri).body(productDto);
    }

    //! Update an existing product
    @Operation(summary = "Update an existing product", description = "Updates the details of an existing product by ID.")
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductDto productDto
    ) {
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);
        if(category == null) {
            return ResponseEntity.badRequest().build();
        }

        var product = productRepository.findById(productId).orElse(null);
        if(product == null) {
            return ResponseEntity.notFound().build();
        }

        productMapper.update(productDto, product);
        product.setCategory(category);
        productRepository.save(product);
        productDto.setId(product.getId());

        return ResponseEntity.ok(productDto);
    }

    //! Delete a product
    @Operation(summary = "Delete a product", description = "Deletes a product by ID.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204", description = "Successfully deleted the product"
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Product not found"
                    )
            }
    )
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }
}
