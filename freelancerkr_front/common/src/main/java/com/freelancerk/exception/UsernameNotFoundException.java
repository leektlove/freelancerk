package com.freelancerk.exception;

import lombok.Getter;

public class UsernameNotFoundException extends BaseException {

    @Getter
    private static final UsernameNotFoundException instance = new UsernameNotFoundException();

    private UsernameNotFoundException() {

    }
}
