package com.kedaireka.monitoringkjabb.ui.statistics.parameter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kedaireka.monitoringkjabb.model.Sensor

class DOFragmentViewModel : ViewModel() {

    companion object {
        private const val TAG = "DOFragmentViewModel"
    }

    private val _records = MutableLiveData<ArrayList<Sensor>>()
    val records: LiveData<ArrayList<Sensor>> = _records

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getDORecord(sensor: Sensor) {
        _isLoading.postValue(true)
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
                _records.postValue(records)
            }
            .addOnFailureListener {
                Log.d(TAG, "Error getting documents: ", it)
            }
    }
}