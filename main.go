package main

import (
	"feastogether/client"
	"feastogether/config"
	"log"
	"net/http"

	"github.com/gin-gonic/gin"
)

func main() {
	// 讀取 config
	cfg, err := config.GetConfig("./")
	if err != nil {
		log.Println(err)
		return
	}

	// 取得 token
	token := client.GetToken(cfg.UserConfig)
	if token == "" {
		return
	}

	// svg code , 對應驗證碼
	var svgCode string

	r := gin.Default()
	r.LoadHTMLGlob("templates/*")

	// 返回 html
	r.GET("/", func(c *gin.Context) {
		c.HTML(http.StatusOK, "index.html", gin.H{})
	})

	// 取得 svg
	r.GET("/svg", func(c *gin.Context) {

		data := client.GetSVG(cfg.UserConfig, token)
		svgContent := data.Result.SVG
		svgCode = data.Result.Code
		c.Header("Content-Type", "image/svg+xml")
		c.Writer.Write([]byte(svgContent))
	})

	// 接收驗證碼 , 然後訂位
	r.POST("/svg", func(c *gin.Context) {
		var Verify struct {
			ID string `json:"verify"`
		}
		c.ShouldBindJSON(&Verify)

		log.Println("執行訂位")
		// 立即訂位 , 取得訂位開始 - 過期時間
		expirationTime := client.GetSaveSeats(
			cfg.UserConfig,
			token,
			cfg.RestaurantConfig,
			svgCode,
			Verify.ID,
		)

		// 判斷是否取得訂位開始時間
		if expirationTime != "" {
			// 確認定位
			msg := client.SaveBooking(
				cfg.UserConfig,
				client.GetToken(cfg.UserConfig),
				cfg.RestaurantConfig)

			log.Println(msg)
		}

	})

	r.Run() // default :8080

}
