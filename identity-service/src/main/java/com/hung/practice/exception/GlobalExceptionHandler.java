package com.hung.practice.exception;

import java.util.Map;

import jakarta.validation.ConstraintViolation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.hung.practice.dto.response.ApiResponse;
import com.hung.practice.enums.ErrorCode;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingException(Exception exception) {
        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(exception.getMessage())
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        // unwrap first error to constraint information
        var constraintViolation =
                exception.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);

        // Get param in annotation
        var attributes = constraintViolation.getConstraintDescriptor().getAttributes();

        ErrorCode errorCode = null;
        try {
            String code = (String) attributes.get("message");
            errorCode = ErrorCode.valueOf(code);

        } catch (IllegalArgumentException ex) {
            errorCode = ErrorCode.ENUM_KEY_INVALID;
        }

        String message = getErrorMessage(attributes, errorCode.getMessage());

        ApiResponse<?> apiResponse =
                ApiResponse.builder().code(errorCode.getCode()).message(message).build();

        return ResponseEntity.ok().body(apiResponse);
    }

    private String getErrorMessage(Map<?, ?> attributes, String message) {
        StringBuilder result = new StringBuilder(message);
        attributes.forEach((key, value) -> {
            if (value instanceof String || value instanceof Number) {
                String target = "{" + key + "}";
                int startIndex = result.indexOf(target);
                if (startIndex != -1) {
                    result.replace(startIndex, startIndex + target.length(), String.valueOf(value));
                }
            }
        });
        return result.toString();
    }
}
