package com.kedaireka.monitoringkjabb.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sensor(
    var name: String,
    var value: String,
    var unit: String,
    var status: Int,
    var created_at: Timestamp,
    var urlIcon: String? = ""
) : Parcelable