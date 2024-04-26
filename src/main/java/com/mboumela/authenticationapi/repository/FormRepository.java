package com.mboumela.authenticationapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mboumela.authenticationapi.entities.Form;


public interface FormRepository extends JpaRepository<Form, String> {
	Optional<Form> findByFilePath(String path);
}
