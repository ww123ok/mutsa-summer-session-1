package com.likelion.shopping.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    private int status;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    // 상태 코드와 메시지, 데이터를 모두 지정하는 성공 응답
    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(status, message, data);
    }

    // 가장 자주 쓰이는 200 OK 성공 응답 편의 메서드
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "요청에 성공했습니다.", data);
    }

    // 데이터가 필요 없는 성공 응답 (예: 삭제 성공, 상품 추가 성공) 또는 에러 응답
    public static <T> ApiResponse<T> of(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}
