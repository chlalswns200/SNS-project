package com.example.finalproject_choiminjun.controller;

import com.example.finalproject_choiminjun.domain.Response;
import com.example.finalproject_choiminjun.domain.dto.UserJoinRequest;
import com.example.finalproject_choiminjun.domain.dto.UserJoinResponse;
import com.example.finalproject_choiminjun.domain.dto.UserLoginResponse;
import com.example.finalproject_choiminjun.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @ApiOperation(value = "회원 가입")
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userJoinRequest) {
        UserJoinResponse userJoinResponse = userService.join(userJoinRequest);
        return Response.success(userJoinResponse);
    }

    @ApiOperation(value = "로그인")
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserJoinRequest userJoinRequest) {
        UserLoginResponse userLoginResponse = new UserLoginResponse(userService.login(userJoinRequest));
        return Response.success(userLoginResponse);
    }

}
