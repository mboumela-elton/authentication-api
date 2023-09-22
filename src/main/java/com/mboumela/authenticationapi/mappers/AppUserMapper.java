package com.mboumela.authenticationapi.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.mboumela.authenticationapi.dtos.AppUserDto;
import com.mboumela.authenticationapi.entities.AppUser;


@Mapper(componentModel = "spring")
public interface AppUserMapper {
    AppUserDto AppUserToAppUserDto(AppUser appUser);
}