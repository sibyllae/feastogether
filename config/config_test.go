package config_test

import (
	"feastogether/config"
	"testing"
)

func TestGetConfig(t *testing.T) {
	config, err := config.GetConfig("..")
	if err != nil {
		t.Errorf("Failed to load config file: %v", err)
	}

	if config.UserConfig.Account == "" {
		t.Errorf("UserConfig.Account is empty")
	}

	if config.RestaurantConfig.StoreID == "" {
		t.Errorf("RestaurantConfig.StoreID is empty")
	}

	t.Log(config)
}
