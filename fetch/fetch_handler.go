package fetch

import (
	"bytes"
	"feastogether/config"
	"fmt"
	"log"
	"net/http"
)

func Post(api string, data []byte, user config.UserConfig, token string) (*http.Response, error) {
	client := &http.Client{}
	req, err := http.NewRequest("POST", api, bytes.NewBuffer(data))
	if err != nil {
		log.Println("Failed to create http request object: ", err)
		return nil, err
	}

	// add header
	addHeader(req, user, token)

	if resp, err := client.Do(req); err != nil {
		log.Println("Failed to execute http request: ", err)
		return nil, err
	} else {
		return resp, err
	}
}

func addHeader(req *http.Request, user config.UserConfig, token string) *http.Request {

	if token != "" {
		req.Header.Set("authorization", fmt.Sprintf("Bearer %s", token))
	}

	req.Header.Set("act", user.Account)
	req.Header.Set("Cookie", fmt.Sprintf("_ga=%v; _ga_9PQXQP3QD6=%v", user.Ga, user.Ga_9PQXQP3QD6))

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Accept", "application/json")
	req.Header.Set("authority", "www.feastogether.com.tw")
	req.Header.Set("accept-language", "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7")
	req.Header.Set("cache-control", "no-cache")
	req.Header.Set("origin", "https://www.feastogether.com.tw")
	req.Header.Set("pragma", "no-cache")
	req.Header.Set("referer", "https://www.feastogether.com.tw/booking/Inparadise/search")
	req.Header.Set("sec-ch-ua", `"Google Chrome";v="111", "Not(A:Brand";v="8", "Chromium";v="111"`)
	req.Header.Set("sec-ch-ua-mobile", "?0")
	req.Header.Set("sec-ch-ua-platform", `"macOS"`)
	req.Header.Set("sec-fetch-dest", "empty")
	req.Header.Set("sec-fetch-mode", "cors")
	req.Header.Set("sec-fetch-site", "same-origin")
	req.Header.Set("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36")

	return req
}
