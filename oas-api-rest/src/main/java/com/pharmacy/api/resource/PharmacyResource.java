package com.pharmacy.api.resource;

import java.util.List;

import com.pharmacy.api.commons.BadRequestException;
import com.pharmacy.api.model.Pharmacy;
import com.pharmacy.api.model.PharmacyPatch;
import com.pharmacy.api.model.Sale;
import com.pharmacy.api.model.Success;
import com.pharmacy.api.model.SuccessWithData;
import com.pharmacy.api.service.PharmacyService;

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
public class PharmacyResource {

    @Inject
    PharmacyService pharmacyService;

    @GET
    @Path("/pharmacys")
    public Response getAllPharmacys() {
        System.out.println("Controller - Obteniendo todas las farmacias");
        
        List<Pharmacy> pharmacys = pharmacyService.getPharmacys();
        
        SuccessWithData response = new SuccessWithData();
        response.setCode("PHARMACYS_RETRIEVED");
        response.setMessage("Lista de farmacias obtenida exitosamente");
        response.setData(pharmacys);
    
        return Response.ok(response).build();
    }

    @GET
    @Path("/pharmacys/{idPharmacy}")
    public Response getPharmacy(@PathParam("idPharmacy") Integer idPharmacy) {
        System.out.println("Controller - Obteniendo farmacia ID: " + idPharmacy);
        
        // Validación básica del ID
        if (idPharmacy == null || idPharmacy < 1 || idPharmacy > 2147483647) {
            throw new BadRequestException("El ID de la farmacia es inválido. Debe estar entre 1 y 2147483647.");
        }
        
        Pharmacy pharmacy = pharmacyService.getPharmacyById(idPharmacy);
        
        SuccessWithData response = new SuccessWithData();
        response.setCode("PHARMACY_RETRIEVED");
        response.setMessage("Información de la farmacia obtenida exitosamente");
        response.setData(pharmacy);
    
        return Response.ok(response).build();
    }

    @GET
	@Path("/pharmacys/{idPharmacy}/sales")
	public Response getSalesByPharmacy(@PathParam("idPharmacy") Integer idPharmacy) {
		System.out.println("Controller - Obteniendo ventas por farmacia ID: " + idPharmacy);

		// Validación básica del ID
		if (idPharmacy == null || idPharmacy < 1 || idPharmacy > 2147483647) {
			throw new BadRequestException("El ID de la farmacia es inválido. Debe estar entre 1 y 2147483647.");
		}

		List<Sale> sales = pharmacyService.getSalesByPharmacy(idPharmacy);

		SuccessWithData response = new SuccessWithData();
		response.setCode("SALES_BY_PHARMACY_RETRIEVED");
		response.setMessage("Ventas por farmacia obtenidas exitosamente");
		response.setData(sales);

		return Response.ok(response).build();
	}

    @POST
    @Path("/pharmacys")
    public Response createPharmacy(@Valid Pharmacy pharmacyRequest) {
        System.out.println("Controller - Creando farmacia: " + pharmacyRequest);

        // Las validaciones y excepciones son manejadas automáticamente por el GlobalExceptionMapper
        Pharmacy savedPharmacy = pharmacyService.createPharmacy(pharmacyRequest);
        
        Success response = new Success();
        response.setCode("PHARMACY_CREATED");
        response.setMessage("Farmacia creada exitosamente");
        response.setData(savedPharmacy);
    
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/pharmacys/{idPharmacy}")
    public Response updatePharmacy(@PathParam("idPharmacy") Integer idPharmacy, @Valid Pharmacy pharmacyRequest) {
        System.out.println("Controller - Actualizando farmacia ID " + idPharmacy + ": " + pharmacyRequest);

        // Validación básica del ID
        if (idPharmacy == null || idPharmacy < 1 || idPharmacy > 2147483647) {
            throw new BadRequestException("El ID de la farmacia es inválido. Debe estar entre 1 y 2147483647.");
        }
        
        Pharmacy updatedPharmacy = pharmacyService.updatePharmacy(idPharmacy, pharmacyRequest);

        Success response = new Success();
        response.setCode("PHARMACY_UPDATED");
        response.setMessage("Farmacia actualizada exitosamente");
        response.setData(updatedPharmacy);
        
        return Response.ok(response).build();
    }

    @PATCH
    @Path("/pharmacys/{idPharmacy}")
    public Response partiallyUpdatePharmacy(@PathParam("idPharmacy") Integer idPharmacy, @Valid PharmacyPatch pharmacyRequest) {
        System.out.println("Controller - Actualizando parcialmente farmacia ID " + idPharmacy + ": " + pharmacyRequest);
        
        // Validación básica del ID
        if (idPharmacy == null || idPharmacy < 1 || idPharmacy > 2147483647) {
            throw new BadRequestException("El ID de la farmacia es inválido. Debe estar entre 1 y 2147483647.");
        }
        
        Pharmacy patchedPharmacy = pharmacyService.patchPharmacy(idPharmacy, pharmacyRequest);

        Success response = new Success();
        response.setCode("PHARMACY_PATCHED");
        response.setMessage("Farmacia actualizada parcialmente con éxito");
        response.setData(patchedPharmacy);
    
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/pharmacys/{idPharmacy}")
    public Response deletePharmacy(@PathParam("idPharmacy") Integer idPharmacy) {
        System.out.println("Controller - Eliminando farmacia ID: " + idPharmacy);
        
        // Validación básica del ID
        if (idPharmacy == null || idPharmacy < 1 || idPharmacy > 2147483647) {
            throw new BadRequestException("El ID de la farmacia es inválido. Debe estar entre 1 y 2147483647.");
        }
        
        String deletionResult = pharmacyService.deletePharmacy(idPharmacy);
        
        Success response = new Success();
        response.setCode("PHARMACY_DELETED");
        response.setMessage("Farmacia eliminada exitosamente");
        response.setData(deletionResult);
    
        return Response.ok(response).build();
    }
}