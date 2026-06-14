package com.feastogether.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前端送出的驗證碼請求。
 * 從 index.html 的 POST /svg 接收。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyRequest {

    @JsonProperty("verify")
    private String verify;
}
