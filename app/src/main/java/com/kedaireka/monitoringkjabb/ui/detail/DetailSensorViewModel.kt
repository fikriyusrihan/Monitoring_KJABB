package com.kedaireka.monitoringkjabb.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.utils.FirebaseDatabase.Companion.DATABASE_REFERENCE
import java.util.*

class DetailSensorViewModel : ViewModel() {

    companion object {
        private const val TAG = "DetailSensorViewModel"
    }

    private val _sensorRecordInRange = MutableLiveData<ArrayList<Sensor>>()
    val sensorRecordInRange: LiveData<ArrayList<Sensor>> = _sensorRecordInRange

    private val _dataSensor = MutableLiveData<ArrayList<Sensor>>()
    val dataSensor: LiveData<ArrayList<Sensor>> = _dataSensor

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading

    private val _thresholds = MutableLiveData<Map<String, String>>()
    val thresholds = _thresholds

    fun getSensorRecordInRange(sensor: Sensor, start: Long, end: Long) {
        val dbRef =
            Firebase.database("https://monitoring-kjabb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("sensors/${sensor.id}/records")

        dbRef.orderByKey().startAfter(start.toString()).endBefore(end.toString())
            .get().addOnSuccessListener { result ->
                val records = arrayListOf<Sensor>()
                Log.d("DetailSensorViewModel", result.childrenCount.toString())
                for (document in result.children) {
                    val id = sensor.id
                    val name = sensor.name
                    val value = document.child("value").value.toString()
                    val unit = sensor.unit
                    val createdAt =
                        Timestamp(Date(document.child("created_at").value.toString().toLong() * 1000))
                    val urlIcon = sensor.urlIcon
                    records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
                }
                _sensorRecordInRange.postValue(records)
            }.addOnFailureListener {
                it.printStackTrace()
            }

    }

    fun getSensorRecords(sensor: Sensor) {
        _isLoading.value = true

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
                        Timestamp(Date(document.child("created_at").value.toString().toLong() * 1000))
                    val urlIcon = sensor.urlIcon

                    records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
                }
                records.reverse()

                _isLoading.postValue(false)
                _dataSensor.postValue(records)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun getThresholdsData(sensor: Sensor) {
        val dbRef = DATABASE_REFERENCE
        dbRef.child("sensors/${sensor.id}/thresholds").get().addOnSuccessListener { result ->
            val dataThreshold = mapOf(
                "upper" to result.child("upper").value.toString(),
                "lower" to result.child("lower").value.toString(),
            )

            _thresholds.postValue(dataThreshold)
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }
}