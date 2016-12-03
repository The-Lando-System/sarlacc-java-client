package com.mattvoget.sarlacc.client.exceptions;

public class SarlaccClientException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SarlaccClientException() {
        super();
    }

    public SarlaccClientException(String message, Throwable e){
        super(message,e);
    }

    public SarlaccClientException(String message){
        super(message);
    }

    public SarlaccClientException(Throwable e){
        super(e);
    }

}
