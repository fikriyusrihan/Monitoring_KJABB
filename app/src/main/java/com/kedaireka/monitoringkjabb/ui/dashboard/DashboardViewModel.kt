package com.kedaireka.monitoringkjabb.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import com.kedaireka.monitoringkjabb.model.Sensor

class DashboardViewModel : ViewModel() {

    companion object {
        private const val TAG = "DashboardViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _data = MutableLiveData<ArrayList<Sensor>>()
    val data : LiveData<ArrayList<Sensor>> = _data

    init {
        getSensorsData()
    }

    private fun getSensorsData() {
        _isLoading.postValue(true)
        val db = Firebase.firestore
        db.collection("sensors")
            .get()
            .addOnSuccessListener { result ->
                val sensorData = arrayListOf<Sensor>()
                for (document in result) {
                    val name = document["name"].toString()
                    val value = document["value"].toString()
                    val unit = document["unit"].toString()
                    val createdAt = document["created_at"].toString()
                    val urlIcon = document["url_icon"].toString()

                    sensorData.add(Sensor(name, value, unit, createdAt, urlIcon))
                }
                _data.postValue(sensorData)
                _isLoading.postValue(false)
            }
            .addOnFailureListener {
                Log.d(TAG, "Error getting documents: ", it)
            }
    }

}