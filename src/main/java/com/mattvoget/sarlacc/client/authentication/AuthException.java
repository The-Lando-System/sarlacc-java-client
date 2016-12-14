package com.mattvoget.sarlacc.client.authentication;

import org.springframework.http.HttpStatus;

public class AuthException {

    private String name;
    private String message;
    private String cause;
    private HttpStatus status;
    private int statusCode;

    public AuthException(String message, String cause, HttpStatus status) {
        this.name = this.getClass().getSimpleName();
        this.message = message;
        this.cause = cause;
        this.status = status;
        this.statusCode = status.value();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

}
