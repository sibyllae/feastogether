package client

import (
	"encoding/json"
	"feastogether/config"
	"feastogether/fetch"
	"log"
)

// api
const (
	LOGIN_API      = "https://www.feastogether.com.tw/api/994f5388-d001-4ca4-a7b1-72750d4211cf/custSignIn"
	FA_SVG         = "https://www.feastogether.com.tw/api/994f5388-d001-4ca4-a7b1-72750d4211cf/get2FASvgByBrand"
	SAVE_SEATS_API = "https://www.feastogether.com.tw/api/booking/saveSeats"
	BOOKING_API    = "https://www.feastogether.com.tw/api/booking/booking"
)

var MealSeqMap = map[string]int{
	"11:30": 1,
	"12:30": 1,
	"14:30": 3,
	"17:30": 4,
	"18:00": 4,
	"18:30": 4,
}

// 取得 Token
func GetToken(user config.UserConfig) string {

	payload := Login{
		Act:               user.Account,
		Pwd:               user.Password,
		MemberAccessToken: "",
	}

	payloadBytes, err := json.Marshal(payload)
	if err != nil {
		log.Printf("Failed to marshal struct to JSON:%v\n", err)
		return ""
	}

	resp, err := fetch.Post(LOGIN_API, payloadBytes, user, "")
	if err != nil {
		log.Println(err)
		return ""
	}
	defer resp.Body.Close()

	var data Response
	if err = json.NewDecoder(resp.Body).Decode(&data); err != nil {
		log.Printf("Failed to decode response body: %v\n", err)
		return ""
	}
	if data.StatusCode != 1000 {
		log.Println(data.Result.Msg)
		return ""
	}
	return data.Result.CustomerLoginResp.Token
}

// 取得驗證序號
func GetSVG(user config.UserConfig, token string) *SvgResponse {

	payload := []byte(`{"brandId":"BR00008"}`)

	resp, err := fetch.Post(FA_SVG, payload, user, token)
	if err != nil {
		log.Println(err)
		return nil
	}

	defer resp.Body.Close()

	var data SvgResponse
	if json.NewDecoder(resp.Body).Decode(&data); err != nil {
		log.Printf("Failed to decode response body: %v\n", err)
		return nil
	}

	if data.StatusCode != 1000 {
		log.Printf("取得驗證序號失敗 : %v\n", data)
		return nil
	}
	return &data
}

// 立即定位
func GetSaveSeats(user config.UserConfig, token string, payload config.RestaurantConfig, code string, fa string) string {

	saveSeats := SaveSeats{
		StoreID:     payload.StoreID,
		PeopleCount: payload.PeopleCount,
		MealPeriod:  payload.MealPeriod,
		MealDate:    payload.MealDate,
		MealTime:    payload.MealTime,
		MealSeq:     MealSeqMap[payload.MealTime],
		Zked:        "1j6ul4y94ejru6xk7vu4vu4",

		SvgCode:      code,
		ScgVerifyStr: fa,
	}

	payloadBytes, err := json.Marshal(saveSeats)
	if err != nil {
		log.Printf("Failed to marshal struct to JSON:%v\n", err)
		return ""
	}

	resp, err := fetch.Post(SAVE_SEATS_API, payloadBytes, user, token)
	if err != nil {
		log.Println(err)
		return ""
	}

	defer resp.Body.Close()

	var data Response
	if json.NewDecoder(resp.Body).Decode(&data); err != nil {
		log.Printf("Failed to decode response body: %v\n", err)
		return ""
	}

	if data.StatusCode != 1000 {
		log.Println(data.Result.Msg)
		return ""
	}

	return data.Result.ExpirationTime
}

// 送出定位
func SaveBooking(user config.UserConfig, token string, payload config.RestaurantConfig) string {

	booking := Booking{
		StoreID:     payload.StoreID,
		MealPeriod:  payload.MealPeriod,
		MealDate:    payload.MealDate,
		MealTime:    payload.MealTime,
		MealPurpose: "",
		MealSeq:     MealSeqMap[payload.MealTime],
		Special:     0,
		ChildSeat:   0,
		Adult:       payload.PeopleCount,
		Child:       0,
		ChargeList: []struct {
			Seq   int "json:\"seq\""
			Count int "json:\"count\""
		}{
			// 大人
			{
				Seq:   201,
				Count: payload.PeopleCount,
			},
			// 小孩
			{
				Seq:   202,
				Count: 0,
			},
		},
		StoreCode:    "NTBQ",
		RedirectType: "iEat_card",
		Domain:       "https://www.feastogether.com.tw",
		PathFir:      "booking",
		PathSec:      "result",
		// Yuuu: GetB00king(user, token),

		// 3/30 新參數
		Yuuu: "892389djdj883831445",
	}

	payloadBytes, err := json.Marshal(booking)
	if err != nil {
		log.Printf("Failed to marshal struct to JSON:%v\n", err)
		return ""
	}

	resp, err := fetch.Post(BOOKING_API, payloadBytes, user, token)
	if err != nil {
		log.Println(err)
		return ""
	}
	defer resp.Body.Close()

	var data Response
	if json.NewDecoder(resp.Body).Decode(&data); err != nil {
		log.Printf("Failed to decode response body: %v\n", err)
		return ""
	}

	if data.StatusCode != 1000 {
		log.Println(data.Result.Msg)
		return ""
	}
	return data.Message
}
