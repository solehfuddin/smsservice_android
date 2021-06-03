package com.sofudev.smsservice

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import com.sofudev.smsservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var timer = object : CountDownTimer(5000, 1000) {
        override fun onFinish() {
            woi()
        }

        override fun onTick(millisUntilFinished: Long) {

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnStart.setOnClickListener { check() }

        binding.btnCancel.setOnClickListener { close() }
    }

    private fun check()
    {
        timer.start()
    }

    private fun close()
    {
        timer.cancel()
    }

    private fun woi() {
        Toast.makeText(applicationContext, "Checking sms", Toast.LENGTH_SHORT).show()
        check()
    }
}

