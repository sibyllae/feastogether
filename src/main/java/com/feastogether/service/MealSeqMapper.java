package com.feastogether.service;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * mealTime → mealSeq 對照。
 * 對應 Go 版 client.MealSeqMap。
 */
@Component
public class MealSeqMapper {

    private static final Map<String, Integer> MEAL_SEQ_MAP = Map.of(
            "11:30", 1,
            "12:30", 1,
            "14:30", 3,
            "17:30", 4,
            "18:00", 4,
            "18:30", 4
    );

    /**
     * 根據用餐時間取得對應的 mealSeq。
     *
     * @param mealTime 用餐時間，例如 "11:30"
     * @return mealSeq 值
     * @throws IllegalArgumentException 若 mealTime 不在對照表中
     */
    public int getSeq(String mealTime) {
        Integer seq = MEAL_SEQ_MAP.get(mealTime);
        if (seq == null) {
            throw new IllegalArgumentException("不支援的用餐時間: " + mealTime);
        }
        return seq;
    }
}
