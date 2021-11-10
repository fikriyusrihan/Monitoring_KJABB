package com.kedaireka.monitoringkjabb.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kedaireka.monitoringkjabb.model.Sensor

class DetailSensorViewModel : ViewModel() {

    companion object {
        private const val TAG = "DetailSensorViewModel"
    }

    private val _dataSensor = MutableLiveData<ArrayList<Sensor>>()
    val dataSensor: LiveData<ArrayList<Sensor>> = _dataSensor

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading

    fun getSensorRecords(sensor: Sensor) {
        _isLoading.value = true
        val db = Firebase.firestore
        db.collection("sensors").document(sensor.id).collection("records")
            .orderBy("created_at", Query.Direction.DESCENDING).limit(10L)
            .get()
            .addOnSuccessListener { result ->
                val records = arrayListOf<Sensor>()
                for (document in result) {
                    val id = sensor.id
                    val name = sensor.name
                    val value = document["value"].toString()
                    val unit = sensor.unit
                    val status = document["status"].toString().toInt()
                    val createdAt = document["created_at"] as Timestamp
                    val urlIcon = sensor.urlIcon
                    records.add(Sensor(id, name, value, unit, status, createdAt, urlIcon))
                }
                _isLoading.postValue(false)
                _dataSensor.postValue(records)
            }
            .addOnFailureListener {
                Log.d(TAG, "Error getting documents: ", it)
            }
    }
}