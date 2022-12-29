package com.example.finalproject_choiminjun.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloApiController {

    @ApiOperation(value = "cicd테스트")
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok().body("최민준");
    }

    @GetMapping("/hello/{num}")
    public ResponseEntity<Integer> sumOfDigit(@PathVariable Integer num) {

        int sum = 0;
        while (num >0) {
            sum += num % 10;
            num /= 10;
        }

        return ResponseEntity.ok().body(sum);

    }

}
