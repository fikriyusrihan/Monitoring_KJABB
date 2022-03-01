package com.kedaireka.monitoringkjabb.ui.statistics.parameter

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

class AmmoniaFragmentViewModel : ViewModel() {

    private val _records = MutableLiveData<ArrayList<Sensor>>()
    val records: LiveData<ArrayList<Sensor>> = _records

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _thresholds = MutableLiveData<Map<String, String>>()
    val thresholds = _thresholds

    private val _sensorRecordInRange = MutableLiveData<ArrayList<Sensor>>()
    val sensorRecordInRange: LiveData<ArrayList<Sensor>> = _sensorRecordInRange

    private val _avg = MutableLiveData<Double>()
    val avg: LiveData<Double> = _avg

    private val _min = MutableLiveData<Double>()
    val min: LiveData<Double> = _min

    private val _max = MutableLiveData<Double>()
    val max: LiveData<Double> = _max

    fun getDORecord(sensor: Sensor) {
        _isLoading.postValue(true)

        val dbRef = DATABASE_REFERENCE
        dbRef.child("sensors/${sensor.id}/records").orderByKey().limitToLast(10).get()
            .addOnSuccessListener { result ->
                val records = arrayListOf<Sensor>()
                var min = Double.MAX_VALUE
                var max = Double.MIN_VALUE
                var counter = 0.0

                for (document in result.children) {
                    try {
                        val id = sensor.id
                        val name = sensor.name
                        val value = document.child("value").value.toString()
                        val unit = sensor.unit
                        val createdAt =
                            Timestamp(
                                Date(
                                    document.child("created_at").value.toString().toLong() * 1000
                                )
                            )
                        val urlIcon = sensor.urlIcon

                        val valueInDouble = value.toDouble()
                        counter += valueInDouble

                        if (valueInDouble < min) {
                            min = valueInDouble
                        }
                        if (valueInDouble > max) {
                            max = valueInDouble
                        }

                        records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
                    } catch (e: Exception) {
                        Log.d(
                            AmmoniaFragmentViewModel::class.java.simpleName,
                            "getDORecord: ${e.message.toString()}"
                        )
                    }
                }
                records.reverse()

                val avg: Double = counter / records.size

                _isLoading.postValue(false)
                _records.postValue(records)
                _min.postValue(min)
                _max.postValue(max)
                _avg.postValue(avg)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun getSensorRecordInRange(sensor: Sensor, start: Long, end: Long) {
        val dbRef =
            Firebase.database("https://monitoring-kjabb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("sensors/${sensor.id}/records")

        dbRef.orderByKey().startAfter(start.toString()).endBefore(end.toString())
            .get().addOnSuccessListener { result ->
                val records = arrayListOf<Sensor>()
                Log.d("DetailSensorViewModel", result.childrenCount.toString())
                for (document in result.children) {
                    try {
                        val id = sensor.id
                        val name = sensor.name
                        val value = document.child("value").value.toString()
                        val unit = sensor.unit
                        val createdAt =
                            Timestamp(
                                Date(
                                    document.child("created_at").value.toString().toLong() * 1000
                                )
                            )
                        val urlIcon = sensor.urlIcon
                        records.add(Sensor(id, name, value, unit, createdAt, urlIcon))
                    } catch (e: Exception) {
                        Log.d(
                            AmmoniaFragmentViewModel::class.java.simpleName,
                            "getSensorRecordInRange: ${e.message.toString()}"
                        )
                    }
                }
                _sensorRecordInRange.postValue(records)
            }.addOnFailureListener {
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