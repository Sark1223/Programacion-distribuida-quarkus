package com.pharmacy.api.resource;

import java.util.List;

import com.pharmacy.api.model.Error400BadRequest;
import com.pharmacy.api.model.Product;
import com.pharmacy.api.model.ProductPatch;
import com.pharmacy.api.model.Success;
import com.pharmacy.api.model.SuccessWithData;
import com.pharmacy.api.service.ProductsService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductsResource {
    
    @Inject
    ProductsService productsService;

    @GET
    @Path("/products")
    public Response getAllProducts() {
        System.out.println("Controller - Entrada a obtener productos");
        
        List<Product> products = productsService.getProducts();
        
        SuccessWithData response = new SuccessWithData();
        response.setCode("PRODUCTS_RETRIEVED");
        response.setMessage("Lista de productos obtenida exitosamente");
        response.setData(products);
    
        return Response.ok(response).build();
    }

    @POST
    @Path("/products")
    public Response createProduct(@Valid Product productRequest) {
        System.out.println("Controller - Producto recibido: " + productRequest);
        
        // El ExceptionMapper se encargará automáticamente de AlreadyExistsException
        Product savedProduct = productsService.createProduct(productRequest);
        
        Success response = new Success();
        response.setCode("PRODUCT_CREATED");
        response.setMessage("Producto creado exitosamente");
        response.setData(savedProduct);
    
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/products/{id}")
    public Response updateProduct(@PathParam("id") Integer id, @Valid Product productRequest) {
        System.out.println("Controller - Producto recibido: " + productRequest);

        // Validación del ID
        if (id == null || id < 1 || id > 999999) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("INVALID_PRODUCT_ID");
            errorResponse.setMessage("El ID del producto es inválido. Debe estar entre 1 y 999999.");
            errorResponse.putDetailsItem("invalidId", id);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
        
        Product updatedProduct = productsService.updateProduct(id, productRequest);
        
        if(updatedProduct == null) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("PRODUCT_NOT_FOUND");
            errorResponse.setMessage("No se encontró el producto con el ID proporcionado.");
            errorResponse.putDetailsItem("invalidId", id);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

        Success response = new Success();
        response.setCode("PRODUCT_UPDATED");
        response.setMessage("Producto actualizado exitosamente");
        response.setData(updatedProduct);
        
        return Response.ok(response).build();
    }

    @PATCH
    @Path("/products/{id}")
    public Response partiallyUpdateProduct(@PathParam("id") Integer id, @Valid ProductPatch productRequest) {
        System.out.println("Controller - Información recibida: " + productRequest);
        
        // Validación del ID
        if (id == null || id < 1 || id > 999999) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("INVALID_PRODUCT_ID");
            errorResponse.setMessage("El ID del producto es inválido. Debe estar entre 1 y 999999.");
            errorResponse.putDetailsItem("invalidId", id);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
        
        Product patchedProduct = productsService.patchProduct(id, productRequest);
        
        if(patchedProduct == null) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("PRODUCT_NOT_FOUND");
            errorResponse.setMessage("No se encontró el producto con el ID proporcionado.");
            errorResponse.putDetailsItem("invalidId", id);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

        Success response = new Success();
        response.setCode("PRODUCT_PATCHED");
        response.setMessage("Producto actualizado parcialmente exitosamente");
        response.setData(patchedProduct);
    
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/products/{id}")
    public Response deleteProduct(@PathParam("id") Integer id) {
        System.out.println("Controller - Información recibida: " + id);
        
        // Validación del ID
        if (id == null || id < 1 || id > 999999) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("INVALID_PRODUCT_ID");
            errorResponse.setMessage("El ID del producto es inválido. Debe estar entre 1 y 999999.");
            errorResponse.putDetailsItem("invalidId", id);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
        
        String deletionResult = productsService.deleteProduct(id);
        
        if(deletionResult == null) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("PRODUCT_NOT_FOUND");
            errorResponse.setMessage("No se encontró el producto con el ID proporcionado.");
            errorResponse.putDetailsItem("invalidId", id);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
        
        Success response = new Success();
        response.setCode("PRODUCT_DELETED");
        response.setMessage("Producto eliminado exitosamente");
        response.setData(deletionResult);
    
        return Response.ok(response).build();
    }
}