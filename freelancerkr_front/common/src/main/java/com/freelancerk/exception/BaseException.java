package com.freelancerk.exception;

import lombok.Getter;
import lombok.Setter;

public class BaseException extends RuntimeException {
    @Getter
    @Setter
    protected String message;

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
