package com.example.finalproject_choiminjun.domain.dto;

import com.example.finalproject_choiminjun.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostRequest {
    private String title;
    private String body;

}
