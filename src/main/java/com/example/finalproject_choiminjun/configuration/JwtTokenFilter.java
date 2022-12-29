package com.example.finalproject_choiminjun.configuration;

import com.example.finalproject_choiminjun.domain.User;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.example.finalproject_choiminjun.service.UserService;
import com.example.finalproject_choiminjun.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    //권한 주거나 안주기
    //권한 안주는 경우 (토큰 x, 유효하지 않은 토큰(적절하지 않은것, 만료된것))
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorization:{}", authorizationHeader);

        // 토큰이 없는 경우 제외
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            try {
                filterChain.doFilter(request, response);
                return;
            } catch (Exception e) {
                request.setAttribute("exception", ErrorCode.INVALID_TOKEN.getMessage());
            }
        }

        try {
            String token = authorizationHeader.split(" ")[1];

            String userName = JwtTokenUtil.getUsername(token, secretKey);
            User user = userService.getUserByUserName(userName);

            // 권한 부여
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), null, List.of(new SimpleGrantedAuthority(user.getRole().name())));
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e){
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN.getMessage());
        }
        filterChain.doFilter(request, response);
    }

}
