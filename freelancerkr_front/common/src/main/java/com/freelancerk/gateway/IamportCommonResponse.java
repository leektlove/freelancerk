package com.freelancerk.gateway;

import lombok.Data;

import java.io.Serializable;

@Data
public class IamportCommonResponse implements Serializable {

    private Integer code;
    private String message;
}
