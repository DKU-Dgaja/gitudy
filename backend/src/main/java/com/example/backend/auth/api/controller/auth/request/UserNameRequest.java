package com.example.backend.auth.api.controller.auth.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserNameRequest {

    @NotBlank(message = "이름은 공백일 수 없습니다.")
    @Size(max = 6, message = "이름 6자 이내")
    private String name;  // 이름

}
