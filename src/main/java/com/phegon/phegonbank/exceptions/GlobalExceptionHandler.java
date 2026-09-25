package com.phegon.phegonbank.exceptions;

import com.phegon.phegonbank.res.Response;
import org.springframework.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(AuthenticationException.class)
 public ResponseEntity<Response<?>> authentication(AuthenticationException ex){return response(HttpStatus.UNAUTHORIZED,"Invalid credentials");}
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<Response<?>> validation(MethodArgumentNotValidException ex){String m=ex.getBindingResult().getFieldErrors().stream().map(e->e.getField()+": "+e.getDefaultMessage()).findFirst().orElse("Validation failed");return response(HttpStatus.BAD_REQUEST,m);}
 @ExceptionHandler(NotFoundException.class) public ResponseEntity<Response<?>> notFound(NotFoundException ex){return response(HttpStatus.NOT_FOUND,ex.getMessage());}
 @ExceptionHandler({BadRequestException.class,InsufficientBalanceException.class,InvalidTransactionException.class})
 public ResponseEntity<Response<?>> badRequest(RuntimeException ex){return response(HttpStatus.BAD_REQUEST,ex.getMessage());}
 @ExceptionHandler(Exception.class) public ResponseEntity<Response<?>> unknown(Exception ex){return response(HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage());}
 private ResponseEntity<Response<?>> response(HttpStatus status,String message){return ResponseEntity.status(status).body(Response.builder().statusCode(status.value()).message(message).build());}
}
