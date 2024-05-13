package com.ssafy.double_bean.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = HttpResponseException.class)
    protected ResponseEntity<ErrorResponse> handleHttpResponseException(HttpResponseException e) {
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Object handleControllerValidationException(MethodArgumentNotValidException e) {
        List<Map<String, String>> errorMessages = new ArrayList<>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            Map<String, String> errorDescription = new HashMap<>();
            errorDescription.put("message", fieldError.getDefaultMessage());
            errorDescription.put("field", fieldError.getField());
            errorDescription.put("rejectedValue", fieldError.getRejectedValue() == null ? null : fieldError.getRejectedValue().toString());
            errorMessages.add(errorDescription);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleNotReadableRequest(HttpMessageNotReadableException e) {
        return ErrorResponse.toResponseEntity(ErrorCode.NOT_READABLE_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<ErrorResponse> handleUnknownException(Exception e) {
        System.out.println("Unknown exception:::::");
        e.printStackTrace();
        ErrorResponse response = new ErrorResponse("", "Unknown exception occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
