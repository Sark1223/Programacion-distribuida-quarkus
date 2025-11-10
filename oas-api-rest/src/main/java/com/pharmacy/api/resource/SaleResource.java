// [file name]: SaleResource.java (actualizado)
package com.pharmacy.api.resource;

import java.util.List;

import com.pharmacy.api.commons.BadRequestException;
import com.pharmacy.api.commons.JsonProcessingException;
import com.pharmacy.api.model.Sale;
import com.pharmacy.api.model.SalePatch;
import com.pharmacy.api.model.Success;
import com.pharmacy.api.model.SuccessWithData;
import com.pharmacy.api.service.SaleService;

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
public class SaleResource {

    @Inject
    SaleService saleService;

    @GET
    @Path("/sales")
    public Response getAllSales() {
        System.out.println("Controller - Obteniendo todas las ventas");

        List<Sale> sales = saleService.getSales();

        SuccessWithData response = new SuccessWithData();
        response.setCode("SALES_RETRIEVED");
        response.setMessage("Lista de ventas obtenida exitosamente");
        response.setData(sales);

        return Response.ok(response).build();
    }

    @POST
    @Path("/sales")
    public Response createSale(@Valid Sale saleRequest) {
        System.out.println("Controller - Creando venta: " + saleRequest);
        
        // Validación adicional del JSON
        validateJsonPayload(saleRequest);

        Sale savedSale = saleService.createSale(saleRequest);

        Success response = new Success();
        response.setCode("SALE_CREATED");
        response.setMessage("Venta creada exitosamente");
        response.setData(savedSale);

        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/sales/{id}")
    public Response updateSale(@PathParam("id") Integer saleId, @Valid Sale saleRequest) {
        System.out.println("Controller - Actualizando venta ID " + saleId + ": " + saleRequest);

        // Validación básica del ID
        if (saleId == null || saleId < 1 || saleId > 2147483647) {
            throw new BadRequestException("El ID de la venta es inválido. Debe estar entre 1 y 2147483647.");
        }
        
        // Validación adicional del JSON
        validateJsonPayload(saleRequest);

        Sale updatedSale = saleService.updateSale(saleId, saleRequest);

        Success response = new Success();
        response.setCode("SALE_UPDATED");
        response.setMessage("Venta actualizada exitosamente");
        response.setData(updatedSale);

        return Response.ok(response).build();
    }

    @PATCH
    @Path("/sales/{saleId}")
    public Response partiallyUpdateSale(@PathParam("saleId") Integer saleId, @Valid SalePatch saleRequest) {
        System.out.println("Controller - Actualizando parcialmente venta ID " + saleId + ": " + saleRequest);

        // Validación básica del ID
        if (saleId == null || saleId < 1 || saleId > 2147483647) {
            throw new BadRequestException("El ID de la venta es inválido. Debe estar entre 1 y 2147483647.");
        }
        
        // Validación adicional del JSON
        validateJsonPayload(saleRequest);

        Sale patchedSale = saleService.patchSale(saleId, saleRequest);

        Success response = new Success();
        response.setCode("SALE_PATCHED");
        response.setMessage("Venta actualizada parcialmente con éxito");
        response.setData(patchedSale);

        return Response.ok(response).build();
    }

    @DELETE
    @Path("/sales/{saleId}")
    public Response deleteSale(@PathParam("saleId") Integer saleId) {
        System.out.println("Controller - Eliminando venta ID: " + saleId);

        // Validación básica del ID
        if (saleId == null || saleId < 1 || saleId > 2147483647) {
            throw new BadRequestException("El ID de la venta es inválido. Debe estar entre 1 y 2147483647.");
        }

        String deletionResult = saleService.deleteSale(saleId);

        Success response = new Success();
        response.setCode("SALE_DELETED");
        response.setMessage("Venta eliminada exitosamente");
        response.setData(deletionResult);

        return Response.ok(response).build();
    }
    
    /**
     * Método auxiliar para validaciones adicionales del payload JSON
     */
    private void validateJsonPayload(Object payload) {
        if (payload == null) {
            throw new JsonProcessingException("El cuerpo de la solicitud no puede estar vacío");
        }
        
        // Aquí puedes agregar más validaciones específicas según tus necesidades
        // Por ejemplo, validar que ciertos campos no sean nulos o tengan formatos específicos
    }
}