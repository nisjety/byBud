package com.bybud.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private String message;
    private T data;
    private String error;

    public BaseResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public BaseResponse(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>(message, data);
    }

    public static BaseResponse<Void> success(String message) {
        return new BaseResponse<>(message, null);
    }

    public static BaseResponse<Void> error(String error) {
        return new BaseResponse<>(error);
    }
}

