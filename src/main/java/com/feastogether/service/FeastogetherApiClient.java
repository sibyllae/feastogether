package com.feastogether.service;

import com.feastogether.config.FeastogetherProperties;
import com.feastogether.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * 饗賓餐旅 API 客戶端。
 * 對應 Go 版 client/booking_handler.go + fetch/fetch_handler.go。
 * 使用 Spring RestClient（Spring 6.1+）取代 Go 的 net/http。
 */
@Slf4j
@Service
public class FeastogetherApiClient {

    private final FeastogetherProperties props;
    private final MealSeqMapper mealSeqMapper;
    private final RestClient restClient;

    public FeastogetherApiClient(FeastogetherProperties props, MealSeqMapper mealSeqMapper) {
        this.props = props;
        this.mealSeqMapper = mealSeqMapper;
        this.restClient = RestClient.builder()
                .defaultHeaders(this::addCommonHeaders)
                .build();
    }

    /**
     * 登入取得 JWT Token。
     * 對應 Go 版 client.GetToken()。
     *
     * @return JWT Token，失敗回傳 null
     */
    public String getToken() {
        var request = LoginRequest.builder()
                .account(props.getUser().getAccount())
                .password(props.getUser().getPassword())
                .build();

        try {
            ApiResponse response = restClient.post()
                    .uri(props.getApi().getLogin())
                    .headers(h -> addActHeader(h))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ApiResponse.class);

            if (response != null && response.isSuccess()) {
                String token = response.getResult().getCustomerLoginResp().getToken();
                log.info("登入成功，取得 Token");
                return token;
            } else {
                String msg = response != null && response.getResult() != null
                        ? response.getResult().getMsg() : "未知錯誤";
                log.error("登入失敗: {}", msg);
                return null;
            }
        } catch (Exception e) {
            log.error("登入請求失敗: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 取得 SVG 驗證碼。
     * 對應 Go 版 client.GetSVG()。
     *
     * @param token JWT Token
     * @return SvgResponse，失敗回傳 null
     */
    public SvgResponse getSvg(String token) {
        Map<String, String> body = Map.of("brandId", props.getParams().getBrandId());

        try {
            SvgResponse response = restClient.post()
                    .uri(props.getApi().getSvg())
                    .headers(h -> {
                        addActHeader(h);
                        addAuthHeader(h, token);
                    })
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(SvgResponse.class);

            if (response != null && response.isSuccess()) {
                log.info("取得驗證碼成功");
                return response;
            } else {
                log.error("取得驗證碼失敗: {}", response);
                return null;
            }
        } catch (Exception e) {
            log.error("取得驗證碼請求失敗: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 鎖定座位（訂位第一步）。
     * 對應 Go 版 client.GetSaveSeats()。
     *
     * @param token     JWT Token
     * @param svgCode   GetSVG 回傳的後端驗證序號
     * @param verifyStr 使用者肉眼輸入的驗證碼
     * @return expirationTime，失敗回傳 null
     */
    public String saveSeats(String token, String svgCode, String verifyStr) {
        var restaurant = props.getRestaurant();

        var request = SaveSeatsRequest.builder()
                .storeId(restaurant.getStoreId())
                .peopleCount(restaurant.getPeopleCount())
                .mealPeriod(restaurant.getMealPeriod())
                .mealDate(restaurant.getMealDate())
                .mealTime(restaurant.getMealTime())
                .mealSeq(mealSeqMapper.getSeq(restaurant.getMealTime()))
                .zked(props.getParams().getZked())
                .svgCode(svgCode)
                .scgVerifyStr(verifyStr)
                .build();

        try {
            ApiResponse response = restClient.post()
                    .uri(props.getApi().getSaveSeats())
                    .headers(h -> {
                        addActHeader(h);
                        addAuthHeader(h, token);
                    })
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ApiResponse.class);

            if (response != null && response.isSuccess()) {
                String expTime = response.getResult().getExpirationTime();
                log.info("鎖定座位成功，過期時間: {}", expTime);
                return expTime;
            } else {
                String msg = response != null && response.getResult() != null
                        ? response.getResult().getMsg() : "未知錯誤";
                log.error("鎖定座位失敗: {}", msg);
                return null;
            }
        } catch (Exception e) {
            log.error("鎖定座位請求失敗: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 確認訂位（訂位第二步）。
     * 對應 Go 版 client.SaveBooking()。
     *
     * @param token JWT Token
     * @return 訂位確認訊息，失敗回傳 null
     */
    public String saveBooking(String token) {
        var restaurant = props.getRestaurant();

        var request = BookingRequest.builder()
                .storeId(restaurant.getStoreId())
                .mealPeriod(restaurant.getMealPeriod())
                .mealDate(restaurant.getMealDate())
                .mealTime(restaurant.getMealTime())
                .mealSeq(mealSeqMapper.getSeq(restaurant.getMealTime()))
                .adult(restaurant.getPeopleCount())
                .chargeList(List.of(
                        // 大人
                        BookingRequest.ChargeItem.builder()
                                .seq(201)
                                .count(restaurant.getPeopleCount())
                                .build(),
                        // 小孩
                        BookingRequest.ChargeItem.builder()
                                .seq(202)
                                .count(0)
                                .build()
                ))
                .storeCode(props.getParams().getStoreCode())
                .yuuu(props.getParams().getYuuu())
                .build();

        try {
            ApiResponse response = restClient.post()
                    .uri(props.getApi().getBooking())
                    .headers(h -> {
                        addActHeader(h);
                        addAuthHeader(h, token);
                    })
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ApiResponse.class);

            if (response != null && response.isSuccess()) {
                log.info("訂位成功: {}", response.getMessage());
                return response.getMessage();
            } else {
                String msg = response != null && response.getResult() != null
                        ? response.getResult().getMsg() : "未知錯誤";
                log.error("訂位失敗: {}", msg);
                return null;
            }
        } catch (Exception e) {
            log.error("訂位請求失敗: {}", e.getMessage(), e);
            return null;
        }
    }

    // ===== Private: Header 偽裝 =====

    /**
     * 通用 Header（所有請求共用）。
     * 對應 Go 版 fetch.addHeader() 中的固定部分。
     */
    private void addCommonHeaders(HttpHeaders headers) {
        headers.set("authority", "www.feastogether.com.tw");
        headers.set("accept-language", "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        headers.set("cache-control", "no-cache");
        headers.set("origin", "https://www.feastogether.com.tw");
        headers.set("pragma", "no-cache");
        headers.set("referer", "https://www.feastogether.com.tw/booking/Inparadise/search");
        headers.set("sec-ch-ua", "\"Google Chrome\";v=\"111\", \"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"111\"");
        headers.set("sec-ch-ua-mobile", "?0");
        headers.set("sec-ch-ua-platform", "\"macOS\"");
        headers.set("sec-fetch-dest", "empty");
        headers.set("sec-fetch-mode", "cors");
        headers.set("sec-fetch-site", "same-origin");
        headers.set("user-agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36");
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    }

    /** 加入 act header 與 Cookie（GA cookies）。 */
    private void addActHeader(HttpHeaders headers) {
        var user = props.getUser();
        headers.set("act", user.getAccount());
        headers.set("Cookie", String.format("_ga=%s; _ga_9PQXQP3QD6=%s",
                user.getGa(), user.getGa9pqxqp3qd6()));
    }

    /** 加入 Authorization Bearer Token。 */
    private void addAuthHeader(HttpHeaders headers, String token) {
        if (token != null && !token.isEmpty()) {
            headers.set("authorization", "Bearer " + token);
        }
    }
}
