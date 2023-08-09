package io.github.fykrel.controller;

import io.github.fykrel.utils.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 登录/登出接口
 *
 * @author fangyk
 * @date 2023年08月09日 11:34
 */
@RestController
public class AuthController {
    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 登录
     *
     * @param username 账号
     * @param password 密码
     * @return jwt token
     */
    @GetMapping("login")
    public String login(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        assert authenticate != null;

        User user = (User) authenticate.getPrincipal();

        redisTemplate.opsForValue().set(username, user);

        return JwtUtil.createToken(user);
    }
}