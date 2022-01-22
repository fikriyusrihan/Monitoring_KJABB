package com.kedaireka.monitoringkjabb.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.utils.FirebaseDatabase.Companion.DATABASE_REFERENCE
import java.util.*
import kotlin.random.Random

class DashboardViewModel : ViewModel() {

    companion object {
        private const val TAG = "DashboardViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _data = MutableLiveData<ArrayList<Sensor>>()
    val data : LiveData<ArrayList<Sensor>> = _data

    private val _thresholdData = MutableLiveData<ArrayList<Map<String, Double>>>()
    val thresholdData = _thresholdData

    init {
//        createDummyRecords()
        getSensorsData()
    }

    private fun getSensorsData() {
        _isLoading.postValue(true)

        val refRealtimeDatabase = DATABASE_REFERENCE
        refRealtimeDatabase.keepSynced(true)
        refRealtimeDatabase.child("sensors").get().addOnSuccessListener { result ->
            val sensorData = arrayListOf<Sensor>()
            val thresholdData = arrayListOf<Map<String, Double>>()
            for (sensor in result.children) {
                val id = sensor.key!!
                val name = sensor.child("data/name").value.toString()
                val value =
                    sensor.child("records").children.last().child("value").value.toString()
                val unit = sensor.child("data/unit").value.toString()
                val createdAt =
                    sensor.child("records").children.last()
                        .child("created_at").value.toString()
                val urlIcon = sensor.child("data/url_icon").value.toString()

                val createdAtTimestamp = Timestamp(Date(createdAt.toLong() * 1000))
                sensorData.add(Sensor(id, name, value, unit, createdAtTimestamp, urlIcon))

                val upper = sensor.child("thresholds/upper").value.toString().toDouble()
                val lower = sensor.child("thresholds/lower").value.toString().toDouble()

                thresholdData.add(hashMapOf("upper" to upper, "lower" to lower))
            }

            _thresholdData.postValue(thresholdData)
            _data.postValue(sensorData)
            _isLoading.postValue(false)
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    private fun createDummyRecords() {
        for (i in 0..1500) {
            val timeInMillis = Date().time - (1800000 * i)
            val db = DATABASE_REFERENCE
            val data = mutableMapOf<String, Any>()
            data["created_at"] = timeInMillis / 1000
            data["value"] = (Random.nextInt(22, 25))

            db.child("sensors/water_temperature/records/${timeInMillis / 1000}").setValue(data)
        }
    }

    private fun check() {
        val dbRef = DATABASE_REFERENCE
        dbRef.child("sensors/ammonia/records").orderByKey().limitToLast(10).get()
            .addOnSuccessListener { result ->
                for (document in result.children) {
                    Log.d("DetailSensorViewModel", document.key.toString())
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

}