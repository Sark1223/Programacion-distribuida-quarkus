package com.pharmacy.api.resource;

import com.pharmacy.api.commons.AlreadyExistsException;
import com.pharmacy.api.model.Employe;
import com.pharmacy.api.model.EmployePatch;
import com.pharmacy.api.model.Error400BadRequest;
import com.pharmacy.api.model.Error404NotFound;
import com.pharmacy.api.model.Product;
import com.pharmacy.api.model.Success;
import com.pharmacy.api.model.SuccessWithData;
import com.pharmacy.api.service.EmployesService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployesResource {

    @Inject
    EmployesService employeService;


    // 1. --- GET /employes : Obtener todos los empleados (200 OK) ---
    @GET
    @Path("/employes")
    public Response getEmployes() {
        System.out.println("Resource - Solicitud GET: obtener todos los empleados.");
        List<Employe> employes = employeService.getEmployes();
        
        SuccessWithData response = new SuccessWithData();
        response.setCode("EMPLOYEES_RETRIEVED");
        response.setMessage("Lista de empleados obtenida exitosamente");
        response.setData(employes);
    
        return Response.ok(response).build();
    }
    
    // 2. --- GET /employes/{idEmploye} : Obtener un empleado por ID (200 OK / 404 NOT_FOUND) ---
    @GET
    @Path("/employes/{id}")
    public Response getEmploye(@PathParam("id") Integer idEmploye) {
        System.out.println("Resource - Solicitud GET: obtener empleado por ID: " + idEmploye);
            Employe employe = employeService.getEmploye(idEmploye);
            
            if(employe == null) {
                Error404NotFound errorResponse = new Error404NotFound();
                errorResponse.setCode("PRODUCT_NOT_FOUND");
                errorResponse.setMessage("No se encontró el producto con el ID proporcionado.");
                errorResponse.putDetailsItem("Invalid",idEmploye);
                return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
            }

            Success response = new Success();
            response.setCode("EMPLOYE_RETRIEVED");
            response.setMessage("Información del empleado obtenida exitosamente");
            response.setData(employe);
            
            return Response.ok(response).status(Status.OK).build();
            
    }

    // 3. --- POST /employes : Crear nuevo empleado (201 CREATED / 400 BAD_REQUEST) ---
    @POST
    @Path("/employes")
    public Response createEmploye(@Valid Employe employeRequest) {
        System.out.println("Resource - Solicitud POST: crear nuevo empleado.");
            Employe savedEmploye = employeService.createEmploye(employeRequest);

            Success response = new Success();
            response.setCode("EMPLOYE_CREATED");
            response.setMessage("Empleado registrado exitosamente");
            response.setData(savedEmploye);
    
        return Response.status(Response.Status.CREATED).entity(response).build();
 
    }

    // 4. --- PUT /employes/{idEmploye} : Actualizar todos los datos (201 CREATED / 404 NOT_FOUND / 400 BAD_REQUEST) ---
    @PUT
    @Path("/employes/{id}")
    public Response updateEmploye(@PathParam("id") Integer idEmploye, @Valid Employe employeRequest) {
        System.out.println("Resource - Solicitud PUT: actualizar empleado ID: " + idEmploye);
        
        if (idEmploye == null || idEmploye < 1 || idEmploye > 2147483647) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado es inválido. Debe estar entre 1 y 2147483647.");
            errorResponse.putDetailsItem("invalidId", idEmploye);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

       Employe updateEmploye = employeService.updateEmploye(idEmploye, employeRequest);

        if (updateEmploye == null) {
            Error404NotFound errorResponse = new Error404NotFound();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado no esta registrado.");
            errorResponse.putDetailsItem("invalidId", idEmploye);
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        }

        Success response = new Success();
        response.setCode("PRODUCT_UPDATED");
        response.setMessage("Producto actualizado exitosamente");
        response.setData(updateEmploye);
        
        return Response.ok(response).build();

    }

    // 5. --- PATCH /employes/{idEmploye} : Actualización parcial (200 OK / 404 NOT_FOUND) ---
    @PATCH
    @Path("/employes/{id}")
    public Response patchEmploye(@PathParam("id") Integer idEmploye, @Valid EmployePatch employePatch) {
        System.out.println("Resource - Solicitud PATCH: actualizar parcialmente empleado ID: " + idEmploye);
        
        if (idEmploye == null || idEmploye < 1 || idEmploye > 2147483647) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado es inválido. Debe estar entre 1 y 2147483647.");
            errorResponse.putDetailsItem("invalidId", idEmploye);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

       Employe patchedEmploye = employeService.patchEmploye(idEmploye, employePatch);

        if (patchedEmploye == null) {
            Error404NotFound errorResponse = new Error404NotFound();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado no esta registrado.");
            errorResponse.putDetailsItem("invalidId", idEmploye);
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        }

        Success response = new Success();
        response.setCode("PRODUCT_PATCHED");
        response.setMessage("Producto actualizado parcialmente exitosamente");
        response.setData(patchedEmploye);
        
        return Response.ok(response).build();
    }
    
    // 6. --- DELETE /employes/{idEmploye} : Eliminar empleado (200 OK / 404 NOT_FOUND) ---
    @DELETE
    @Path("/employes/{id}")
    public Response deleteEmploye(@PathParam("id") Integer idEmploye) {
        System.out.println("Resource - Solicitud DELETE: eliminar empleado ID: " + idEmploye);

        if (idEmploye == null || idEmploye < 1 || idEmploye > 2147483647) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado es inválido. Debe estar entre 1 y 2147483647.");
            errorResponse.putDetailsItem("invalidId", idEmploye);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
            
        String deletionResultEmploye = employeService.deleteEmploye(idEmploye);
                
        Success response = new Success();
        response.setCode("EMPLOYE_DELETED");
        response.setMessage("Eliminado eliminado exitosamente");
        response.setData(deletionResultEmploye);
    
        return Response.ok(response).build();
    }
}
