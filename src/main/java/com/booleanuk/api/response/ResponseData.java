package com.booleanuk.api.response;

public class ResponseData<T> extends Response {
    private final T data;

    public ResponseData(T data) {
        this.data = data;
    }

    public T getData() {
        return this.data;
    }
}
