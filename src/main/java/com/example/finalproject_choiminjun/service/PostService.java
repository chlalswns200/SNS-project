package com.example.finalproject_choiminjun.service;

import com.example.finalproject_choiminjun.domain.Post;
import com.example.finalproject_choiminjun.domain.User;
import com.example.finalproject_choiminjun.domain.dto.OnePostResponse;
import com.example.finalproject_choiminjun.domain.dto.PostRequest;
import com.example.finalproject_choiminjun.domain.dto.PostResponse;
import com.example.finalproject_choiminjun.exception.AppException;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.example.finalproject_choiminjun.repository.PostRepository;
import com.example.finalproject_choiminjun.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    public PostResponse post(PostRequest postRequest,String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "이 없습니다."));

        Post post = Post.of(postRequest,user);
        Post save = postRepository.save(post);
        return new PostResponse("포스트 등록 완료", save.getId());
    }

    public OnePostResponse get(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        OnePostResponse onePostResponse = OnePostResponse.entityToResponse(post);
        return onePostResponse;

    }

    @Transactional
    public PostResponse modifyOnePost(Long id,String userName,PostRequest postRequest) {

        log.info("input_id : {}",id);
        log.info("input_userName : {}",userName);
        log.info("input_postRequest_title : {}",postRequest.getTitle());


        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        log.info("find_userName_in_userId : {}",post.getUser().getId());

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        log.info("find_userId : {}", user.getId());

        if (!Objects.equals(post.getUser().getId(),user.getId())) {
            throw new AppException(ErrorCode.INVALID_PERMISSION);
        }
        post.setTitle(postRequest.getTitle());
        post.setBody(postRequest.getBody());
        postRepository.saveAndFlush(post);

        return new PostResponse("포스트 수정 완료", post.getId());

    }

    public PostResponse deleteOnePost(Long id, String userName) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));

        if (!Objects.equals(post.getUser().getId(),user.getId())) {
            throw new AppException(ErrorCode.INVALID_PERMISSION);
        }
        postRepository.delete(post);

        return new PostResponse("포스트 삭제 완료", post.getId());

    }

    public Page<OnePostResponse> getPostList(Pageable pageable) {
        Page<Post> all = postRepository.findAll(pageable);
        Page<OnePostResponse> responseList = OnePostResponse.toList(all);
        return responseList;

    }
}
