package com.pharmacy.api.commons;

import com.pharmacy.api.model.Error400BadRequest;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AlreadyExistsExceptionMapper implements ExceptionMapper<AlreadyExistsException> {
    
    @Override
    public Response toResponse(AlreadyExistsException exception) {
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("ALREADY_EXISTS");
        errorResponse.setMessage(exception.getMessage());
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
}