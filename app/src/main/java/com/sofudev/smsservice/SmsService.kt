package com.sofudev.smsservice

import retrofit2.Call
import retrofit2.http.GET

interface SmsService {
    @GET("getsmsqueue")
    fun getSmsQueue() : Call<List<SmsQueue>>
}