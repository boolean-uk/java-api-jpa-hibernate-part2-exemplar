package com.booleanuk.api.response;

import java.util.List;

public class ResponseList<T> extends Response {
    private final List<T> data;

    public ResponseList(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return this.data;
    }
}
