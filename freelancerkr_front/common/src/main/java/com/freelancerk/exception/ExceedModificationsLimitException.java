package com.freelancerk.exception;

import lombok.Getter;

public class ExceedModificationsLimitException extends BaseException {

    @Getter
    private static final ExceedModificationsLimitException instance = new ExceedModificationsLimitException();

    private ExceedModificationsLimitException() {

    }
}
