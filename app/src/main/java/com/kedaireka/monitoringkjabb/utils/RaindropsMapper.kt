package com.kedaireka.monitoringkjabb.utils

import com.kedaireka.monitoringkjabb.R

class RaindropsMapper {
    companion object {
        val RAINDROPS_DICT = mapOf(
            0 to R.string.tidak_hujan,
            1 to R.string.noise,
            2 to R.string.gerimis,
            3 to R.string.sedang,
            4 to R.string.tinggi
        )

        const val RAINDROPS_ID = "raindrops"
    }
}