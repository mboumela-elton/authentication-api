package com.mboumela.authenticationapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

import java.io.File;

@OpenAPIDefinition(
		info = @Info(
			contact = @Contact(
					name = "Mboumela Elton",
					email = "mboumelae@gmail.com",
					url = "https://cv-portfolio-elton.vercel.app/"
					),
			
			title = "common authentification", 
			version = "1.0.0", 
			description = "simple connexion with jwt",
			license = @License(
					name = "MIT license",
					url = ""
					)
		),
		servers = {
				@Server(
						description = "Local Environnement",
						url = "http://localhost:8090"
						)
		},
		security = {
				@SecurityRequirement(
						name = "bearerAuth"
						)
		}
)

@SecurityScheme(
		name = "bearerAuth",
		description = "JWT authentication",
		scheme = "bearer",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		in = SecuritySchemeIn.HEADER
)

@SpringBootApplication
public class AuthenticationApi {
	 public static String parentFolder;

	public static void main(String[] args) {
		 String dossierPath = "files";
	        
	        // Créez une instance de la classe File avec le chemin du dossier
	        File dossier = new File(dossierPath);
	        
	        // Vérifiez si le dossier existe
	        if (dossier.exists()) {
	            // Obtenez le chemin absolu du dossier
	            String cheminAbsolu = dossier.getAbsolutePath();
	            
	            parentFolder = cheminAbsolu + "\\";
	            
	            System.out.println("Chemin absolu du dossier : " + cheminAbsolu);
	        } else {
	            System.out.println("Le dossier n'existe pas.");
	        }
	        
	        SpringApplication.run(AuthenticationApi.class, args);  
	}

}
