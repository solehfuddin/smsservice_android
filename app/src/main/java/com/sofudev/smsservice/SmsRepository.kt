package com.sofudev.smsservice

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SmsRepository {
    fun init(url : String) : SmsService {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(url)
            .build()

        return retrofit.create(SmsService::class.java)
    }
}