---
name: feastogether
description: 此 skill 提供 feastogether 專案的完整上下文知識，包含架構、API 端點、資料結構、核心函式與已知問題。適用於以下情境：修改訂位邏輯、新增餐廳支援、除錯 API 呼叫失敗、更新 HTTP Header 偽裝參數、改善前端驗證碼 UI，或任何需要理解此 Go 語言自動訂位工具的開發工作。
metadata:
  author: zhong1016
  version: "1.0"
  language: Go 1.19
  framework: Gin
compatibility: Go 1.19+, Docker
allowed-tools: view_file run_command grep_search
---

## 專案概述

**Feastogether** 是一個針對「饗賓餐旅」線上訂位系統的 Go 語言自動訂位輔助工具，幫助使用者搶訂熱門餐廳（饗食天堂、旭集、饗饗）的座位。採用「程式自動化 + 人工輸入驗證碼」的半自動化策略，規避 CAPTCHA 限制。

- **專案路徑**：`c:\SideProject\2026\feastogether`
- **語言**：Go 1.19 / **框架**：Gin / **Config**：viper + ini
- **部署**：支援 Docker / Docker-compose / Local (`go run main.go`)

---

## 目錄結構

```
feastogether/
├── main.go                  # 入口：讀取 config → GetToken → 啟動 Gin Server
├── config.ini               # 用戶帳密、GA Cookie、餐廳訂位參數
├── config/
│   ├── config_handler.go    # GetConfig()：viper 讀 ini，解析 UserConfig + RestaurantConfig
│   └── config_test.go
├── client/
│   ├── booking_handler.go   # GetToken / GetSVG / GetSaveSeats / SaveBooking
│   ├── booking_model.go     # 所有 Request/Response struct 定義
│   └── booking_test.go
├── fetch/
│   ├── fetch_handler.go     # Post()：HTTP POST 封裝 + addHeader()（偽裝 UA/Cookie）
│   └── fetch_test.go
├── templates/
│   └── index.html           # 顯示 SVG 驗證碼 + 輸入框 + 訂位按鈕
├── Dockerfile               # golang:1.19.1，EXPOSE 8080，CMD go run main.go
└── docker-compose.yml
```

---

## 訂位流程

```
程式啟動
  ├─ config.GetConfig("./")       → 讀取 config.ini
  └─ client.GetToken(cfg.User)    → POST LOGIN_API → JWT Token

Gin Server :8080
  ├─ GET  /     → 回傳 index.html
  ├─ GET  /svg  → client.GetSVG()  → 取得 SVG 圖片 + 存 svgCode
  └─ POST /svg  → 接收使用者輸入的驗證碼文字 (verify)
        ├─ client.GetSaveSeats(...)  → POST SAVE_SEATS_API → 鎖定座位 → expirationTime
        └─ (成功) client.SaveBooking(...) → POST BOOKING_API → 訂位確認
```

---

## 官方 API 端點

| 常數 | URL | 說明 |
|------|-----|------|
| `LOGIN_API` | `.../custSignIn` | 登入，取得 JWT Token |
| `FA_SVG` | `.../get2FASvgByBrand` | 取得 SVG 驗證碼與對應 code |
| `SAVE_SEATS_API` | `.../api/booking/saveSeats` | 鎖定座位（第一步） |
| `BOOKING_API` | `.../api/booking/booking` | 確認訂位（第二步） |

> API base UUID 路徑：`994f5388-d001-4ca4-a7b1-72750d4211cf`（若官方更換會失效）

---

## 核心函式

### `config.GetConfig(path string) (*Config, error)`
讀取 `config.ini`，回傳含 `UserConfig` 與 `RestaurantConfig` 的 `*Config`。

### `client.GetToken(user UserConfig) string`
- Body: `{act, pwd, memberAccessToken: ""}`
- 成功：回傳 `result.customerLoginResp.token`（JWT）
- 失敗：回傳 `""`

