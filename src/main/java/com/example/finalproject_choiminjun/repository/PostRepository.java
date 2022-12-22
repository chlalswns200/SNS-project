package com.example.finalproject_choiminjun.repository;

import com.example.finalproject_choiminjun.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PostRepository extends JpaRepository<Post,Long> {
}
