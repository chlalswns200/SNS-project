package com.example.finalproject_choiminjun.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
@Getter
public enum ErrorCode {

    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "DUPLICATED_USERNAME"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,"INVALID_PASSWORD");


    private HttpStatus status;
    private String message;
}
