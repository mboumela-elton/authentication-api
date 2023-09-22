package com.mboumela.authenticationapi.dtos;

public record SignUpDto (String firstName, String lastName, String email, char[] password) { }
