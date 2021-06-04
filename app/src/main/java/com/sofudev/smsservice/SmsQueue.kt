package com.sofudev.smsservice

data class SmsQueue(
    val id_sms : Int,
    val phone_number : String,
    val message : String,
    val status : Int,
    val response : String
)