package com.mboumela.authenticationapi.dtos;

public record WorkerFormDto(
		 Long userId, String nom, String prenom, String dateNaissance, String lieuNaissance,
         String adresse, String telephone, String email, String lieuService, String posteOccupe,
         String experience, String motivation
		) {

}
