package com.mboumela.authenticationapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mboumela.authenticationapi.entities.AppRole;

public interface AppRoleRepository extends JpaRepository<AppRole, String> {
	Optional<AppRole> findByRoleName(String roleName);
}
