package com.mboumela.authenticationapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mboumela.authenticationapi.entities.Cv;

public interface CvRepository extends JpaRepository<Cv, String> {
	Optional<Cv> findByFilePath(String path);
}
