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

@SpringBootApplication
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

public class AuthenticationApi {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApi.class, args);
	}

}
