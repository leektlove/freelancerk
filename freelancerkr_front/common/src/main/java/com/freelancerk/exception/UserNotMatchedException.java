package com.freelancerk.exception;

import lombok.Getter;

public class UserNotMatchedException extends BaseException {

    @Getter
    private static final UserNotMatchedException instance = new UserNotMatchedException();

    private UserNotMatchedException() {

    }
}
