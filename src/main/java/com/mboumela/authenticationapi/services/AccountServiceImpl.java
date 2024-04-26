package com.mboumela.authenticationapi.services;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mboumela.authenticationapi.config.RestExceptionHandler;
import com.mboumela.authenticationapi.dtos.AppUserDto;
import com.mboumela.authenticationapi.dtos.LoginDto;
import com.mboumela.authenticationapi.dtos.SignUpDto;
import com.mboumela.authenticationapi.entities.AppRole;
import com.mboumela.authenticationapi.entities.AppUser;
import com.mboumela.authenticationapi.exceptions.ApplicationException;
import com.mboumela.authenticationapi.mappers.AppUserMapper;
import com.mboumela.authenticationapi.repository.AppRoleRepository;
import com.mboumela.authenticationapi.repository.AppUserRepository;

import java.nio.CharBuffer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
	
    public boolean verifyPassword(String providedPassword, String encodedPassword) {
        return passwordEncoder.matches(providedPassword, encodedPassword);
    }

	@Override
	public AppUserDto addNewUser(SignUpDto signUpDto) {

		LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        Optional<AppUser> appUser2 = appUserRepository.findByUsername(signUpDto.username());
		if (appUser2.isPresent())
			throw new ApplicationException("The user name entered is already associated with an account !!!", HttpStatus.BAD_REQUEST);

        
		Optional<AppUser> appUser = appUserRepository.findByEmail(signUpDto.email());
		if (appUser.isPresent())
			throw new ApplicationException("The email entered is already associated with an account !!!", HttpStatus.BAD_REQUEST);
		
		AppUser newAppUser = AppUser.builder().username(signUpDto.username()).email(signUpDto.email())
				.password(passwordEncoder.encode(CharBuffer.wrap(signUpDto.password()))).dateCreated(LocalDate.now().format(formatter))
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
		AppUser appUser = appUserRepository.findByEmail(email)
				.orElseThrow(() -> new ApplicationException("The email entered is not associated with an account !!!", HttpStatus.NOT_FOUND));

		AppRole appRole = appRoleRepository.findByRoleName(roleName)
				.orElseThrow(() -> new ApplicationException("role not found", HttpStatus.NOT_FOUND));

		List<AppRole> roles = new ArrayList<>();
		roles.add(appRole);

		appUser.setRoles(roles);
		return appUserMapper.AppUserToAppUserDto(appUser);
	}

	@Override
	public void removeRoleFromUser(String email, String roleName) {
		AppUser appUser = appUserRepository.findByEmail(email)
				.orElseThrow(() -> new ApplicationException("The email entered is not associated with an account !!!", HttpStatus.NOT_FOUND));

		AppRole appRole = appRoleRepository.findByRoleName(roleName)
				.orElseThrow(() -> new ApplicationException("role not found", HttpStatus.NOT_FOUND));
		appUser.getRoles().remove(appRole);
	}

	@Override
	public AppUserDto loadUserByEmail(String email) {
		AppUser appUser = appUserRepository.findByEmail(email)
				.orElseThrow(() -> new ApplicationException("The email entered is not associated with an account !!!", HttpStatus.NOT_FOUND));

		return appUserMapper.AppUserToAppUserDto(appUser);
	}
	
	@Override
	public AppUserDto loginVerification(LoginDto loginDto) {
		AppUser appUser = appUserRepository.findByEmail(loginDto.email())
				.orElseThrow(() -> new ApplicationException("The email entered is not associated with an account !!!", HttpStatus.NOT_FOUND));
		
		if(!verifyPassword(new String(loginDto.password()), appUser.getPassword())) {
			throw new ApplicationException("The account associated with this email does not match this password !!!", HttpStatus.BAD_REQUEST);
		}
		
		return appUserMapper.AppUserToAppUserDto(appUser);
	}

	@Override
	public List<AppUser> findAllUsers() {
		List<AppUser> appUsers = appUserRepository.findAll();
		return appUsers;
	}

	@Override
	public AppUser getuserByUserId(int userId) {
		AppUser user = appUserRepository.findByUserId(userId)
				.orElseThrow(() -> new ApplicationException("The email entered is already associated with an account !!!", HttpStatus.NOT_FOUND));
		
		return user;
	}

	@Override
	public void deleteAppUser(Long userId) {
		appUserRepository.deleteById(userId);
	}
}
