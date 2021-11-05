package com.kedaireka.monitoringkjabb.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

class DashboardViewModel : ViewModel() {

    companion object {
        private const val TAG = "DashboardViewModel"
    }

    private val _text = MutableLiveData<String>().apply {
        value = "Dashboard"
    }
    val text: LiveData<String> = _text

    private val _data = MutableLiveData<Map<String, String>>()
    val data : LiveData<Map<String, String>> = _data

    init {
        getSensorsData()
    }

    private fun getSensorsData() {
        val db = Firebase.firestore
        db.collection("sensors")
            .get()
            .addOnSuccessListener { result ->
                val sensorData = mutableMapOf<String, String>()
                for (document in result) {
                    sensorData[document.id] = document["value"].toString()
                }
                _data.postValue(sensorData)
            }
            .addOnFailureListener {
                Log.d(TAG, "Error getting documents: ", it)
            }
    }

}