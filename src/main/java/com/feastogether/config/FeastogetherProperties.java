package com.feastogether.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 饗賓餐旅設定綁定。
 * 對應 application.yml 中 feastogether.* 的所有設定項目。
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "feastogether")
public class FeastogetherProperties {

    private ApiProperties api = new ApiProperties();
    private UserProperties user = new UserProperties();
    private RestaurantProperties restaurant = new RestaurantProperties();
    private ParamProperties params = new ParamProperties();

    @Data
    public static class ApiProperties {
        /** 登入 API */
        private String login;
        /** SVG 驗證碼 API */
        private String svg;
        /** 鎖定座位 API */
        private String saveSeats;
        /** 確認訂位 API */
        private String booking;
    }

    @Data
    public static class UserProperties {
        /** 用戶帳號（手機號碼） */
        private String account;
        /** 用戶密碼 */
        private String password;
        /** _ga cookie */
        private String ga;
        /** _ga_9PQXQP3QD6 cookie */
        private String ga9pqxqp3qd6;
    }

    @Data
    public static class RestaurantProperties {
        /** 餐廳門店 ID */
        private String storeId;
        /** 用餐人數 */
        private int peopleCount;
        /** 用餐時段：lunch / tea / dinner */
        private String mealPeriod;
        /** 用餐日期：yyyy-MM-dd */
        private String mealDate;
        /** 用餐時間：11:30 / 12:00 / 14:30 / 17:30 / 18:00 / 18:30 */
        private String mealTime;
    }

    @Data
    public static class ParamProperties {
        /** SaveSeats 必填硬編碼參數 */
        private String zked;
        /** Booking 必填硬編碼參數（3/30 新增） */
        private String yuuu;
        /** Booking storeCode */
        private String storeCode;
        /** SVG brandId */
        private String brandId;
    }
}
