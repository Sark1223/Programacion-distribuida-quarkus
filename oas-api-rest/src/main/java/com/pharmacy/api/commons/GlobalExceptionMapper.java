// [file name]: GlobalExceptionMapper.java
package com.pharmacy.api.commons;

import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.pharmacy.api.model.Error400BadRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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
        
        // Manejar NotFoundException (Not Found - 404)
        if (exception instanceof NotFoundException) {
            return handleNotFoundException((NotFoundException) exception);
        }
        
        // Manejar BadRequestException (Bad Request - 400)
        if (exception instanceof BadRequestException) {
            return handleBadRequestException((BadRequestException) exception);
        }
        
        // Manejar JsonProcessingException (Bad Request - 400)
        if (exception instanceof JsonProcessingException) {
            return handleJsonProcessingException((JsonProcessingException) exception);
        }
        
        // Manejar excepciones de validación de Jakarta
        if (exception instanceof ConstraintViolationException) {
            return handleConstraintViolationException((ConstraintViolationException) exception);
        }
        
        // Manejar excepciones de Jackson (JSON parsing)
        if (exception instanceof JsonParseException) {
            return handleJsonParseException((JsonParseException) exception);
        }
        
        if (exception instanceof JsonMappingException) {
            return handleJsonMappingException((JsonMappingException) exception);
        }
        
        if (exception instanceof InvalidFormatException) {
            return handleInvalidFormatException((InvalidFormatException) exception);
        }
        
        if (exception instanceof UnrecognizedPropertyException) {
            return handleUnrecognizedPropertyException((UnrecognizedPropertyException) exception);
        }
        
        if (exception instanceof MismatchedInputException) {
            return handleMismatchedInputException((MismatchedInputException) exception);
        }
        
        // Manejar cualquier otra excepción (Internal Server Error - 500)
        return handleGenericException(exception);
    }
    
    private Response handleAlreadyExistsException(AlreadyExistsException exception) {
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("ALREADY_EXISTS");
        errorResponse.setMessage(exception.getMessage());
        
        return Response.status(Response.Status.CONFLICT)
                .entity(errorResponse)
                .build();
    }
    
    private Response handleNotFoundException(NotFoundException exception) {
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("NOT_FOUND");
        errorResponse.setMessage(exception.getMessage());
        
        return Response.status(Response.Status.NOT_FOUND)
                .entity(errorResponse)
                .build();
    }
    
    private Response handleBadRequestException(BadRequestException exception) {
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("BAD_REQUEST");
        errorResponse.setMessage(exception.getMessage());
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
    
    private Response handleJsonProcessingException(JsonProcessingException exception) {
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("INVALID_JSON");
        errorResponse.setMessage(exception.getMessage());
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
    
    private Response handleConstraintViolationException(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        String errorMessage = violations.stream()
                .map(violation -> {
                    String path = violation.getPropertyPath().toString();
                    // Extraer solo el nombre del campo del path completo
                    String fieldName = path.contains(".") ? 
                        path.substring(path.lastIndexOf('.') + 1) : path;
                    return fieldName + ": " + violation.getMessage();
                })
                .collect(Collectors.joining("; "));
        
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("VALIDATION_ERROR");
        errorResponse.setMessage("Error de validación: " + errorMessage);
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
    
    private Response handleJsonParseException(JsonParseException exception) {
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("INVALID_JSON_FORMAT");
        errorResponse.setMessage("JSON malformado: " + exception.getOriginalMessage());
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
    
    private Response handleJsonMappingException(JsonMappingException exception) {
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("JSON_MAPPING_ERROR");
        errorResponse.setMessage("Error en el mapeo JSON: " + exception.getOriginalMessage());
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
    
    private Response handleInvalidFormatException(InvalidFormatException exception) {
        String fieldName = exception.getPath().isEmpty() ? 
            "campo desconocido" : 
            exception.getPath().get(0).getFieldName();
        
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("INVALID_FORMAT");
        errorResponse.setMessage(String.format(
            "Formato inválido para el campo '%s'. Se esperaba: %s", 
            fieldName, exception.getTargetType().getSimpleName()
        ));
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
    
    private Response handleUnrecognizedPropertyException(UnrecognizedPropertyException exception) {
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("UNRECOGNIZED_FIELD");
        errorResponse.setMessage(String.format(
            "Campo no reconocido: '%s'. Campos permitidos: %s",
            exception.getPropertyName(),
            exception.getKnownPropertyIds()
        ));
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
    
    private Response handleMismatchedInputException(MismatchedInputException exception) {
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("MISMATCHED_INPUT");
        errorResponse.setMessage("Tipo de dato incorrecto: " + exception.getOriginalMessage());
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
    
    private Response handleGenericException(Exception exception) {
        Error400BadRequest errorResponse = new Error400BadRequest();
        errorResponse.setCode("INTERNAL_SERVER_ERROR");
        errorResponse.setMessage("Ocurrió un error interno en el servidor");
        
        System.err.println("Error no manejado: " + exception.getMessage());
        exception.printStackTrace();
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
    }
}