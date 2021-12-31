package com.kedaireka.monitoringkjabb.ui.statistics.parameter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.utils.FirebaseDatabase.Companion.DATABASE_REFERENCE
import java.util.*

class AmmoniaFragmentViewModel : ViewModel() {

    companion object {
        private const val TAG = "DOFragmentViewModel"
    }

    private val _records = MutableLiveData<ArrayList<Sensor>>()
    val records: LiveData<ArrayList<Sensor>> = _records

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _allRecord = MutableLiveData<ArrayList<Sensor>>()
    val allRecord: LiveData<ArrayList<Sensor>> = _allRecord

    fun getDORecord(sensor: Sensor) {
        _isLoading.postValue(true)

        val dbRef = DATABASE_REFERENCE
        dbRef.child("sensors/${sensor.id}/records").orderByKey().limitToLast(10).get()
            .addOnSuccessListener { result ->
                val records = arrayListOf<Sensor>()
                for (document in result.children) {
                    val id = sensor.id
                    val name = sensor.name
                    val value = document.child("value").value.toString()
                    val unit = sensor.unit
                    val createdAt =
                        Timestamp(Date(document.child("created_at").value.toString().toLong()))
                    val urlIcon = sensor.urlIcon

                    records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
                }
                records.reverse()

                _isLoading.postValue(false)
                _records.postValue(records)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun getAllDORecord(sensor: Sensor) {

        val dbRef = DATABASE_REFERENCE
        dbRef.child("sensors/${sensor.id}/records").orderByKey().limitToLast(10).get()
            .addOnSuccessListener { result ->
                val records = arrayListOf<Sensor>()
                for (document in result.children) {
                    val id = sensor.id
                    val name = sensor.name
                    val value = document.child("value").value.toString()
                    val unit = sensor.unit
                    val createdAt =
                        Timestamp(Date(document.child("created_at").value.toString().toLong()))
                    val urlIcon = sensor.urlIcon

                    records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
                }
                records.reverse()

                _isLoading.postValue(false)
                _allRecord.postValue(records)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }
}