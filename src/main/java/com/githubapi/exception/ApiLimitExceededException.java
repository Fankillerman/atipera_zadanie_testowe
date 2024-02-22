package com.githubapi.exception;

public class ApiLimitExceededException extends RuntimeException {
    public ApiLimitExceededException(String message) {
        super(message);
    }
}
