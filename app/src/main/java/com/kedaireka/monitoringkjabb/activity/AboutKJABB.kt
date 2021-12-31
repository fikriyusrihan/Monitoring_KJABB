package com.kedaireka.monitoringkjabb.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kedaireka.monitoringkjabb.databinding.ActivityAboutKjabbBinding

class AboutKJABB : AppCompatActivity() {
    private lateinit var binding: ActivityAboutKjabbBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutKjabbBinding.inflate(layoutInflater)

        binding.btnBack.setOnClickListener {
            finish()
        }

        setContentView(binding.root)
    }
}