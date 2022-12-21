package com.example.finalproject_choiminjun.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id @GeneratedValue
    private Long id;
    private String userName;
    private String password;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private UserRole role;
}
