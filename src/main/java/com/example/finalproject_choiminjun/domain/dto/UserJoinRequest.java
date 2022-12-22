package com.example.finalproject_choiminjun.domain.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class UserJoinRequest {
    private String userName;
    private String password;

}
