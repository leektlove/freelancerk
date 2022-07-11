package com.freelancerk.exception;

import lombok.Getter;

public class IamportException extends BaseException {

    @Getter
    private static final IamportException instance = new IamportException();

    private IamportException() {

    }
}
