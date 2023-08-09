package io.github.fykrel.filter;

import io.github.fykrel.constants.Headers;
import io.github.fykrel.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * jwt过滤器
 *
 * @author fangyk
 * @date 2023年08月09日 13:46
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(Headers.AUTHORIZATION);

        if (!Objects.isNull(token) && !token.isEmpty()) {
            if (Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
                UsernamePasswordAuthenticationToken authentication = getAuthentication(token);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 根据token构造验证主体
     *
     * @param token token
     * @return {@link UsernamePasswordAuthenticationToken}
     */
    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims;
        try {
            claims = JwtUtil.parse(token);
            User user = (User) redisTemplate.opsForValue().get(claims.getSubject());

            return new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
        } catch (Exception e) {
            return null;
        }
    }
}
