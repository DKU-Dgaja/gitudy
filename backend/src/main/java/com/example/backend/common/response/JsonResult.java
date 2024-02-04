package com.example.backend.common.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class JsonResult<T> {
    private Integer resCode;
    private T resObj;
    private String resMsg;

    @Builder
    public JsonResult(Integer resCode, T resObj, String resMsg) {
        this.resCode = resCode;
        this.resObj = resObj;
        this.resMsg = resMsg;
    }

    /*
        성공 응답 객체 생성 메서드 - static
     */
    public static JsonResult successOf() {
        return JsonResult.builder()
                .resCode(HttpStatus.OK.value())
                .resMsg(HttpStatus.OK.getReasonPhrase())
                .build();
    }

    public static <T> JsonResult successOf(T resObj) {
        return JsonResult.builder()
                .resCode(HttpStatus.OK.value())
                .resObj(resObj)
                .resMsg(HttpStatus.OK.getReasonPhrase())
                .build();
    }

    /*
        실패 응답 객체 생성 메서드 - static
     */
    public static JsonResult failOf() {
        return JsonResult.builder()
                .resCode(HttpStatus.BAD_REQUEST.value())
                .resMsg(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .build();
    }

    public static <T> JsonResult failOf(String resMsg) {
        return JsonResult.builder()
                .resCode(HttpStatus.BAD_REQUEST.value())
                .resMsg(resMsg)
                .build();
    }

    @Override
    public String toString() {
        return "JsonResult{" +
                "resCode=" + resCode +
                ", resObj=" + resObj +
                ", resMsg='" + resMsg + '\'' +
                '}';
    }

}
