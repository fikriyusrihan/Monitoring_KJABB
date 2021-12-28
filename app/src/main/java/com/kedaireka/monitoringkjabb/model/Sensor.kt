package com.kedaireka.monitoringkjabb.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sensor(
    var id: String,
    var name: String,
    var value: String,
    var unit: String,
    var created_at: Timestamp,
    var urlIcon: String? = ""
) : Parcelable