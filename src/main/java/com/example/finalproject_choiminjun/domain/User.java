package com.example.finalproject_choiminjun.domain;

import com.example.finalproject_choiminjun.domain.dto.UserJoinRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    public static User of(UserJoinRequest userJoinRequest,String password) {
        User user = User.builder()
                .userName(userJoinRequest.getUserName())
                .password(password)
                .role(UserRole.NORMAL)
                .build();
        return user;
    }

    public void changeRole(UserRole userRole) {
        this.role = userRole;
    }
}
