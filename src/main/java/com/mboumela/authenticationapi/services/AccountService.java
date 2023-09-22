package com.mboumela.authenticationapi.services;

import com.mboumela.authenticationapi.dtos.AppUserDto;
import com.mboumela.authenticationapi.dtos.SignUpDto;
import com.mboumela.authenticationapi.entities.AppRole;
import com.mboumela.authenticationapi.entities.AppUser;

public interface AccountService {
    AppUserDto addNewUser(SignUpDto loginDto);
    AppRole addNewRole(String role);
    AppUserDto addRoleToUser(String email,String role);
    void removeRoleFromUser(String email,String role);
    AppUserDto loadUserByEmail(String email);
}
