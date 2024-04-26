package com.mboumela.authenticationapi.dtos;

public record StudentFormDto (Long userId, String nom, String prenom, String dateNaissance, String lieuNaissance, String adresse,
		String telephone, String email, String etablissement, String filiere, String niveau, String motivation) {
}
