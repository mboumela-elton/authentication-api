package com.mboumela.authenticationapi.services;

import java.util.List;
import java.util.Optional;

import com.mboumela.authenticationapi.dtos.AppUserDto;
import com.mboumela.authenticationapi.dtos.LoginDto;
import com.mboumela.authenticationapi.dtos.SignUpDto;
import com.mboumela.authenticationapi.entities.AppRole;
import com.mboumela.authenticationapi.entities.AppUser;

public interface AccountService {
    AppUserDto addNewUser(SignUpDto loginDto);
    AppRole addNewRole(String role);
    AppUserDto addRoleToUser(String email,String role);
    void removeRoleFromUser(String email,String role);
    AppUserDto loadUserByEmail(String email);
    AppUserDto loginVerification(LoginDto loginDto);
    List<AppUser> findAllUsers();
    AppUser getuserByUserId(int userId);
    void deleteAppUser(Long userId);
}
