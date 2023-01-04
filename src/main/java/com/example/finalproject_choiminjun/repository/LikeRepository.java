package com.example.finalproject_choiminjun.repository;

import com.example.finalproject_choiminjun.domain.Like;
import com.example.finalproject_choiminjun.domain.Post;
import com.example.finalproject_choiminjun.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Long> {
    Optional<Like> findByPostAndUser(Post post, User user);
    List<Like> findByPost(Post post);
    void deleteAllByPost(Post post);
}
