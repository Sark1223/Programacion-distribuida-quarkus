package com.pharmacy.api.resource;

import java.util.List;

import com.pharmacy.api.model.Employee;
import com.pharmacy.api.model.EmployeePatch;
import com.pharmacy.api.model.Error400BadRequest;
import com.pharmacy.api.model.Error404NotFound;
import com.pharmacy.api.model.Success;
import com.pharmacy.api.model.SuccessWithData;
import com.pharmacy.api.service.EmployeesService;

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
import jakarta.ws.rs.core.Response.Status;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeesResource {

    @Inject
    EmployeesService employeService;


    // 1. --- GET /employeees : Obtener todos los empleados (200 OK) ---
    @GET
    @Path("/employeees")
    public Response getEmployees() {
        System.out.println("Resource - Solicitud GET: obtener todos los empleados.");
        List<Employee> employeees = employeService.getEmployees();
        
        SuccessWithData response = new SuccessWithData();
        response.setCode("EMPLOYEES_RETRIEVED");
        response.setMessage("Lista de empleados obtenida exitosamente");
        response.setData(employeees);
    
        return Response.ok(response).build();
    }
    
    // 2. --- GET /employeees/{idEmployee} : Obtener un empleado por ID (200 OK / 404 NOT_FOUND) ---
    @GET
    @Path("/employeees/{id}")
    public Response getEmployee(@PathParam("id") Integer idEmployee) {
        System.out.println("Resource - Solicitud GET: obtener empleado por ID: " + idEmployee);
            Employee employe = employeService.getEmployee(idEmployee);
            
            if(employe == null) {
                Error404NotFound errorResponse = new Error404NotFound();
                errorResponse.setCode("PRODUCT_NOT_FOUND");
                errorResponse.setMessage("No se encontró el producto con el ID proporcionado.");
                errorResponse.putDetailsItem("Invalid",idEmployee);
                return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
            }

            Success response = new Success();
            response.setCode("EMPLOYE_RETRIEVED");
            response.setMessage("Información del empleado obtenida exitosamente");
            response.setData(employe);
            
            return Response.ok(response).status(Status.OK).build();
            
    }

    // 3. --- POST /employeees : Crear nuevo empleado (201 CREATED / 400 BAD_REQUEST) ---
    @POST
    @Path("/employeees")
    public Response createEmployee(@Valid Employee employeRequest) {
        System.out.println("Resource - Solicitud POST: crear nuevo empleado.");
            Employee savedEmployee = employeService.createEmployee(employeRequest);

            Success response = new Success();
            response.setCode("EMPLOYE_CREATED");
            response.setMessage("Empleado registrado exitosamente");
            response.setData(savedEmployee);
    
        return Response.status(Response.Status.CREATED).entity(response).build();
 
    }

    // 4. --- PUT /employeees/{idEmployee} : Actualizar todos los datos (201 CREATED / 404 NOT_FOUND / 400 BAD_REQUEST) ---
    @PUT
    @Path("/employeees/{id}")
    public Response updateEmployee(@PathParam("id") Integer idEmployee, @Valid Employee employeRequest) {
        System.out.println("Resource - Solicitud PUT: actualizar empleado ID: " + idEmployee);
        
        if (idEmployee == null || idEmployee < 1 || idEmployee > 2147483647) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado es inválido. Debe estar entre 1 y 2147483647.");
            errorResponse.putDetailsItem("invalidId", idEmployee);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

        Employee updateEmployee = employeService.updateEmployee(idEmployee, employeRequest);

        if (updateEmployee == null) {
            Error404NotFound errorResponse = new Error404NotFound();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado no esta registrado.");
            errorResponse.putDetailsItem("invalidId", idEmployee);
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        }

        Success response = new Success();
        response.setCode("PRODUCT_UPDATED");
        response.setMessage("Producto actualizado exitosamente");
        response.setData(updateEmployee);
        
        return Response.ok(response).build();

    }

    // 5. --- PATCH /employeees/{idEmployee} : Actualización parcial (200 OK / 404 NOT_FOUND) ---
    @PATCH
    @Path("/employeees/{id}")
    public Response patchEmployee(@PathParam("id") Integer idEmployee, @Valid EmployeePatch employePatch) {
        System.out.println("Resource - Solicitud PATCH: actualizar parcialmente empleado ID: " + idEmployee);
        
        if (idEmployee == null || idEmployee < 1 || idEmployee > 2147483647) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado es inválido. Debe estar entre 1 y 2147483647.");
            errorResponse.putDetailsItem("invalidId", idEmployee);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

       Employee patchedEmployee = employeService.patchEmployee(idEmployee, employePatch);

        if (patchedEmployee == null) {
            Error404NotFound errorResponse = new Error404NotFound();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado no esta registrado.");
            errorResponse.putDetailsItem("invalidId", idEmployee);
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        }

        Success response = new Success();
        response.setCode("PRODUCT_PATCHED");
        response.setMessage("Producto actualizado parcialmente exitosamente");
        response.setData(patchedEmployee);
        
        return Response.ok(response).build();
    }
    
    // 6. --- DELETE /employeees/{idEmployee} : Eliminar empleado (200 OK / 404 NOT_FOUND) ---
    @DELETE
    @Path("/employeees/{id}")
    public Response deleteEmployee(@PathParam("id") Integer idEmployee) {
        System.out.println("Resource - Solicitud DELETE: eliminar empleado ID: " + idEmployee);

        if (idEmployee == null || idEmployee < 1 || idEmployee > 2147483647) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado es inválido. Debe estar entre 1 y 2147483647.");
            errorResponse.putDetailsItem("invalidId", idEmployee);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
            
        String deletionResultEmployee = employeService.deleteEmployee(idEmployee);
                
        Success response = new Success();
        response.setCode("EMPLOYE_DELETED");
        response.setMessage("Eliminado eliminado exitosamente");
        response.setData(deletionResultEmployee);
    
        return Response.ok(response).build();
    }
}