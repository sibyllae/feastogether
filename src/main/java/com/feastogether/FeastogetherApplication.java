package com.feastogether;

import com.feastogether.controller.BookingController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * Feastogether Spring Boot 應用程式入口。
 * 啟動時自動呼叫 getToken() 取得 JWT Token。
 */
@Slf4j
@SpringBootApplication
public class FeastogetherApplication {

    private final BookingController bookingController;

    public FeastogetherApplication(BookingController bookingController) {
        this.bookingController = bookingController;
    }

    public static void main(String[] args) {
        SpringApplication.run(FeastogetherApplication.class, args);
    }

    /**
     * 應用啟動完成後，自動取得 Token。
     * 對應 Go 版 main() 中的 token := client.GetToken(cfg.UserConfig)。
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("=== Feastogether 啟動中 ===");
        bookingController.initToken();
        log.info("=== 請在瀏覽器開啟 http://localhost:8080 輸入驗證碼 ===");
    }
}
