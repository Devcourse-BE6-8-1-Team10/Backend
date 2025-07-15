package com.back.global.rsData;

public record RsData<T>(String code, String message, T data) {

    public static <T> RsData<T> of(String code, String message, T data) {
        return new RsData<>(code, message, data);
    }

    // 성공 편의 메소드
    public static <T> RsData<T> successOf(T data) {
        return of("200", "success", data);
    }

    // 실패 편의 메소드
    public static <T> RsData<T> failOf(T data) {
        return of("500", "fail", data);
    }

}