package com.example.finalproject_choiminjun.domain;

import com.example.finalproject_choiminjun.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponse {

    private ErrorCode errorCode;
    private String message;
}
