package com.mboumela.authenticationapi.exceptions;

import org.springframework.http.HttpStatus;

public class ApplicationException extends RuntimeException {

    private final HttpStatus status;

    public ApplicationException(String errorMessage, HttpStatus status) {
        super(errorMessage);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
