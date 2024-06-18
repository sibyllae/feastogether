package client_test

import (
	"feastogether/client"
	"feastogether/config"
	"log"
	"testing"
)

var cfg *config.Config

func init() {
	var err error
	cfg, err = config.GetConfig("..")
	if err != nil {
		log.Panicf("Failed to load config file: %v", err)
	}
}

func TestGetToken(t *testing.T) {
	if token := client.GetToken(cfg.UserConfig); token == "" {
		t.Errorf("Failed to get token")
	} else {
		t.Log(token)
	}
}
func TestGetSVG(t *testing.T) {
	if svg := client.GetSVG(
		cfg.UserConfig,
		client.GetToken(cfg.UserConfig)); svg == nil {
		t.Errorf("svg failed")
	} else {
		t.Log(svg)
	}
}

// 要先取得 SVG 驗證後執行 GetSaveSeats 取得訂位開始時間
func TestSaveBooking(t *testing.T) {

	if booking := client.SaveBooking(
		cfg.UserConfig,
		client.GetToken(cfg.UserConfig),
		cfg.RestaurantConfig); booking == "" {
		t.Errorf("Booking failed")
	} else {
		t.Log(booking)
	}
}
