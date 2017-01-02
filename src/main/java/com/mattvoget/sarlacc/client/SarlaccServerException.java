package com.mattvoget.sarlacc.client;

public class SarlaccServerException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SarlaccServerException() {
        super();
    }

    public SarlaccServerException(String message, Throwable e){
        super(message,e);
    }

    public SarlaccServerException(String message){
        super(message);
    }

    public SarlaccServerException(Throwable e){
        super(e);
    }
}
