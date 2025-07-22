package ua.orlov.betreactive.controller;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.orlov.betreactive.dto.ErrorResponse;
import ua.orlov.betreactive.exceptions.EntityNotFoundException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleIllegalArgumentException() {
        String message = "Invalid argument";
        Mono<ResponseEntity<ErrorResponse>> response = handler.handleIllegalArgumentException(new IllegalArgumentException(message));

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
                    assertEquals(message, entity.getBody().getMessage());
                })
                .verifyComplete();
    }

    @Test
    void testHandleResourceNotFoundException() {
        String message = "Resource not found";
        Mono<ResponseEntity<ErrorResponse>> response = handler.handleResourceNotFound(new ResourceNotFoundException(message));

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
                    assertEquals(message, entity.getBody().getMessage());
                })
                .verifyComplete();
    }

    @Test
    void testHandleEntityNotFoundException() {
        String message = "Entity not found";
        Mono<ResponseEntity<ErrorResponse>> response = handler.handleEntityNotFoundException(new EntityNotFoundException(message));

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
                    assertEquals(message, entity.getBody().getMessage());
                })
                .verifyComplete();
    }

    @Test
    void testHandleValidationException() {
        WebExchangeBindException ex = mock(WebExchangeBindException.class);
        when(ex.getFieldErrors()).thenReturn(Collections.singletonList(
                new org.springframework.validation.FieldError("objectName", "fieldName", "must not be null")
        ));

        Mono<ResponseEntity<ErrorResponse>> response = handler.handleValidationException(ex);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
                    assertTrue(entity.getBody().getMessage().contains("Validation failed: fieldName: must not be null"));
                })
                .verifyComplete();
    }

    @Test
    void testHandleInvalidInputWithUUIDThenException() {
        Throwable cause = new IllegalArgumentException("Invalid UUID string");
        ServerWebInputException ex = new ServerWebInputException("Failed to convert", null, cause);

        Mono<ResponseEntity<ErrorResponse>> response = handler.handleInvalidInput(ex);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
                    assertTrue(entity.getBody().getMessage().contains("Invalid UUID format"));
                })
                .verifyComplete();
    }

    @Test
    void testHandleInvalidInputThenException() {
        ServerWebInputException ex = new ServerWebInputException("Failed to convert");

        Mono<ResponseEntity<ErrorResponse>> response = handler.handleInvalidInput(ex);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
                })
                .verifyComplete();
    }

    @Test
    void testHandleInvalidInputThenExceptionWithDifferentCause() {
        Throwable cause = new IllegalArgumentException("Invalid string");
        ServerWebInputException ex = new ServerWebInputException("Failed to convert", null, cause);

        Mono<ResponseEntity<ErrorResponse>> response = handler.handleInvalidInput(ex);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
                })
                .verifyComplete();
    }

    @Test
    void testHandleGeneralException() {
        Exception ex = new Exception("Something bad happened");

        Mono<ResponseEntity<ErrorResponse>> response = handler.handleGeneralException(ex);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
                    assertTrue(entity.getBody().getMessage().contains("Unexpected error occurred. Something bad happened"));
                })
                .verifyComplete();
    }
}
