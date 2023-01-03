package com.example.finalproject_choiminjun.repository;

import com.example.finalproject_choiminjun.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
