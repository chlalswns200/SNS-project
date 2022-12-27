package com.example.finalproject_choiminjun.controller;

import com.example.finalproject_choiminjun.domain.dto.UserJoinRequest;
import com.example.finalproject_choiminjun.domain.dto.UserJoinResponse;
import com.example.finalproject_choiminjun.domain.dto.UserLoginRequest;
import com.example.finalproject_choiminjun.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.awt.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public String index() {
        return "redirect:/posts";
    }

    @GetMapping("/join")
    public String createPage() {
        return "join";
    }

    @PostMapping("")
    public String add(UserForm userForm) {
        UserJoinResponse join = userService.join(new UserJoinRequest(userForm.getUsername(), userForm.getPassword()));
        return "redirect:/posts";
    }


}
