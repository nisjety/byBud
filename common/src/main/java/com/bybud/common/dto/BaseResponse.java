package com.bybud.common.dto;

public class BaseResponse<T> {

    private String status;
    private String message;
    private T data;

    // Constructor for success responses
    private BaseResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Constructor for error responses
    private BaseResponse(String status, String message) {
        this.status = status;
        this.message = message;
        this.data = null; // No data for errors
    }

    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>("SUCCESS", message, data);
    }

    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>("ERROR", message);
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
