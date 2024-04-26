package com.mboumela.authenticationapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mboumela.authenticationapi.entities.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long>{
	Optional<AppUser> findByUsername(String username);
	Optional<AppUser> findByEmail(String email);
	Optional<AppUser> findByUserId(int userId);
}
