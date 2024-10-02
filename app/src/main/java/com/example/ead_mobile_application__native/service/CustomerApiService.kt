package com.example.ead_mobile_application__native.service

import android.content.Context
import com.example.ead_mobile_application__native.BuildConfig
import com.example.ead_mobile_application__native.model.CustomerDetails
import com.example.ead_mobile_application__native.utils.ApiUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class CustomerApiService(private val context: Context) {
    private val client = OkHttpClient()

    // INSTANCE OF THE AUTH API SERVICES
    private val authApiService = AuthApiService(context)

    // FUNCTION TO CHANGE CUSTOMER EMAIL
    fun changeEmail(currentEmail: String, newEmail: String, callback: (Int?, String?) -> Unit) {
        // CONSTRUCT THE URL
        val url = "${BuildConfig.BASE_URL}/user/update/email"

        // CREATE THE JSON OBJECT FOR THE REQUEST BODY
        val jsonBody = JSONObject().apply {
            put("currentEmail", currentEmail.trim())
            put("newEmail", newEmail.trim())
        }

        // CREATE THE REQUEST BODY
        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        // BUILD THE REQUEST
        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        // EXECUTE THE REQUEST
        ApiUtils.makeRequest(request, callback)
    }

    // FUNCTION TO CHANGE CUSTOMER PASSWORD
    fun changePassword(email: String, currentPassword: String, newPassword: String, callback: (Int?, String?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/user/update/password"

        val jsonBody = JSONObject().apply {
            put("email", email.trim())
            put("currentPassword", currentPassword.trim())
            put("newPassword", newPassword.trim())
        }

        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        ApiUtils.makeRequest(request, callback)
    }

    // FUNCTION TO GET USER DETAILS
    fun getUserDetails(callback: (CustomerDetails?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/user/details"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()

                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)

                        callback(CustomerDetails(
                            userName = jsonObject.getString("userName"),
                            email = jsonObject.getString("email"),
                            phoneNumber = jsonObject.getString("phoneNumber"),
                            dateOfBirth = jsonObject.getString("dateOfBirth"),
                            gender = jsonObject.getString("gender")
                        ))
                    } else {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }
        })
    }

    // FUNCTION TO UPDATE USER DETAILS
    fun updateUserDetails(customer: CustomerDetails, callback: (Int?, String?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/user/update/details"

        val jsonBody = JSONObject().apply {
            put("userName", customer.userName.trim())
            put("email", customer.email.trim())
            put("password", null)
            put("phoneNumber", customer.phoneNumber.trim())
            put("dateOfBirth", customer.dateOfBirth.trim())
            put("gender", customer.gender.trim())
            put("role", "Customer")
        }

        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        ApiUtils.makeRequest(request, callback)
    }

    // FUNCTION TO DEACTIVATE ACCOUNT
    fun deactivateAccount(email: String, callback: (Int?, String?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/user/deactivate/$email"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        ApiUtils.makeRequest(request, callback)
    }

    // FUNCTION TO DEACTIVATE ACCOUNT
    fun checkShipping(callback: (result: Result<Boolean>) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/user/check/shipping"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(Exception("408: Shipping details fetching failed: Please check your internet connection.")))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        val isAvailable = JSONObject(responseBody).optBoolean("body", false)
                        callback(Result.success(isAvailable))
                    } ?: callback(Result.failure(Exception("500: Error parsing shipping data.")))
                } else {
                    callback(Result.failure(Exception("${response.code}: ${response.message}")))
                }
            }
        })
    }
}
