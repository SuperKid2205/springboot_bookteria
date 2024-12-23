package com.hung.practice.enums;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNKNOWN_ERROR(9999, "Unknown error.", HttpStatus.INTERNAL_SERVER_ERROR),
    ENUM_KEY_INVALID(9000, "Unknown enum key.", HttpStatus.BAD_REQUEST),
    USER_EXITS(9001, "User exited.", HttpStatus.BAD_REQUEST),
    USER_NOT_EXITS(9002, "User does not exited.", HttpStatus.BAD_REQUEST),
    USERNAME_MIN_CHARACTER(9003, "Username must be have least {min} character.", HttpStatus.NOT_FOUND),
    PASSWORD_MIN_CHARACTER(9004, "Password must be have least {min} character.", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(9005, "Wrong password.", HttpStatus.UNAUTHORIZED),
    GENERATE_TOKEN_FAILED(9006, "Can not generate token.", HttpStatus.FORBIDDEN),
    ACCESS_DENIED(9007, "Does not have enough permission.", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(9008, "Token has expired.", HttpStatus.UNAUTHORIZED),
    AUTHORITY_NOT_ENOUGH(9009, "Insufficient authorization to access endpoint.", HttpStatus.FORBIDDEN),
    ROLE_NOT_EXITS(9010, "Role does not exited.", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_EXITS(9011, "Permission does not exited.", HttpStatus.BAD_REQUEST),
    DOB_INVALID(9012, "Your age must be least {min} years.", HttpStatus.BAD_REQUEST),
    TOKEN_LOGOUT(9013, "Token has expired.", HttpStatus.UNAUTHORIZED);

    int code;
    String message;
    HttpStatusCode httpStatusCode;
}
