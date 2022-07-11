package com.freelancerk.exception;

import lombok.Getter;

public class NotFreePostingException extends BaseException {

    @Getter
    private static final NotFreePostingException instance = new NotFreePostingException();

    private NotFreePostingException() {

    }
}
