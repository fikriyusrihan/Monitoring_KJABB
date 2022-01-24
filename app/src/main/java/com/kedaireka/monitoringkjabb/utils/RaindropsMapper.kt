package com.kedaireka.monitoringkjabb.utils

class RaindropsMapper {
    companion object {
        val RAINDROPS_DICT = mapOf<Int, String>(
            0 to "Tidak Hujan",
            1 to "Tidak Hujan",
            2 to "Gerimis",
            3 to "Sedang",
            4 to "Deras"
        )

        const val RAINDROPS_ID = "raindrops"
    }
}