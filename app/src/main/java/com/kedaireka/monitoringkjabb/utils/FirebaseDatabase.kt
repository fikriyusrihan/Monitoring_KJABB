package com.kedaireka.monitoringkjabb.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseDatabase {
    companion object {
        val DATABASE_REFERENCE =
            Firebase.database("https://monitoring-kjabb-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    }
}