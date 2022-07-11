package com.freelancerk.exception;

import lombok.Getter;

public class NotLoggedInException extends BaseException {

    @Getter
    private static final NotLoggedInException instance = new NotLoggedInException();

    private NotLoggedInException() {

    }
}
