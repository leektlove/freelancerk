package com.freelancerk.exception;

import lombok.Getter;

public class ContestNotFullyPickedException extends BaseException {

    @Getter
    private static final ContestNotFullyPickedException instance = new ContestNotFullyPickedException();

    private ContestNotFullyPickedException() {

    }
}
