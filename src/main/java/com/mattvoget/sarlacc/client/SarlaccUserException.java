package com.mattvoget.sarlacc.client;

public class SarlaccUserException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SarlaccUserException() {
        super();
    }

    public SarlaccUserException(String message, Throwable e){
        super(message,e);
    }

    public SarlaccUserException(String message){
        super(message);
    }

    public SarlaccUserException(Throwable e){
        super(e);
    }
}
