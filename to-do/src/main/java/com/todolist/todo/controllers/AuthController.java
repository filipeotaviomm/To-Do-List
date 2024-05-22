package com.todolist.todo.controllers;

import com.todolist.todo.dtos.login.LoginRequestDto;
import com.todolist.todo.dtos.login.LoginResponseDto;
import com.todolist.todo.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto body) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(authService.login(body));
  }
}