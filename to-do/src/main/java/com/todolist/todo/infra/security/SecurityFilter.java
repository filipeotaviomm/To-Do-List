package com.todolist.todo.infra.security;

import com.todolist.todo.models.UserModel;
import com.todolist.todo.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class SecurityFilter extends OncePerRequestFilter {
  @Autowired
  TokenService tokenService;
  @Autowired
  UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var token = this.recoverToken(request);
    var userId = tokenService.validateToken(token);

    if (userId != null) {
      UUID userIdUUID = UUID.fromString(userId); // Converter a String para UUID
      UserModel user = userRepository.findById(userIdUUID).orElseThrow(() -> new RuntimeException("User Not Found"));
      // var authorities = Collections.singletonList(new
      // SimpleGrantedAuthority("ROLE_USER")); //se não for usar o campo roles passa a
      // variável "authorities" na linha de baixo no lugar de "user.getAuthorities()"
      var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }

  private String recoverToken(HttpServletRequest request) {
    var authHeader = request.getHeader("Authorization");
    if (authHeader == null)
      return null;
    return authHeader.replace("Bearer ", "");
  }
}
