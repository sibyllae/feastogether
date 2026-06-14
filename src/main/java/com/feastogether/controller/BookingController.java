package com.feastogether.controller;

import com.feastogether.dto.SvgResponse;
import com.feastogether.dto.VerifyRequest;
import com.feastogether.service.FeastogetherApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 訂位控制器。
 * 對應 Go 版 main.go 中的 Gin 路由。
 *
 * GET  /     → 回傳 index.html（Thymeleaf）
 * GET  /svg  → 取得 SVG 驗證碼圖片
 * POST /svg  → 接收驗證碼，執行訂位
 */
@Slf4j
@Controller
public class BookingController {

    private final FeastogetherApiClient apiClient;

    /** 快取的 JWT Token，啟動時取得 */
    private String token;

    /** 當前 SVG 的後端驗證序號 */
    private String svgCode;

    public BookingController(FeastogetherApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * 初始化 Token。
     * 由 FeastogetherApplication 在啟動時呼叫。
     */
    public void initToken() {
        this.token = apiClient.getToken();
        if (this.token == null) {
            log.error("啟動時取得 Token 失敗，請確認帳號密碼設定");
        }
    }

    /**
     * 首頁 — 顯示驗證碼輸入頁面。
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 取得 SVG 驗證碼圖片。
     * 對應 Go 版 GET /svg 路由。
     */
    @GetMapping(value = "/svg", produces = "image/svg+xml")
    @ResponseBody
    public ResponseEntity<String> getSvg() {
        SvgResponse svgResponse = apiClient.getSvg(token);

        if (svgResponse == null || svgResponse.getResult() == null) {
            return ResponseEntity.internalServerError()
                    .body("<svg><text>取得驗證碼失敗</text></svg>");
        }

        // 存下後端驗證序號，供 POST /svg 使用
        this.svgCode = svgResponse.getResult().getCode();

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/svg+xml"))
                .body(svgResponse.getResult().getSvg());
    }

    /**
     * 接收驗證碼，執行訂位流程。
     * 對應 Go 版 POST /svg 路由。
     *
     * 流程：saveSeats（鎖定座位）→ saveBooking（確認訂位）
     */
    @PostMapping("/svg")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> postVerify(@RequestBody VerifyRequest verifyRequest) {
        log.info("執行訂位");

        // Step 1: 鎖定座位
        String expirationTime = apiClient.saveSeats(token, svgCode, verifyRequest.getVerify());

        if (expirationTime == null || expirationTime.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "鎖定座位失敗"
            ));
        }

        // Step 2: 重新取得 Token 並確認訂位
        String newToken = apiClient.getToken();
        String resultMsg = apiClient.saveBooking(newToken != null ? newToken : token);

        if (resultMsg != null) {
            log.info("訂位成功: {}", resultMsg);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", resultMsg
            ));
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "確認訂位失敗"
            ));
        }
    }
}
