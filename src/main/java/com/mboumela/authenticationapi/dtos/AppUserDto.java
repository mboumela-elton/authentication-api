package com.mboumela.authenticationapi.dtos;

import java.util.List;

import com.mboumela.authenticationapi.entities.AppRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AppUserDto {
	
	private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private List<AppRole> roles;
    private String token;

}
