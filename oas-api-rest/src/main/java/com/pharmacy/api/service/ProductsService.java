package com.pharmacy.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.pharmacy.api.commons.AlreadyExistsException;
import com.pharmacy.api.model.Product;
import com.pharmacy.api.model.Product.ProductTypeEnum;
import com.pharmacy.api.model.ProductPatch;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductsService {

    private List<Product> products = new ArrayList<>(Arrays.asList(
        new Product(1, ProductTypeEnum.GENERICO, "Paracetamol", 50.50),
        new Product(55, ProductTypeEnum.GENERICO, "Jarabe Broncolin", 130.22),
        new Product(302, ProductTypeEnum.PRESCRITO, "Amoxicilina", 160.00),
        new Product(2, ProductTypeEnum.GENERICO, "Ibuprofeno", 50.50)
    ));

    public List<Product> getProducts() {
        System.out.println("Service - Buscando todos los productos...");
        return products;
    }
    
    public Product createProduct(Product product) {
        System.out.println("Service - Creando producto: " + product);
        
        boolean idExists = products.stream()
                .anyMatch(p -> p.getProductId().equals(product.getProductId()));
        if (idExists) {
            // Usar AlreadyExistsException 
            throw new AlreadyExistsException("El ID del producto ya existe: " + product.getProductId());
        }

        // Guardar en la base de datos
        Product newProduct = new Product(
                product.getProductId(), 
                product.getProductType(), 
                product.getName(), 
                product.getPrice());
        newProduct.setDescription(product.getDescription()); 
        products.add(newProduct);
        return newProduct; // Retornar el nuevo producto creado
    }

    public Product updateProduct(Integer id, Product product) {
        System.out.println("Service - Actualizando producto ID " + id + ": " + product);
        
        // Buscar si el producto existe
        Optional<Product> existingProductOpt = products.stream()
            .filter(p -> p.getProductId().equals(id))
            .findFirst();
            
        if (existingProductOpt.isPresent()) {
            // Actualizar el producto
            Product existingProduct = existingProductOpt.get();
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setProductType(product.getProductType());
            return existingProduct; // Retornar el producto actualizado
        } else {
            // Si no existe retornar un exception
            throw new AlreadyExistsException("El producto con ID no fue encontrado: " + product.getProductId());
        }
    }

    public Product patchProduct(Integer id, ProductPatch productPatch) {
        System.out.println("Service - Actualizando parcialmente producto ID " + id + ": " + productPatch);
        
        // Buscar el producto existente
        Optional<Product> existingProductOpt = products.stream()
            .filter(p -> p.getProductId().equals(id))
            .findFirst();
            
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();
            
            // Aplicar los cambios del patch
            if (productPatch.getPrice() != null) {
                existingProduct.setPrice(productPatch.getPrice());
            }
            if (productPatch.getDescription() != null) {
                existingProduct.setDescription(productPatch.getDescription());
            }
            
            return existingProduct;
        }
        // Si no existe, retornar un exception
        throw new AlreadyExistsException("El producto con ID no fue encontrado: " + id);
    }

    public String deleteProduct(Integer id) {
        System.out.println("Service - Eliminando producto ID: " + id);
        
        // Buscar si el producto existe y eliminarlo
        boolean removed = products.removeIf(p -> p.getProductId().equals(id));
        
        if (removed) {
            return "Producto con ID " + id + " eliminado correctamente";
        } else {
            throw new AlreadyExistsException("El producto con ID no fue encontrado: " + id);
        }
    }
}