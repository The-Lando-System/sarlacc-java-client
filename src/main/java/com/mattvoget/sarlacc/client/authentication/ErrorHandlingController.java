package com.mattvoget.sarlacc.client.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ErrorHandlingController {

    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public @ResponseBody
    AuthException illegalAccessException(IllegalAccessError iae) {
        return new AuthException(iae.getMessage(), iae.getClass().getName(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public @ResponseBody AuthException unknownException(RuntimeException e) {
        return new AuthException(e.getMessage(), e.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
