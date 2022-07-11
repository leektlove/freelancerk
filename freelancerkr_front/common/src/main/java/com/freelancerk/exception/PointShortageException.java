package com.freelancerk.exception;

import lombok.Getter;

public class PointShortageException extends BaseException {

    @Getter
    private static final PointShortageException instance = new PointShortageException();

    private PointShortageException() {

    }
}
