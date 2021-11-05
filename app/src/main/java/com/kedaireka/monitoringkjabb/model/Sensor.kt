package com.kedaireka.monitoringkjabb.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sensor(
    var name: String,
    var value: String,
    var unit: String,
    var created_at: String,
    var urlIcon: String? = ""
) : Parcelable