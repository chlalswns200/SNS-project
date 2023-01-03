package com.example.finalproject_choiminjun.repository;

import com.example.finalproject_choiminjun.domain.Post;
import com.example.finalproject_choiminjun.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PostRepository extends JpaRepository<Post,Long> {
    Page<Post> findAllByUser(User user, Pageable pageable);
}
