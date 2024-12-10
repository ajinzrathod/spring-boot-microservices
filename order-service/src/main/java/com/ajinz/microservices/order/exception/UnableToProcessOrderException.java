package com.ajinz.microservices.order.exception;

public class UnableToProcessOrderException extends  RuntimeException{
    public UnableToProcessOrderException(String message) {
        super(message);
    }
}
