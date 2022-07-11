package com.freelancerk.exception;

import lombok.Getter;

public class ExceedPasswordFailCountException extends BaseException {

    @Getter
    private static final ExceedPasswordFailCountException instance = new ExceedPasswordFailCountException();

    private ExceedPasswordFailCountException() {

    }
}
