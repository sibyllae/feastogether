package com.feastogether.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SVG 驗證碼回應。
 * 對應 Go 版 client.SvgResponse struct。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SvgResponse {

    @JsonProperty("statusCode")
    private int statusCode;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("message")
    private String message;

    @JsonProperty("result")
    private SvgResult result;

    /** 判斷 API 是否成功（statusCode == 1000） */
    public boolean isSuccess() {
        return statusCode == 1000;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SvgResult {

        @JsonProperty("code")
        private String code;

        @JsonProperty("svg")
        private String svg;
    }
}
