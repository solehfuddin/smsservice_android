package com.sofudev.smsservice

import retrofit2.Call
import retrofit2.http.*

interface SmsService {
    @GET("getsmsqueue")
    fun getSmsQueue() : Call<List<SmsQueue>>

    @FormUrlEncoded
    @PUT("putsmsqueue/{id}")
    fun editSmsQueue(@Path("id") id : Int, @Field("response") response : String) : Call<SmsQueue>
}