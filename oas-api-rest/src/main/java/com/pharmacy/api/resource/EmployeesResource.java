package com.pharmacy.api.resource;

import com.pharmacy.api.commons.AlreadyExistsException;
import com.pharmacy.api.model.Employee;
import com.pharmacy.api.model.EmployeePatch;
import com.pharmacy.api.model.Error400BadRequest;
import com.pharmacy.api.model.Error404NotFound;
import com.pharmacy.api.model.Product;
import com.pharmacy.api.model.Success;
import com.pharmacy.api.model.SuccessWithData;
import com.pharmacy.api.service.EmployeesService;

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
public class EmployeesResource {

    @Inject
    EmployeesService employeeService;


    // 1. --- GET /employees : Obtener todos los empleados (200 OK) ---
    @GET
    @Path("/employees")
    public Response getEmployees() {
        System.out.println("Resource - Solicitud GET: obtener todos los empleados.");
        List<Employee> employees = employeeService.getEmployees();
        
        SuccessWithData response = new SuccessWithData();
        response.setCode("EMPLOYEES_RETRIEVED");
        response.setMessage("Lista de empleados obtenida exitosamente");
        response.setData(employees);
    
        return Response.ok(response).build();
    }
    
    // 2. --- GET /employees/{idEmployee} : Obtener un empleado por ID (200 OK / 404 NOT_FOUND) ---
    @GET
    @Path("/employees/{id}")
    public Response getEmployee(@PathParam("id") Integer idEmployee) {
        System.out.println("Resource - Solicitud GET: obtener empleado por ID: " + idEmployee);
            Employee employee = employeeService.getEmployee(idEmployee);
            
            if(employee == null) {
                Error404NotFound errorResponse = new Error404NotFound();
                errorResponse.setCode("EMPLOYEE_NOT_FOUND");
                errorResponse.setMessage("No se encontró el empleado con el ID proporcionado.");
                errorResponse.putDetailsItem("Invalid",idEmployee);
                return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
            }

            Success response = new Success();
            response.setCode("EMPLOYEE_RETRIEVED");
            response.setMessage("Información del empleado obtenida exitosamente");
            response.setData(employee);
            
            return Response.ok(response).status(Status.OK).build();
            
    }

    // 3. --- POST /employees : Crear nuevo empleado (201 CREATED / 400 BAD_REQUEST) ---
    @POST
    @Path("/employees")
    public Response createEmploye(@Valid Employee employeeRequest) {
        System.out.println("Resource - Solicitud POST: crear nuevo empleado.");
            Employee savedEmployee = employeeService.createEmployee(employeeRequest);

            Success response = new Success();
            response.setCode("EMPLOYEE_CREATED");
            response.setMessage("Empleado registrado exitosamente");
            response.setData(savedEmployee);
    
        return Response.status(Response.Status.CREATED).entity(response).build();
 
    }

    // 4. --- PUT /employees/{idEmployee} : Actualizar todos los datos (201 CREATED / 404 NOT_FOUND / 400 BAD_REQUEST) ---
    @PUT
    @Path("/employees/{id}")
    public Response updateEmployee(@PathParam("id") Integer idEmployee, @Valid Employee employeeRequest) {
        System.out.println("Resource - Solicitud PUT: actualizar empleado ID: " + idEmployee);
        
        if (idEmployee < 1 || idEmployee > 2147483647) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado es inválido. Debe estar entre 1 y 2147483647.");
            errorResponse.putDetailsItem("invalidId", idEmployee);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

       Employee updateEmployee = employeeService.updateEmployee(idEmployee, employeeRequest);

        if (idEmployee == null || updateEmployee == null) {
            Error404NotFound errorResponse = new Error404NotFound();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado no esta registrado.");
            errorResponse.putDetailsItem("invalidId", idEmployee);
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        }

        Success response = new Success();
        response.setCode("EMPLOYEE_UPDATED");
        response.setMessage("Empleado actualizado exitosamente");
        response.setData(updateEmployee);
        
        return Response.ok(response).build();

    }

    // 5. --- PATCH /employees/{idEmployee} : Actualización parcial (200 OK / 404 NOT_FOUND) ---
    @PATCH
    @Path("/employees/{id}")
    public Response patchEmploye(@PathParam("id") Integer idEmployee, @Valid EmployeePatch employeePatch) {
        System.out.println("Resource - Solicitud PATCH: actualizar parcialmente empleado ID: " + idEmployee);
        
        if (idEmployee < 1 || idEmployee > 2147483647) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado es inválido. Debe estar entre 1 y 2147483647.");
            errorResponse.putDetailsItem("invalidId", idEmployee);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

       Employee patchedEmployee = employeeService.patchEmployee(idEmployee, employeePatch);

        if (idEmployee == null || patchedEmployee == null) {
            Error404NotFound errorResponse = new Error404NotFound();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado no esta registrado.");
            errorResponse.putDetailsItem("invalidId", idEmployee);
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        }

        Success response = new Success();
        response.setCode("EMPLOYEE_PATCHED");
        response.setMessage("Empleado actualizado parcialmente exitosamente");
        response.setData(patchedEmployee);
        
        return Response.ok(response).build();
    }
    
    // 6. --- DELETE /employees/{idEmployee} : Eliminar empleado (200 OK / 404 NOT_FOUND) ---
    @DELETE
    @Path("/employees/{id}")
    public Response deleteEmployee(@PathParam("id") Integer idEmployee) {
        System.out.println("Resource - Solicitud DELETE: eliminar empleado ID: " + idEmployee);

        if (idEmployee < 1 || idEmployee > 2147483647) {
            Error400BadRequest errorResponse = new Error400BadRequest();
            errorResponse.setCode("INVALID_EMPLOYEE_ID");
            errorResponse.setMessage("El ID del empleado es inválido. Debe estar entre 1 y 2147483647.");
            errorResponse.putDetailsItem("invalidId", idEmployee);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

                   
        String deletionResultEmployee = employeeService.deleteEmployee(idEmployee);
   
        Success response = new Success();
        response.setCode("EMPLOYEE_DELETED");
        response.setMessage("Eliminado empleado exitosamente");
        response.setData(deletionResultEmployee);
    
        return Response.ok(response).build();
    }
}
