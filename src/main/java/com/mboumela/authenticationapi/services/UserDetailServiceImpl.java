package com.mboumela.authenticationapi.services;

import lombok.AllArgsConstructor;

import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mboumela.authenticationapi.entities.AppUser;
import com.mboumela.authenticationapi.repository.AppUserRepository;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private AppUserRepository appUserRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	System.out.println("alelouya");
        Optional<AppUser> appUser = appUserRepository.findByEmail(email);
        if(appUser.isEmpty()) throw new UsernameNotFoundException(String.format("User %s not found",email));
        
        System.out.println("alelouya boy");

        String[] roles = appUser.get().getRoles().
                stream().map(u->u.getRoleName()).toArray(String[]::new);

        UserDetails userDetails = User
                .withUsername(appUser.get().getEmail())
                .password(appUser.get().getPassword())
                .roles(roles).build();
        return userDetails;
    }
}
