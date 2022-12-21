package com.example.finalproject_choiminjun.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    NORMAL("normal"),ADMIN("admin");
    private String role;

}
