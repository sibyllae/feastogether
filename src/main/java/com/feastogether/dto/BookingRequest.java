package com.feastogether.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 確認訂位請求。
 * 對應 Go 版 client.Booking struct。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @JsonProperty("storeId")
    private String storeId;

    @JsonProperty("mealDate")
    private String mealDate;

    @JsonProperty("mealPurpose")
    @Builder.Default
    private String mealPurpose = "";

    @JsonProperty("mealSeq")
    private int mealSeq;

    @JsonProperty("mealTime")
    private String mealTime;

    @JsonProperty("mealPeriod")
    private String mealPeriod;

    @JsonProperty("special")
    @Builder.Default
    private int special = 0;

    @JsonProperty("childSeat")
    @Builder.Default
    private int childSeat = 0;

    @JsonProperty("adult")
    private int adult;

    @JsonProperty("child")
    @Builder.Default
    private int child = 0;

    @JsonProperty("chargeList")
    private List<ChargeItem> chargeList;

    @JsonProperty("storeCode")
    private String storeCode;

    @JsonProperty("redirectType")
    @Builder.Default
    private String redirectType = "iEat_card";

    @JsonProperty("domain")
    @Builder.Default
    private String domain = "https://www.feastogether.com.tw";

    @JsonProperty("pathFir")
    @Builder.Default
    private String pathFir = "booking";

    @JsonProperty("pathSec")
    @Builder.Default
    private String pathSec = "result";

    @JsonProperty("yuuu")
    private String yuuu;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChargeItem {

        @JsonProperty("seq")
        private int seq;

        @JsonProperty("count")
        private int count;
    }
}
