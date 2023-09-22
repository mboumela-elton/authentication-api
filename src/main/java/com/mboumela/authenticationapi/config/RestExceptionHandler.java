package com.mboumela.authenticationapi.config;

import com.mboumela.authenticationapi.dtos.ErrorMsgDto;
import com.mboumela.authenticationapi.exceptions.ApplicationException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
//this annotation is for intercept controllers errors
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = { ApplicationException.class })
    @ResponseBody
    public ResponseEntity<ErrorMsgDto> handleException(ApplicationException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(new ErrorMsgDto(exception.getMessage()));
    }
}