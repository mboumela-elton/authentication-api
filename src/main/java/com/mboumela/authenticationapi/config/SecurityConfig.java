package com.mboumela.authenticationapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.mboumela.authenticationapi.filters.AppUserAuthenticationProvider;

import com.mboumela.authenticationapi.filters.JwtAuthenticationFilter;
import com.mboumela.authenticationapi.services.UserDetailServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

//	private final UserDetailServiceImpl userDetailServiceImpl;

	private final AppUserAuthenticationProvider appUserAuthenticationProvider;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable());
		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("resources/**", "/api/v1/auth/**","/v3/api-docs/**","/v3/api-docs.yml","/swagger-ui/**", "/swagger-ui.html")
				.permitAll());	
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/public/**")
				.permitAll()
				.anyRequest().authenticated());
		
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//				BasicAuthenticationFilter.class);
//		http.userDetailsService(userDetailServiceImpl);
		return http.build();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(appUserAuthenticationProvider);
	}

}
