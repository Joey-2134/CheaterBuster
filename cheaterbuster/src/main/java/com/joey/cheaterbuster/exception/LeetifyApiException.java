package com.joey.cheaterbuster.exception;

public class LeetifyApiException extends RuntimeException {

    public LeetifyApiException(String message) {
        super(message);
    }

    public LeetifyApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
