package com.chain.sketch.advice.exception;

public class RoomNotFoundException extends RuntimeException{
    public RoomNotFoundException(String msg, Throwable t) { super(msg, t); }

    public RoomNotFoundException(String msg) { super(msg); }

    public RoomNotFoundException() {}
}
