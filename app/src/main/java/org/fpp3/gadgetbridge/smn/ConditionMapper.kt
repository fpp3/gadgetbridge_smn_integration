package org.fpp3.gadgetbridge.smn

object ConditionMapper {
    fun toOpenWeatherCode(smnCode: Int): Int = when (smnCode) {
        0 -> 800
        1, 2, 15, 16 -> 803
        3, 6, 11, 13 -> 500
        4, 14 -> 211
        5, 7, 17, 19 -> 601
        9, 10, 12, 18 -> 804
        20 -> 771
        else -> 800
    }
}
