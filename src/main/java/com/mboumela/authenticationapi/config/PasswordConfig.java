package com.mboumela.authenticationapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mboumela.authenticationapi.mappers.AppUserMapper;
import com.mboumela.authenticationapi.mappers.AppUserMapperImpl;

@Component
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}
