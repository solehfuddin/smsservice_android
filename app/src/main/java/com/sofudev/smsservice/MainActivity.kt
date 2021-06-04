package com.sofudev.smsservice

import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sofudev.smsservice.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
        val smsManager: SmsManager = SmsManager.getDefault()
        Log.d("phone : ", smsQueue.phone_number)
        Log.d("message : ", smsQueue.message)
        smsManager.sendTextMessage(smsQueue.phone_number, "+62855000000", smsQueue.message, null, null)
        update(smsQueue.id_sms, "Pending")
    }

    private fun update(id : Int, response : String) {
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
}

