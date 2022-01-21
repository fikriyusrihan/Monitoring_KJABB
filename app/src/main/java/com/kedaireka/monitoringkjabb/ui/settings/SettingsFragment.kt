package com.kedaireka.monitoringkjabb.ui.settings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kedaireka.monitoringkjabb.R
import com.kedaireka.monitoringkjabb.activity.AboutKJABB
import com.kedaireka.monitoringkjabb.activity.MainActivity
import com.kedaireka.monitoringkjabb.databinding.FragmentSettingsBinding
import com.kedaireka.monitoringkjabb.utils.AlarmReceiver
import com.kedaireka.monitoringkjabb.utils.ThresholdWarningReceiver

class SettingsFragment : Fragment() {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "Daily Notification"
        private const val CHANNEL_NAME = "Monitoring KJABB Daily Notification"

        private const val DAILY_NOTIFICATION_KEY = "DailyNotification"
        private const val THRESHOLD_WARNING_KEY = "ThresholdWarning"
    }

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var alarmReceiver: AlarmReceiver
    private lateinit var thresholdWarningReceiver: ThresholdWarningReceiver
    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        alarmReceiver = AlarmReceiver()
        thresholdWarningReceiver = ThresholdWarningReceiver()

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.aboutKjabb.setOnClickListener {
            val intentAboutKJABB = Intent(this.requireContext(), AboutKJABB::class.java)
            startActivity(intentAboutKJABB)
        }

        binding.thresholdWarning.setOnClickListener {
            sendNotification()
        }

        val sharedPref =
            activity?.getSharedPreferences("SettingFragmentSharedPreference", Context.MODE_PRIVATE)

        dailyNotificationSwitch(sharedPref)
        thresholdWarningSwitch(sharedPref)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dailyNotificationSwitch(sharedPref: SharedPreferences?) {
        val switchDailyNotificationStatus = sharedPref?.getBoolean(DAILY_NOTIFICATION_KEY, false)
        if (switchDailyNotificationStatus != null) {
            binding.switchDailyNotification.isChecked = switchDailyNotificationStatus
        } else {
            binding.switchDailyNotification.isChecked = false
        }

        binding.switchDailyNotification.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                alarmReceiver.setRepeatingAlarm(this.requireContext())
                sharedPref?.edit()?.putBoolean(DAILY_NOTIFICATION_KEY, true)?.apply()
            } else {
                alarmReceiver.cancelAlarm(this.requireContext())
                sharedPref?.edit()?.putBoolean(DAILY_NOTIFICATION_KEY, false)?.apply()
            }
        }
    }

    private fun thresholdWarningSwitch(sharedPref: SharedPreferences?) {
        val switchThresholdWarningStatus = sharedPref?.getBoolean(THRESHOLD_WARNING_KEY, false)
        if (switchThresholdWarningStatus != null) {
            binding.switchThresholdWarning.isChecked = switchThresholdWarningStatus
        } else {
            binding.switchThresholdWarning.isChecked = false
        }

        binding.switchThresholdWarning.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sharedPref?.edit()?.putBoolean(THRESHOLD_WARNING_KEY, true)?.apply()
                thresholdWarningReceiver.setRepeatingThresholdAlarm(this.requireContext())
            } else {
                sharedPref?.edit()?.putBoolean(THRESHOLD_WARNING_KEY, false)?.apply()
                thresholdWarningReceiver.cancelThresholdAlarm(this.requireContext())
            }
        }
    }


    private fun sendNotification() {
        val intent = Intent(this.requireContext(), MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this.requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val mNotificationManager = this.requireActivity()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(this.requireContext(), CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("Peringatan Threshold")
            .setContentText("Amonia berada diluar batas aman")
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_NAME

            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()
        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }
}