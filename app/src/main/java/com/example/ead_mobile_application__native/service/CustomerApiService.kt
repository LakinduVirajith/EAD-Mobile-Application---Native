package com.example.ead_mobile_application__native.service

import com.example.ead_mobile_application__native.model.Customer
import com.example.ead_mobile_application__native.model.SignIn
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class CustomerApiService {
    private val client = OkHttpClient()

    // SERVICE FUNCTION TO SIGN UP
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
            .url("http://BACKEND_SERVER_URL/api/v1/auth/sign-up")
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

    // SERVICE FUNCTION TO SIGN IN
    fun signIn(signIn: SignIn, callback: (String?) -> Unit) {
        val requestBody = FormBody.Builder()
            .add("email", signIn.email)
            .add("password", signIn.password)
            .build()

        val request = Request.Builder()
            .url("http://BACKEND_SERVER_URL/api/v1/auth/sign-in")
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

    // SERVICE FUNCTION TO CHANGE CUSTOMER EMAIL
    fun changeEmail(currentEmail: String, newEmail: String, callback: (String?) -> Unit) {
        val requestBody = FormBody.Builder()
            .add("currentEmail", currentEmail)
            .add("newEmail", newEmail)
            .build()

        val request = Request.Builder()
            .url("http://BACKEND_SERVER_URL/api/v1/auth/change-email")
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