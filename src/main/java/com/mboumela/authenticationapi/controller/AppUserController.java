package com.mboumela.authenticationapi.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mboumela.authenticationapi.dtos.AppUserDto;
import com.mboumela.authenticationapi.dtos.LoginDto;
import com.mboumela.authenticationapi.dtos.SignUpDto;
import com.mboumela.authenticationapi.filters.AppUserAuthenticationProvider;
import com.mboumela.authenticationapi.services.AccountService;
import com.mboumela.authenticationapi.utils.RolesEnum;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class AppUserController {
	
	private final AccountService accountService;
	private final AppUserAuthenticationProvider appUserAuthenticationProvider;
	
	@GetMapping("/yo")
	@PreAuthorize("hasRole('ROLE_USER')")
	public String yo() {
		System.out.println("yo");
		return "yo";
	}
	
	@GetMapping("/user/hello")
	@PreAuthorize("hasRole('ROLE_USER')")
	public String hello() {
		System.out.println("bg");
		return "hello";
	}
	
	@PostMapping("/public/signup")
	public ResponseEntity<AppUserDto> Signup(@RequestBody @Valid SignUpDto signUpDto) {
//		accountService.addNewRole(RolesEnum.USER.name());
		AppUserDto newUser = accountService.addNewUser(signUpDto);
		newUser = accountService.addRoleToUser(newUser.getEmail(), RolesEnum.USER.name());
		newUser.setToken(appUserAuthenticationProvider.createToken(newUser));
		return ResponseEntity.created(URI.create("/user/" + newUser.getEmail())).body(newUser);
	}
	
	@PostMapping("/public/login")
    public ResponseEntity<AppUserDto> login(@RequestBody @Valid LoginDto loginDto) {
        AppUserDto appUserDto = accountService.loadUserByEmail(loginDto.email());
        appUserDto.setToken(appUserAuthenticationProvider.createToken(appUserDto));
        return ResponseEntity.ok(appUserDto);
    }
}
