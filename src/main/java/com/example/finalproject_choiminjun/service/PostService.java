package com.example.finalproject_choiminjun.service;

import com.example.finalproject_choiminjun.domain.*;
import com.example.finalproject_choiminjun.domain.dto.*;
import com.example.finalproject_choiminjun.exception.AppException;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.example.finalproject_choiminjun.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Where;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final AlarmRepository alarmRepository;

    private User findUser(Optional<User> userRepository) {
        return userRepository
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }
    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
    }

    public PostResponse post(PostRequest postRequest,String userName) {
        User user =  findUser(userRepository.findByUserName(userName));

        Post post = Post.of(postRequest,user);
        Post save = postRepository.save(post);
        return new PostResponse("포스트 등록 완료", save.getId());
    }

    public OnePostResponse get(Long id) {
        Post post = findPostById(id);
        OnePostResponse onePostResponse = OnePostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .userName(post.getUser().getUserName())
                .lastModifiedAt(post.getLastModifiedAt())
                .createdAt(post.getCreatedAt())
                .build();
        return onePostResponse;

    }

    @Transactional
    public PostResponse modifyOnePost(Long id,String userName,PostRequest postRequest) {

        Post post = findPostById(id);

        User byUserName = findUser(userRepository.findByUserName(userName));

        if (!Objects.equals(post.getUser().getId(),byUserName.getId()) && !Objects.equals(byUserName.getRole(), UserRole.ADMIN)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION);
        }
        post.modifyPost(postRequest.getTitle(), postRequest.getBody());
        postRepository.saveAndFlush(post);

        return new PostResponse("포스트 수정 완료", post.getId());

    }

    @Transactional
    public PostResponse deleteOnePost(Long id, String userName) {

        Post post = findPostById(id);

        User byUserName = findUser(userRepository.findByUserName(userName));

        if (!Objects.equals(post.getUser().getId(),byUserName.getId()) && !Objects.equals(byUserName.getRole(), UserRole.ADMIN)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION);
        }
        Long deleteId = post.getId();

        List<Comment> allByPostId = commentRepository.findAllByPostId(deleteId);
        for (Comment comment : allByPostId) {
            comment.deletePostKey();
            commentRepository.saveAndFlush(comment);
        }
        likeRepository.deleteAllByPost(post);
        post.delete();
        postRepository.saveAndFlush(post);

        return new PostResponse("포스트 삭제 완료", deleteId);
    }

    public Page<OnePostResponse> getPostList(Pageable pageable) {
        Page<Post> all = postRepository.findAll(pageable);
        Page<OnePostResponse> responseList = OnePostResponse.makeResponse(all);
        return responseList;

    }

    public CommentResponse writeComment(Long postsId,String userName ,CommentRequest commentRequest) {

        Post post = findPostById(postsId);

        User byUserName = findUser(userRepository.findByUserName(userName));

        Comment save = commentRepository.save( Comment.of(commentRequest.getComment(), post, byUserName));

        Alarm alarm = Alarm.commentAlarm(post, byUserName);
        alarmRepository.save(alarm);

        return CommentResponse.builder()
                .id(save.getId())
                .comment(save.getComment())
                .createdAt(save.getCreatedAt())
                .userName(byUserName.getUserName())
                .postId(post.getId())
                .build();
    }

    public Page<CommentResponse> getCommentsList(Pageable pageable,Long postId) {

        Post post = findPostById(postId);
        Page<Comment> all = commentRepository.findAllByPostId(pageable,post.getId());
        Page<CommentResponse> commentResponses = CommentResponse.toList(all);
        return commentResponses;
    }

    @Transactional
    public CommentModifyResponse modifyComment(Long postId, Long id, String name, CommentRequest commentRequest) {
        Post post = findPostById(postId);
        User user = findUser(userRepository.findByUserName(name));

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

    @Transactional
    public CommentDeleteResponse deleteOneComment(Long postId, Long id, String name) {

        Post post = findPostById(postId);

        User user = findUser(userRepository.findByUserName(name));

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENTS_NOT_FOUND));

        if (!comment.getUser().getUserName().equals(user.getUserName())) {
            throw new AppException(ErrorCode.INVALID_PERMISSION);
        }
        Long findId = comment.getId();
        commentRepository.deleteById(id);

        return new CommentDeleteResponse("댓글 삭제 완료", findId);

    }


    public Page<OnePostResponse> myPost(String name, Pageable pageable) {
        User user = findUser(userRepository.findByUserName(name));
        Page<Post> allByUser = postRepository.findAllByUser(user,pageable);
        return OnePostResponse.makeResponse(allByUser);
    }

    @Transactional
    public String pushLike(Long postId, String userName) {

        Post post = findPostById(postId);
        User byUserName = findUser(userRepository.findByUserName(userName));
        Optional<Like> byPostAndUser = likeRepository.findByPostAndUser(post, byUserName);

        if (byPostAndUser.isPresent()) {
            likeRepository.delete(byPostAndUser.get());
            return "좋아요가 취소 되었습니다.";
        } else {
            Like of = Like.of(post, byUserName);
            likeRepository.save(of);
            Alarm likeAlarm = Alarm.likeAlarm(post,byUserName);
            alarmRepository.save(likeAlarm);
            return "좋아요를 눌렀습니다.";
        }
    }

    public long getCount(Long postId) {

        Post post = findPostById(postId);
        List<Like> byPost = likeRepository.findByPost(post);
        return byPost.stream().count();

    }
}
