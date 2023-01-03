package com.example.finalproject_choiminjun.service;

import com.example.finalproject_choiminjun.domain.Comment;
import com.example.finalproject_choiminjun.domain.Post;
import com.example.finalproject_choiminjun.domain.User;
import com.example.finalproject_choiminjun.domain.UserRole;
import com.example.finalproject_choiminjun.domain.dto.*;
import com.example.finalproject_choiminjun.exception.AppException;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.example.finalproject_choiminjun.repository.CommentRepository;
import com.example.finalproject_choiminjun.repository.PostRepository;
import com.example.finalproject_choiminjun.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final CommentRepository commentRepository;
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

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));

        if (!Objects.equals(post.getUser().getId(),user.getId()) && !Objects.equals(user.getRole(), UserRole.ADMIN)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION);
        }
        post.modifyPost(postRequest.getTitle(), postRequest.getBody());
        postRepository.saveAndFlush(post);

        return new PostResponse("포스트 수정 완료", post.getId());

    }

    public PostResponse deleteOnePost(Long id, String userName) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));

        if (!Objects.equals(post.getUser().getId(),user.getId()) && !Objects.equals(user.getRole(), UserRole.ADMIN)) {
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

    public CommentResponse writeComment(Long postsId,String userName ,CommentRequest commentRequest) {

        Post post = postRepository.findById(postsId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));

        Comment save = commentRepository.save( Comment.of(commentRequest.getComment(), post, user));

        return CommentResponse.builder()
                .id(save.getId())
                .comment(save.getComment())
                .createdAt(save.getCreatedAt())
                .userName(user.getUserName())
                .postId(post.getId())
                .build();
    }

    public Page<CommentResponse> getCommentsList(Pageable pageable,Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        Page<Comment> all = commentRepository.findAllByPostId(pageable,post.getId());
        Page<CommentResponse> commentResponses = CommentResponse.toList(all);
        return commentResponses;
    }

    @Transactional
    public CommentModifyResponse modifyComment(Long postId, Long id, String name, CommentRequest commentRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        User user = userRepository.findByUserName(name)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENTS_NOT_FOUND));

        if (!comment.getUser().getUserName().equals(user.getUserName())) {
            throw new AppException(ErrorCode.INVALID_PERMISSION);
        }

        comment.modify(commentRequest.getComment());

        Comment modifiedComment = commentRepository.saveAndFlush(comment);

        return CommentModifyResponse.builder()
                .postId(post.getId())
                .comment(modifiedComment.getComment())
                .id(modifiedComment.getId())
                .userName(user.getUserName())
                .createdAt(modifiedComment.getCreatedAt())
                .lastModifiedAd(modifiedComment.getLastModifiedAt())
                .build();
    }
}
