package com.feastogether.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登入請求。
 * 對應 Go 版 client.Login struct。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @JsonProperty("act")
    private String account;

    @JsonProperty("pwd")
    private String password;

    @JsonProperty("memberAccessToken")
    @Builder.Default
    private String memberAccessToken = "";
}
