package com.example.ead_mobile_application__native.service

import com.example.ead_mobile_application__native.model.Customer
import com.example.ead_mobile_application__native.model.SignIn
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class CustomerApiService {
    private val client = OkHttpClient()

    // SERVICE FUNCTION TO SIGN UP
    fun signUp(customer: Customer, callback: (String?) -> Unit) {
        // CONSTRUCT THE URL
        val url = "http://BACKEND_SERVER_URL/api/v1/auth/sign-up"

        // CREATE THE JSON OBJECT FOR THE REQUEST BODY
        val jsonBody = JSONObject().apply {
            put("userName", customer.userName)
            put("email", customer.email)
            put("password", customer.password)
            put("phoneNumber", customer.phoneNumber)
            put("dateOfBirth", customer.dateOfBirth)
            put("gender", customer.gender)
        }

        // CREATE THE REQUEST BODY
        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        // BUILD THE REQUEST
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // EXECUTE THE REQUEST
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
        val url = "http://BACKEND_SERVER_URL/api/v1/auth/sign-in"

        val jsonBody = JSONObject().apply {
            put("email", signIn.email)
            put("password", signIn.password)
        }

        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
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
        val url = "http://BACKEND_SERVER_URL/api/v1/auth/change-email"

        val jsonBody = JSONObject().apply {
            put("currentEmail", currentEmail)
            put("newEmail", newEmail)
        }

        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
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

    // SERVICE FUNCTION TO CHANGE CUSTOMER PASSWORD
    fun changePassword(email: String, currentPassword: String, newPassword: String, callback: (String?) -> Unit) {
        val url = "http://BACKEND_SERVER_URL/api/v1/auth/change-password"

        val jsonBody = JSONObject().apply {
            put("email", email)
            put("currentPassword", currentPassword)
            put("newPassword", newPassword)
        }

        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
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

    // SERVICE FUNCTION TO DEACTIVATE ACCOUNT
    fun deactivateAccount(email: String, callback: (String?) -> Unit) {
        val url = "http://BACKEND_SERVER_URL/api/v1/auth/deactivate"

        val jsonBody = JSONObject().apply {
            put("email", email)
        }

        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
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