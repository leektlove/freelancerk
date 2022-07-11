package com.freelancerk.gateway;

import lombok.Data;

@Data
public class IamportResponse<T> {
    private int code;
    private String message;
    private T response;
}
