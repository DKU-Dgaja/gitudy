package com.example.backend.common.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class JsonResult<T> {
    private T resObj;
    private String resMsg;

    @Builder
    public JsonResult(T resObj, String resMsg) {
        this.resObj = resObj;
        this.resMsg = resMsg;
    }

    /*
        성공 응답 객체 생성 메서드 - static
     */
    public static JsonResult successOf() {
        return JsonResult.builder()
                .resMsg(HttpStatus.OK.getReasonPhrase())
                .build();
    }

    public static <T> JsonResult successOf(T resObj) {
        return JsonResult.builder()
                .resObj(resObj)
                .resMsg(HttpStatus.OK.getReasonPhrase())
                .build();
    }

    /*
        실패 응답 객체 생성 메서드 - static
     */
    public static <T> JsonResult failOf(String resMsg) {
        return JsonResult.builder()
                .resMsg(resMsg)
                .build();
    }

    @Override
    public String toString() {
        return "JsonResult{" +
                ", resObj=" + resObj +
                ", resMsg='" + resMsg + '\'' +
                '}';
    }

}
