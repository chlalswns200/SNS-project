package com.example.finalproject_choiminjun.controller;

import com.example.finalproject_choiminjun.domain.Post;
import com.example.finalproject_choiminjun.domain.dto.OnePostResponse;
import com.example.finalproject_choiminjun.domain.dto.PostRequest;
import com.example.finalproject_choiminjun.repository.PostRepository;
import com.example.finalproject_choiminjun.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostRepository postRepository;
    private final PostService postService;

    @GetMapping("")
    public String getHospitalByKeyword(@PageableDefault(size = 20)
                                           @SortDefault(sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable,Model model) {

        Page<OnePostResponse> posts = postService.getPostList(pageable);
        model.addAttribute("posts", posts);
        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber());
        model.addAttribute("next", pageable.next().getPageNumber());
        return "list";
    }

    @GetMapping("/{id}")
    public String selectOne(@PathVariable Long id, Model model) {
        OnePostResponse onePostResponse = postService.get(id);
        model.addAttribute("post", onePostResponse);
        return "show";
    }

    @GetMapping("/new")
    public String createPage() {
        return "new";
    }

}
