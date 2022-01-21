package com.kedaireka.monitoringkjabb.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.Timestamp
import com.kedaireka.monitoringkjabb.R
import com.kedaireka.monitoringkjabb.activity.MainActivity
import com.kedaireka.monitoringkjabb.model.Sensor
import com.kedaireka.monitoringkjabb.utils.FirebaseDatabase.Companion.DATABASE_REFERENCE
import java.util.*

class ThresholdWarningReceiver : BroadcastReceiver() {

    companion object {
        private const val ID_THRESHOLD_WARNING = 102
    }

    override fun onReceive(context: Context, intent: Intent) {
        getSensorsData(context, ID_THRESHOLD_WARNING)
    }

    fun setRepeatingThresholdAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ThresholdWarningReceiver::class.java)

        val calendar = Calendar.getInstance()

        val pendingIntent = PendingIntent.getBroadcast(context, ID_THRESHOLD_WARNING, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            pendingIntent
        )

        Toast.makeText(context, R.string.threshold_warning_activated, Toast.LENGTH_SHORT).show()
    }

    private fun showAlarmNotification(context: Context, notificationId: Int, sensor: Sensor) {
        val CHANNEL_ID = "Channel_2"
        val CHANNEL_NAME = "Threshold Warning Notification"

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val mBuilder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("Peringatan Threshold")
            .setContentText("Sensor mendeteksi nilai di luar batas aman")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${sensor.name} berada diluar batas aman dengan nilai saat ini ${sensor.value} ${sensor.unit}")
            )
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.description = CHANNEL_NAME

            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()
        mNotificationManager.notify(notificationId, notification)
    }

    fun cancelThresholdAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ThresholdWarningReceiver::class.java)
        val requestCode = ID_THRESHOLD_WARNING
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        pendingIntent.cancel()

        alarmManager.cancel(pendingIntent)

        Toast.makeText(context, R.string.threshold_warning_deactivated, Toast.LENGTH_SHORT).show()
    }

    private fun getSensorsData(context: Context, notificationId: Int) {

        Log.d("ThresholdWarning", "Fetching Data")

        val databaseRef = DATABASE_REFERENCE
        databaseRef.child("sensors").get().addOnSuccessListener { result ->
            val sensorData = arrayListOf<Sensor>()
            val thresholdData = arrayListOf<Map<String, Double>>()

            for (sensor in result.children) {
                val id = sensor.key!!
                val name = sensor.child("data/name").value.toString()
                val value = sensor.child("records").children.last().child("value").value.toString()
                val unit = sensor.child("data/unit").value.toString()
                val createdAt =
                    sensor.child("records").children.last().child("created_at").value.toString()
                val urlIcon = sensor.child("data/url_icon").value.toString()

                val createdAtTimestamp = Timestamp(Date(createdAt.toLong() * 1000))
                sensorData.add(Sensor(id, name, value, unit, createdAtTimestamp, urlIcon))

                val upper = sensor.child("thresholds/upper").value.toString().toDouble()
                val lower = sensor.child("thresholds/lower").value.toString().toDouble()

                thresholdData.add(hashMapOf("upper" to upper, "lower" to lower))
            }

            Log.d("ThresholdWarning", "${sensorData.size}")

            for (i in sensorData.indices) {
                val upper = thresholdData[i]["upper"]!!
                val lower = thresholdData[i]["lower"]!!
                val value = sensorData[i].value.toDouble()

                if (value !in lower..upper) {
                    showAlarmNotification(context, notificationId, sensorData[i])
                }
            }
        }


    }
}