package com.feastogether.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 鎖定座位請求。
 * 對應 Go 版 client.SaveSeats struct。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveSeatsRequest {

    @JsonProperty("storeId")
    private String storeId;

    @JsonProperty("peopleCount")
    private int peopleCount;

    @JsonProperty("mealPeriod")
    private String mealPeriod;

    @JsonProperty("mealSeq")
    private int mealSeq;

    @JsonProperty("mealDate")
    private String mealDate;

    @JsonProperty("mealTime")
    private String mealTime;

    @JsonProperty("zked")
    private Object zked;

    @JsonProperty("svgCode")
    private String svgCode;

    @JsonProperty("scgVerifyStr")
    private String scgVerifyStr;
}
