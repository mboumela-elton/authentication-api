package com.mboumela.authenticationapi.services;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mboumela.authenticationapi.config.RestExceptionHandler;
import com.mboumela.authenticationapi.dtos.AppUserDto;
import com.mboumela.authenticationapi.dtos.SignUpDto;
import com.mboumela.authenticationapi.entities.AppRole;
import com.mboumela.authenticationapi.entities.AppUser;
import com.mboumela.authenticationapi.exceptions.ApplicationException;
import com.mboumela.authenticationapi.mappers.AppUserMapper;
import com.mboumela.authenticationapi.repository.AppRoleRepository;
import com.mboumela.authenticationapi.repository.AppUserRepository;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
	
	private final AppUserRepository appUserRepository;
	private final AppRoleRepository appRoleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AppUserMapper appUserMapper;
	
	
	@Override
	public AppUserDto addNewUser(SignUpDto signUpDto) {
		
		Optional<AppUser> appUser = appUserRepository.findByEmail(signUpDto.email());
				
		if(appUser.isPresent())
				throw new ApplicationException("user already exits", HttpStatus.BAD_REQUEST);
		
		AppUser newAppUser = AppUser.builder().firstName(signUpDto.firstName()).lastName(signUpDto.lastName())
				.email(signUpDto.email()).password(passwordEncoder.encode(CharBuffer.wrap(signUpDto.password())))
				.build();
		
		AppUser savedAppUser = appUserRepository.save(newAppUser);
		
		return appUserMapper.AppUserToAppUserDto(savedAppUser);
	}

	@Override
	public AppRole addNewRole(String roleName) {
		Optional<AppRole> appRole = appRoleRepository.findByRoleName(roleName);

		if (appRole.isPresent())
			throw new ApplicationException("role already exists", HttpStatus.BAD_REQUEST);

		return appRoleRepository.save(AppRole.builder().roleName(roleName).build());
	}

	@Override
	public AppUserDto addRoleToUser(String email, String roleName) {
		AppUser appUser = appUserRepository.findByEmail(email).orElseThrow(
				() -> new ApplicationException("user not found", HttpStatus.NOT_FOUND));
		
		AppRole appRole = appRoleRepository.findByRoleName(roleName).orElseThrow(
				() -> new ApplicationException("role not found", HttpStatus.NOT_FOUND));
		
		List<AppRole> roles = new ArrayList<>();
		roles.add(appRole);
		
		appUser.setRoles(roles);
		return appUserMapper.AppUserToAppUserDto(appUser);
	}

	@Override
	public void removeRoleFromUser(String email, String roleName) {
		AppUser appUser = appUserRepository.findByEmail(email).orElseThrow(
				() -> new ApplicationException("user not found", HttpStatus.NOT_FOUND));
		
		AppRole appRole = appRoleRepository.findByRoleName(roleName).orElseThrow(
				() -> new ApplicationException("role not found", HttpStatus.NOT_FOUND));
		appUser.getRoles().remove(appRole);
	}

	@Override
	public AppUserDto loadUserByEmail(String email) {
		AppUser appUser = appUserRepository.findByEmail(email).orElseThrow(
				() -> new ApplicationException("user not found", HttpStatus.NOT_FOUND));

		return appUserMapper.AppUserToAppUserDto(appUser);
	}
}
