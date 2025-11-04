package com.pharmacy.api.commons;

import com.pharmacy.api.model.Error400BadRequest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    
    @Override
    public Response toResponse(Exception exception) {
        System.out.println("GlobalExceptionMapper - Capturando excepción: " + exception.getClass().getName() + " - " + exception.getMessage());
        
        // Manejar AlreadyExistsException (Conflict - 409)
        if (exception instanceof AlreadyExistsException) {
            return handleAlreadyExistsException((AlreadyExistsException) exception);
        }
        
        // Manejar NotFoundException (Not Found - 404) - ¡CORREGIDO! usa tu propia excepción
        if (exception instanceof com.pharmacy.api.commons.NotFoundException) {
            return handleNotFoundException((com.pharmacy.api.commons.NotFoundException) exception);
        }
        
        // Manejar BadRequestException (Bad Request - 400) - ¡CORREGIDO! usa tu propia excepción
        if (exception instanceof com.pharmacy.api.commons.BadRequestException) {
            return handleBadRequestException((com.pharmacy.api.commons.BadRequestException) exception);
        }
        
        // Manejar cualquier otra excepción (Internal Server Error - 500)
        return handleGenericException(exception);
    }
    
    private Response handleAlreadyExistsException(AlreadyExistsException exception) {
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("ALREADY_EXISTS");
        errorResponse.setMessage(exception.getMessage());
        
        return Response.status(Response.Status.CONFLICT) // 409 Conflict
                .entity(errorResponse)
                .build();
    }
    
    private Response handleNotFoundException(com.pharmacy.api.commons.NotFoundException exception) {
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("NOT_FOUND");
        errorResponse.setMessage(exception.getMessage());
        
        return Response.status(Response.Status.NOT_FOUND) // 404 Not Found
                .entity(errorResponse)
                .build();
    }
    
    private Response handleBadRequestException(com.pharmacy.api.commons.BadRequestException exception) {
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("BAD_REQUEST");
        errorResponse.setMessage(exception.getMessage());
        
        return Response.status(Response.Status.BAD_REQUEST) // 400 Bad Request
                .entity(errorResponse)
                .build();
    }
    
    private Response handleGenericException(Exception exception) {
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("INTERNAL_SERVER_ERROR");
        errorResponse.setMessage("Ocurrió un error interno en el servidor");
        
        System.err.println("Error no manejado: " + exception.getMessage());
        exception.printStackTrace();
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR) // 500 Internal Server Error
                .entity(errorResponse)
                .build();
    }
}