package fetch_test

import (
	"encoding/json"
	"feastogether/config"
	"feastogether/fetch"
	"fmt"
	"log"
	"testing"
)

// 查看用餐情況
const (
	API = "https://www.feastogether.com.tw/api/booking/getStoreBookingSituation"
)

type Situation struct {
	StoreID     string `json:"storeId"`
	MealPeriod  string `json:"mealPeriod"`
	PeopleCount int    `json:"peopleCount"`
}

func TestPost(t *testing.T) {
	if cfg, err := config.GetConfig(".."); err != nil {
		log.Println(err)
	} else {

		situation := Situation{
			StoreID:     "S2212290010",
			MealPeriod:  "dinner",
			PeopleCount: 2,
		}

		payload, err := json.Marshal(situation)
		if err != nil {
			panic("Failed to marshal struct to JSON: " + err.Error())
		}

		resp, err := fetch.Post(API, payload, cfg.UserConfig, "")
		if err != nil {
			panic(err)
		}
		defer resp.Body.Close()

		var data map[string]interface{}
		if err = json.NewDecoder(resp.Body).Decode(&data); err != nil {
			panic(err)
		}

		fmt.Println(data)
	}
}
