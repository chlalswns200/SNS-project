package com.example.finalproject_choiminjun.domain.dto;
import com.example.finalproject_choiminjun.domain.User;
import com.example.finalproject_choiminjun.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class UserJoinRequest {

    private String userName;
    private String password;

    public User toEntity(String encodePassword) {
        User user = User.builder()
                .userName(this.userName)
                .password(encodePassword)
                .registeredAt(LocalDateTime.now())
                .role(UserRole.NORMAL)
                .build();
        return user;
    }
}
