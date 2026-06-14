package com.feastogether.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 饗賓 API 通用回應。
 * 對應 Go 版 client.Response struct。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse {

    @JsonProperty("statusCode")
    private int statusCode;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("message")
    private String message;

    @JsonProperty("result")
    private Result result;

    @JsonProperty("path")
    private String path;

    /** 判斷 API 是否成功（statusCode == 1000） */
    public boolean isSuccess() {
        return statusCode == 1000;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {

        // login 回應
        @JsonProperty("_ACT")
        private String act;

        @JsonProperty("_P")
        private String p;

        @JsonProperty("customerLoginResp")
        private CustomerLoginResp customerLoginResp;

        // saveSeats 回應
        @JsonProperty("expirationTime")
        private String expirationTime;

        // booking 回應
        @JsonProperty("bookingId")
        private String bookingId;

        @JsonProperty("bookingState")
        private String bookingState;

        @JsonProperty("brandName")
        private String brandName;

        @JsonProperty("expireTime")
        private String expireTime;

        @JsonProperty("mealDate")
        private String mealDate;

        @JsonProperty("mealPeriod")
        private String mealPeriod;

        @JsonProperty("mealTime")
        private String mealTime;

        @JsonProperty("paymentState")
        private String paymentState;

        @JsonProperty("storeName")
        private String storeName;

        // error
        @JsonProperty("msg")
        private String msg;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CustomerLoginResp {

        @JsonProperty("address")
        private String address;

        @JsonProperty("area")
        private String area;

        @JsonProperty("birthDay")
        private String birthDay;

        @JsonProperty("birthMonth")
        private String birthMonth;

        @JsonProperty("birthYear")
        private String birthYear;

        @JsonProperty("city")
        private String city;

        @JsonProperty("email")
        private String email;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("memberShip")
        private String memberShip;

        @JsonProperty("name")
        private String name;

        @JsonProperty("phoneNumber")
        private String phoneNumber;

        @JsonProperty("token")
        private String token;
    }
}
