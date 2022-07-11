package com.freelancerk.io;

import lombok.Data;

@Data
public class CommonResponse<T> {
    protected ResponseCode responseCode;
    protected String message;
    protected T data;

    private static final CommonResponse successResponse = new CommonResponse();

    public static CommonResponse ok() {
        successResponse.responseCode = ResponseCode.SUCCESS;
        return successResponse;
    }

    public static CommonResponse fail() {
        successResponse.responseCode = ResponseCode.FAIL;
        return successResponse;
    }

    public CommonResponse() {

    }

    public CommonResponse(Builder<T> builder) {
        this.message = builder.message;
        this.responseCode = builder.responseCode;
        this.data = builder.data;
    }

    public static class Builder<T> {
        private T data;
        private String message;
        private ResponseCode responseCode = ResponseCode.SUCCESS;

        public Builder<T> message(String message) {
            this.message = message;
            return this;
        }

        public Builder<T> responseCode(ResponseCode responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public CommonResponse<T> build() {
            return new CommonResponse<T>(this);
        }
    }
}
