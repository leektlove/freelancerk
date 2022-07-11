package com.freelancerk.io;

import lombok.Data;

@Data
public class CommonListResponse<T> extends CommonResponse<T> {
    private long totalCount;
    private int totalPages;
    private int currentPage;

    public CommonListResponse() {

    }

    public CommonListResponse(Builder<T> builder) {
        this.totalCount = builder.totalCount;
        this.totalPages = builder.totalPages;
        this.currentPage = builder.currentPage;

        this.message = builder.message;
        this.responseCode = builder.responseCode;
        this.data = builder.data;
    }

    public static class Builder<T> {
        private T data;
        private String message;
        private ResponseCode responseCode = ResponseCode.SUCCESS;

        private long totalCount;
        private int totalPages;
        private int currentPage;

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

        public Builder<T> totalCount(long totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public Builder<T> totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder<T> currentPage(int currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public CommonListResponse<T> build() {
            return new CommonListResponse<T>(this);
        }
    }
}
