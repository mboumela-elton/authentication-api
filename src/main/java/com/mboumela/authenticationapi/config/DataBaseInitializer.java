package com.mboumela.authenticationapi.config;

import java.nio.CharBuffer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.mboumela.authenticationapi.entities.AppRole;
import com.mboumela.authenticationapi.entities.AppUser;
import com.mboumela.authenticationapi.repository.AppRoleRepository;
import com.mboumela.authenticationapi.repository.AppUserRepository;
import com.mboumela.authenticationapi.services.AccountService;
import com.mboumela.authenticationapi.utils.RolesEnum;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataBaseInitializer implements ApplicationRunner {
	
	private final AccountService accountService;
	private final AppRoleRepository appRoleRepository;
	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Value("${admin.username}")
	private String adminUsername; 
	
	@Value("${admin.email}")
	private String adminEmail; 
	
	@Value("${admin.password}")
	private String adminPassword;	
	@Override
	
	public void run(ApplicationArguments args) throws Exception {
		
		// for initial role
		Optional<AppRole> userRole = appRoleRepository.findByRoleName(RolesEnum.USER.name());
        if (userRole.isEmpty()) {
            AppRole appRole = new AppRole(RolesEnum.USER.name());
            appRoleRepository.save(appRole);
        }

        Optional<AppRole> adminRole = appRoleRepository.findByRoleName(RolesEnum.ADMIN.name());
        if (adminRole.isEmpty()) {
            AppRole appRole = new AppRole(RolesEnum.ADMIN.name());
            appRoleRepository.save(appRole);
        }
        
        // for initial admin
        Optional<AppUser> user = appUserRepository.findByEmail(adminEmail);
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        if (user.isEmpty()) {
			AppUser newAppUser = AppUser.builder().username(adminUsername).email(adminEmail)
    				.password(passwordEncoder.encode(CharBuffer.wrap(adminPassword))).dateCreated(LocalDate.now().format(formatter))
    				.build();
            appUserRepository.save(newAppUser);
            accountService.addRoleToUser(adminEmail, RolesEnum.ADMIN.name());
        }
        
	}

}
