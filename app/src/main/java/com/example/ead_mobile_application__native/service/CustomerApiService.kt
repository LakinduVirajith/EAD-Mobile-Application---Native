package com.example.ead_mobile_application__native.service

import com.example.ead_mobile_application__native.model.Customer
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class CustomerApiService {
    private val client = OkHttpClient()

    fun signUp(customer: Customer, callback: (String?) -> Unit) {
        val requestBody = FormBody.Builder()
            .add("userName", customer.userName)
            .add("email", customer.email)
            .add("password", customer.password)
            .add("phoneNumber", customer.phoneNumber)
            .add("dateOfBirth", customer.dateOfBirth)
            .add("gender", customer.gender)
            .build()

        val request = Request.Builder()
            .url("http://BACKEND_SERVER_URL/api/auth/register")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.body?.string())
            }
        })
    }
}