### `client.GetSVG(user UserConfig, token string) *SvgResponse`
- Body: `{"brandId":"BR00008"}`
- 回傳 `result.svg`（SVG 圖片字串）和 `result.code`（後端驗證序號）

### `client.GetSaveSeats(user, token, config, code, fa string) string`
- `code`：GetSVG 回傳的後端序號；`fa`：使用者肉眼輸入的驗證碼
- 成功：回傳 `result.expirationTime`

### `client.SaveBooking(user, token, config) string`
- 需帶 `chargeList`：大人 `{seq:201, count:N}`、小孩 `{seq:202, count:0}`
- 成功：回傳訂位確認訊息 `message`

### `fetch.Post(api, data, user, token) (*http.Response, error)`
底層 HTTP POST，呼叫 `addHeader()` 自動加入所有必要 Header。

---

## HTTP Header 偽裝（fetch/fetch_handler.go）

```go
authorization: Bearer <token>
act: <user.Account>
Cookie: _ga=<ga>; _ga_9PQXQP3QD6=<ga_9PQXQP3QD6>
Content-Type: application/json
authority: www.feastogether.com.tw
user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) ... Chrome/111.0.0.0
referer: https://www.feastogether.com.tw/booking/Inparadise/search
sec-ch-ua: "Google Chrome";v="111", ...
sec-fetch-mode: cors / sec-fetch-site: same-origin
```

---

## config.ini 結構

```ini
[user]
account = <手機號碼>
password = <密碼>
ga = <_ga cookie>
ga_9PQXQP3QD6 = <_ga_9PQXQP3QD6 cookie>

[restaurant]
storeID = S2212290001      # 見下方 Store ID 對照表
peopleCount = 2
mealPeriod = lunch         # lunch / tea / dinner
mealDate = 2023-01-01      # yyyy-mm-dd
mealTime = 11:30           # 見下方時間對照表
```

### 餐廳 Store ID

| 品牌 | 分店 | storeID |
|------|------|---------|
| 饗食天堂 | 京站 / 台中 / 新竹 / 中壢 / 西門 / 三多 / 板橋 / 新光 / 信義 / 夢時代 | S2212290001 ~ S2212290012, S2212290062 |
| 旭集 | 信義 / 義享 / 新竹 | S2212290042 / S2212290047 / S2212290053 |
| 饗饗 | 信義 / 新莊 | S2212290004 / S2212290068 |

### mealTime → mealSeq 對照（MealSeqMap）

| mealTime | mealSeq |
|----------|---------|
| 11:30 / 12:30 | 1 |
| 14:30 | 3 |
| 17:30 / 18:00 / 18:30 | 4 |

---

## 已知問題（Gotchas）

1. **GetSVG decode bug**：`if json.NewDecoder(resp.Body).Decode(&data); err != nil` 缺少 `err =`，decode 錯誤永遠不會被捕捉。
2. **硬編碼參數**：
   - `Zked: "1j6ul4y94ejru6xk7vu4vu4"`（SaveSeats 必填，意義不明）
   - `Yuuu: "892389djdj883831445"`（3/30 新增，Booking 必填）
   - `StoreCode: "NTBQ"`（SaveBooking 固定值）
   - 若官方更換這些值，訂位將失敗。
3. **GA Cookie 手動維護**：`_ga` 和 `_ga_9PQXQP3QD6` 需從瀏覽器 DevTools 手動複製，過期後需更新。
4. **Config 安全性**：`config.ini` 含明文帳密，確認 `.gitignore` 已忽略此檔案。
5. **statusCode 判斷**：官方成功回傳 `statusCode: 1000`；非 1000 皆視為失敗並印出 `result.msg`。

---

## 執行指令

```bash
# Local
go mod download && go run main.go

# Docker
docker build -t feastogether . && docker run -it -p 8080:8080 feastogether

# Docker Compose
docker-compose up

# 測試
go test ./...
```
