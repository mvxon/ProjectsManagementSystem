package com.strigalev.authenticationservice.exception;

import com.strigalev.starter.dto.ApiResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.strigalev.starter.util.MethodsUtil.getBindingResultErrors;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestControllerAdvice
public class AuthExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        return ResponseEntity.badRequest().body(ApiResponseEntity.builder()
                .errorCode(ex.getMessage())
                .status(status)
                .message(getBindingResultErrors(ex.getBindingResult()))
                .build());
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        return new ResponseEntity<>(ApiResponseEntity.builder()
                .errorCode(ex.getMessage())
                .status(status)
                .build(), status);
    }


    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(FORBIDDEN)
    protected ApiResponseEntity handleBadCredentials(BadCredentialsException ex) {
        return ApiResponseEntity.builder()
                .message(ex.getMessage())
                .status(FORBIDDEN)
                .build();
    }

}
