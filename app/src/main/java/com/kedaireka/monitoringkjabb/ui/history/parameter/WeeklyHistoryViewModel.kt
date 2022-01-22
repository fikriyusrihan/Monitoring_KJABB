package com.kedaireka.monitoringkjabb.ui.history.parameter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kedaireka.monitoringkjabb.model.Sensor
import java.util.*

class WeeklyHistoryViewModel : ViewModel() {
    private val _records = MutableLiveData<ArrayList<Sensor>>()
    val records: LiveData<ArrayList<Sensor>> = _records

    private val _avg = MutableLiveData<Double>()
    val avg: LiveData<Double> = _avg

    private val _min = MutableLiveData<Double>()
    val min: LiveData<Double> = _min

    private val _max = MutableLiveData<Double>()
    val max: LiveData<Double> = _max

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getSensorHistory(sensor: Sensor) {
        _isLoading.postValue(true)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val dbRef =
            Firebase.database("https://monitoring-kjabb-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("sensors/${sensor.id}/records")
        dbRef.orderByKey().startAfter(((calendar.timeInMillis - 604800000) / 1000).toString())
            .get()
            .addOnSuccessListener { result ->
                val records = arrayListOf<Sensor>()
                var min = Double.MAX_VALUE
                var max = Double.MIN_VALUE
                var counter = 0.0
                var avg = 0.0

                for (document in result.children) {
                    val id = sensor.id
                    val name = sensor.name
                    val value = document.child("value").value.toString()
                    val unit = sensor.unit
                    val createdAt = Timestamp(
                        Date(
                            document.child("created_at").value.toString().toLong() * 1000
                        )
                    )
                    val urlIcon = sensor.urlIcon

                    records.add(Sensor(id, name, value, unit, createdAt, urlIcon))


                    val valueInDouble = value.toDouble()
                    counter += valueInDouble

                    if (valueInDouble < min) {
                        min = valueInDouble
                    }
                    if (valueInDouble > max) {
                        max = valueInDouble
                    }
                }

                avg = counter / records.size

                _records.postValue(records)
                _min.postValue(min)
                _max.postValue(max)
                _avg.postValue(avg)

            }

        _isLoading.value = false

    }
}