package com.mboumela.authenticationapi.filters;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mboumela.authenticationapi.dtos.AppUserDto;
import com.mboumela.authenticationapi.services.AccountService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AppUserAuthenticationProvider {
	
	@Value("${jwt.secret.key}")
	private String secretKey; 
	
	private final AccountService accountService;
	
	//to avoid to have secretKey in the JVM
	@PostConstruct
	private void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}
	
	public String createToken(AppUserDto appUserDto) {
//        Date now = new Date();
//        Date validity = new Date(now.getTime() + 3600000*24*30*12); // 1 year

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withIssuer(appUserDto.getEmail())
//                .withIssuedAt(now)
//                .withExpiresAt(validity)
                .withClaim("roles", appUserDto.getRoles().stream().map(ga -> ga.getRoleName()).collect(Collectors.toList()))
                .sign(algorithm);
    }
	
	public Authentication validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decoded = verifier.verify(token);

        List<String> roleNames = decoded.getClaim("roles").asList(String.class);
        String[] roles = roleNames.stream().toArray(String[]::new);
        
        UserDetails userDetails = User
        		.withUsername(decoded.getIssuer())
        		.password("dummy-password")
        		.roles(roles).build();
		
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public Authentication validateTokenStrongly(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        DecodedJWT decoded = verifier.verify(token);

        AppUserDto appUserDto = accountService.loadUserByEmail(decoded.getIssuer());

        String[] roles = appUserDto.getRoles().
                stream().map(u->u.getRoleName()).toArray(String[]::new);
        
        UserDetails userDetails = User
        		.withUsername(appUserDto.getEmail())
        		.password("dummy-password")
        		.roles(roles).build();

        
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

}
