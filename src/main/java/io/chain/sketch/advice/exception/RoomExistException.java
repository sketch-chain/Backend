package io.chain.sketch.advice.exception;

public class RoomExistException extends RuntimeException {
    public RoomExistException(String msg, Throwable t) {
        super(msg, t);
    }

    public RoomExistException(String msg) {
        super(msg);
    }

    public RoomExistException() {
        super();
    }
}
