package com.example.finalproject_choiminjun.repository;

import com.example.finalproject_choiminjun.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    Page<Comment> findAllByPostId(Pageable pageable,Long postId);
    List<Comment> findAllByPostId(Long postId);

}
