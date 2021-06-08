package com.sofudev.smsservice

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sofudev.smsservice.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val permissionRequest = 101

    private var timer = object : CountDownTimer(15000, 1000) {
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

        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS),
                permissionRequest)
        }
        else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS),
                permissionRequest)
        }

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
//        Toast.makeText(applicationContext, "Checking sms", Toast.LENGTH_SHORT).show()
        val init = SmsRepository.init(binding.edUrl.text.toString())
        init.getSmsQueue().enqueue(object : Callback<List<SmsQueue>> {
            override fun onFailure(call: Call<List<SmsQueue>>, t: Throwable) {
                Log.e("tag", "errornya ${t.message}")
            }

            override fun onResponse(
                call: Call<List<SmsQueue>>,
                response: Response<List<SmsQueue>>
            ) {
                if (response.isSuccessful)
                {
                    val data = response.body()
                    Log.d("tag", "responsennya ${data?.size}")

                    data?.map {
                        if (it.id_sms > 0)
                        {
                            Log.d("tag", "datanya ${it.id_sms}")
                            sendSms(it)
                        }
                        else
                        {
                            Log.d("tag", "data tidak ditemukan")
                        }
                    }
                }
            }
        })
        check()
    }

    private fun sendSms(smsQueue: SmsQueue) {
        val SENT = "SMS_SENT"
        val DELIVERED = "SMS_DELIVERED"

        val sentPI = PendingIntent.getBroadcast(
            this, 0,
            Intent(SENT), 0
        )

        val deliveredPI = PendingIntent.getBroadcast(
            this, 0,
            Intent(DELIVERED), 0
        )

        //---when the SMS has been sent---
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context?, arg1: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> Log.i("STATUS", "SMS Terkirim")
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Log.i("STATUS", "Cek pulsamu")
                    SmsManager.RESULT_ERROR_NO_SERVICE -> Log.i("STATUS", "Gangguan sinyal")
                    SmsManager.RESULT_ERROR_NULL_PDU -> Log.i("STATUS", "PDU tidak ditemukan")
                    SmsManager.RESULT_ERROR_RADIO_OFF -> Log.i("STATUS", "GSM inactive")
                }
            }
        }, IntentFilter(SENT))

        //---when the SMS has been delivered---
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(arg0: Context?, arg1: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> update(smsQueue.id_sms, "SMS terkirim")
                    Activity.RESULT_CANCELED -> update(smsQueue.id_sms, "SMS gagal terkirim")
                }
            }
        }, IntentFilter(DELIVERED))

        val smsManager: SmsManager = SmsManager.getDefault()
        Log.d("phone : ", smsQueue.phone_number)
        Log.d("message : ", smsQueue.message)
//        smsManager.sendTextMessage(smsQueue.phone_number, binding.edSmsc.text.toString(), smsQueue.message, sentPI, deliveredPI)
        smsManager.sendTextMessage(smsQueue.phone_number, binding.edSmsc.text.toString(), smsQueue.message, sentPI, deliveredPI)
        close()
    }

    private fun update(id : Int, response : String) {
        check()
        val init = SmsRepository.init(binding.edUrl.text.toString())
        init.editSmsQueue(id, response).enqueue(object : Callback<SmsQueue> {
            override fun onFailure(call: Call<SmsQueue>, t: Throwable) {
//                Log.e("tag", "error update ${t.message}")
            }

            override fun onResponse(call: Call<SmsQueue>, response: Response<SmsQueue>) {
                if (response.isSuccessful) {
                    val data = response.code()
                    Log.d("tag", "responsennya ${data}")
                }
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults:
    IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequest) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Akses sms diberikan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Akses sms tidak diizinkan akan menghambat sistem",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}

