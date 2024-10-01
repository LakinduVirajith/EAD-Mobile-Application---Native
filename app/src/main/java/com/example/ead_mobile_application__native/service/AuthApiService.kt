package com.example.ead_mobile_application__native.service

import android.content.Context
import com.example.ead_mobile_application__native.BuildConfig
import com.example.ead_mobile_application__native.model.Customer
import com.example.ead_mobile_application__native.model.SignIn
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

class AuthApiService (private val context: Context) {
    private val client = OkHttpClient()

    // SERVICE FUNCTION TO SIGN UP
    fun signUp(customer: Customer, callback: (String?) -> Unit) {
        // CONSTRUCT THE URL
        val url = "${BuildConfig.BASE_URL}/auth/sign-up"

        // CREATE THE JSON OBJECT FOR THE REQUEST BODY
        val jsonBody = JSONObject().apply {
            put("userName", customer.userName.trim())
            put("email", customer.email.trim())
            put("password", customer.password.trim())
            put("phoneNumber", customer.phoneNumber.trim())
            put("dateOfBirth", customer.dateOfBirth.trim())
            put("gender", customer.gender.trim())
            put("role", "Customer")
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
                if (response.isSuccessful) {
                    val successBody = response.body?.string()
                    callback("${response.code}: $successBody")
                } else {
                    val errorBody = response.body?.string()
                    val (status, message) = ApiUtils.parseResponseBody(response.code, errorBody)
                    callback("$status: $message")
                }
            }
        })
    }

    // SERVICE FUNCTION TO SIGN IN
    fun signIn(signIn: SignIn, callback: (Response?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/auth/sign-in"

        val jsonBody = JSONObject().apply {
            put("userName", signIn.email.trim())
            put("password", signIn.password.trim())
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
                if (response.isSuccessful) {
                    val successBody = response.body?.string()
                    if(successBody != null){
                        val jsonObject = JSONObject(successBody)
                        val token = jsonObject.getString("token")
                        val refreshToken = jsonObject.getString("refreshToken")

                        // STORE TOKEN IN SHARED-PREFERENCES
                        storeTokens(token, refreshToken)
                    }else{
                        callback(null)
                    }

                    callback(response)
                } else {
                    callback(response)
                }
            }
        })
    }

    // SERVICE FUNCTION TO SIGN IN USING REFRESH TOKEN
    fun signInWithRefreshToken(refreshToken: String, callback: (Boolean?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/auth/refresh-token/${refreshToken}"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val successBody = response.body?.string()
                    if(successBody != null){
                        val jsonObject = JSONObject(successBody)
                        val newToken = jsonObject.getString("token")
                        val newRefreshToken = jsonObject.getString("refreshToken")

                        // STORE TOKEN IN SHARED-PREFERENCES
                        storeTokens(newToken, newRefreshToken)
                    }else{
                        callback(false)
                    }

                    callback(true)
                } else {
                    callback(false)
                }
            }
        })
    }

    // FUNCTION TO STORE TOKENS IN SHARED PREFERENCES
    private fun storeTokens(token: String, refreshToken: String) {
        val sharedPreferences = context.getSharedPreferences("TokenPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.putString("refreshToken", refreshToken)
        editor.apply()
    }

    // FUNCTION TO RETRIEVE ACCESS TOKEN FROM SHARED PREFERENCES
    fun accessToken(): String?{
        val sharedPreferences = context.getSharedPreferences("TokenPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        return token
    }

    // FUNCTION TO RETRIEVE TOKENS FROM SHARED PREFERENCES
    fun getTokens(): Pair<String?, String?> {
        val sharedPreferences = context.getSharedPreferences("TokenPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val refreshToken = sharedPreferences.getString("refreshToken", null)
        return Pair(token, refreshToken)
    }

    // FUNCTION TO REMOVE TOKENS FROM SHARED PREFERENCES
    fun removeTokens() {
        val sharedPreferences = context.getSharedPreferences("TokenPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("token")
        editor.remove("refreshToken")
        editor.apply()
    }
}