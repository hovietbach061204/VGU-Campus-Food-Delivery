package com.devteria.identityservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    EATERY_NOT_FOUND(1009, "Eatery does not exist", HttpStatus.BAD_REQUEST),
    FOOD_ITEM_NOT_FOUND(1010, "Food item does not exist", HttpStatus.BAD_REQUEST),
    FOOD_ITEM_ALREADY_EXISTS(1011, "Food item has already existed in the selected eatery", HttpStatus.BAD_REQUEST),
    EATER_EXISTED(1012, "Eatery existed", HttpStatus.BAD_REQUEST),
    PASSWORD_EXISTED(1013, "Password existed", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT(1014, "The password is incorrect", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(1015, "Invalid order status transition", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1016, "Order not found", HttpStatus.NOT_FOUND);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
