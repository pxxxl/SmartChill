package com.minjer.smartchill.utils;

public class TemperatureUtil {
    // 指数衰减公式中的常数
    private final static double K = 0.1;
    /**
     *
     * @param initialTemperature 饮料初始温度
     * @param fridgeTemperature 冰箱温度
     * @param minutes 入柜时间(分钟)
     * @return 饮料冷却后的温度
     */
    public static double calCoolingTemperature(double initialTemperature, double fridgeTemperature, long minutes) {
        // 使用指数衰减公式计算冷却后的温度
        return fridgeTemperature + (initialTemperature - fridgeTemperature) * Math.exp(-K * minutes);
    }
}
