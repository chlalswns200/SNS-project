package com.example.finalproject_choiminjun.configuration;

import com.example.finalproject_choiminjun.domain.ErrorResponse;
import com.example.finalproject_choiminjun.domain.Response;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint{

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = (String) request.getAttribute("exception");
        ObjectMapper objectMapper = new ObjectMapper();

        ErrorCode errorCode = ErrorCode.INVALID_TOKEN;

        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        ErrorResponse errorResponse = new ErrorResponse(errorCode, errorCode.getMessage());

        Response errorRes = Response.error("ERROR", errorResponse);
        response.getWriter().write(objectMapper.writeValueAsString(errorRes));
    }

}
