package com.wwm.main.controller;

import com.wwm.main.config.JwtTokenProvider;
import com.wwm.main.domain.User;
import com.wwm.main.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class UserControllerTest {


    private  PasswordEncoder passwordEncoder;
    private  JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;


    @Test
    public Long join() {
        Map<String,String> user = new HashMap<String,String>();
        user.put("email", "goyounha11@naver.com");
        user.put("password", "123");

        System.out.println(user.get("email"));
        System.out.println(user.get("password"));


        return userRepository.save(User.builder()
                .email(user.get("email"))
                .password(passwordEncoder.encode(user.get("password")))
                .roles(Collections.singletonList("ROLE_USER")) // 최초 가입시 USER 로 설정
                .build()).getId();
    }

}