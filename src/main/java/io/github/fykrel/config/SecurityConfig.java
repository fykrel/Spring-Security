package io.github.fykrel.config;

import io.github.fykrel.exceptionHandler.DefaultAccessDeniedHandler;
import io.github.fykrel.exceptionHandler.DefaultAuthenticationEntryPoint;
import io.github.fykrel.filter.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security 配置
 *
 * @author fangyk
 * @date 2023年08月09日 11:15
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenFilter jwtTokenFilter) throws Exception {
        http
                // 禁用表单认证
                .formLogin().disable()
                // 禁用HTTP BASIC认证
                .httpBasic().disable()
                // 禁用CSRF
                .csrf().disable()
                //
                .headers().frameOptions().disable()
                .and()
                // 配置路径权限
                .authorizeHttpRequests()
                // 任何用户都能访问
                .antMatchers("/login").permitAll()
                // 需认证用户才能访问
                .anyRequest().authenticated()
                .and()
                // 采用Token认证, SESSION设置为无状态
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 配置异常处理
                .exceptionHandling()
                // 认证异常
                .authenticationEntryPoint(new DefaultAuthenticationEntryPoint())
                // 授权异常
                .accessDeniedHandler(new DefaultAccessDeniedHandler())
                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}