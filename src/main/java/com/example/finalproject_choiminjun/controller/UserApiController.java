package com.example.finalproject_choiminjun.controller;

import com.example.finalproject_choiminjun.domain.Response;
import com.example.finalproject_choiminjun.domain.dto.*;
import com.example.finalproject_choiminjun.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserApiController {
    private final UserService userService;

    @ApiOperation(value = "회원 가입",notes = "회원 가입을 한다")
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userJoinRequest) {
        UserJoinResponse userJoinResponse = userService.join(userJoinRequest);
        return Response.success(userJoinResponse);
    }

    @ApiOperation(value = "로그인",notes = "로그인 한다")
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        UserLoginResponse userLoginResponse = new UserLoginResponse(userService.login(userLoginRequest));
        return Response.success(userLoginResponse);
    }
    @ApiOperation(value = "역할 변경",notes = "유저의 권한을 변경 한다")
    @PostMapping("/{id}/role/change")
    public Response<UserResponse> changeRole(@PathVariable Long id, Authentication authentication, @RequestBody RoleRequest roleRequest) {
        UserResponse userResponse = userService.changeUserRole(id, roleRequest.getUserRole(),authentication.getName());
        return Response.success(userResponse);
    }

}
