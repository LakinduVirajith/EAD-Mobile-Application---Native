package com.example.ead_mobile_application__native.service

import android.content.Context
import android.util.Log
import com.example.ead_mobile_application__native.BuildConfig
import com.example.ead_mobile_application__native.model.Customer
import com.example.ead_mobile_application__native.model.SignIn
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
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
                    val errorMessage = parseErrorBody(errorBody, response.code)
                    callback(errorMessage)
                }
            }
        })
    }

    // FUNCTION TO PARSE ERROR BODY
    private fun parseErrorBody(errorBody: String?, statusCode: Int): String {
        return try {
            // ATTEMPT TO PARSE ERROR AS JSON-ARRAY
            val jsonArray = JSONArray(errorBody)
            if (jsonArray.length() > 0) {
                val firstError = jsonArray.getJSONObject(0)
                val errorCode = firstError.getString("code")
                val errorDescription = firstError.getString("description")

                if (errorCode.length == 3) {
                    "$errorCode: $errorDescription"
                } else {
                    "$statusCode: $errorDescription"
                }
            } else {
                "$statusCode: No specific error message"
            }
        } catch (e: JSONException) {
            // IF IT'S NOT A JSON-ARRAY, TREAT IT AS JSON-OBJECT
            try {
                if(errorBody != null) {
                    val jsonObject = JSONObject(errorBody)
                    val status = jsonObject.optInt("status", statusCode)
                    val errors = jsonObject.getJSONObject("errors")
                    val firstKey = errors.keys().next()
                    val messages = errors.getJSONArray(firstKey)
                    "$status: ${messages.getString(0)}"
                }else{
                    "$statusCode: Invalid error response format"
                }
            } catch (ex: JSONException) {
                "$statusCode: Invalid error response format"
            }
        }
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

    // FUNCTION TO STORE TOKENS IN SHARED PREFERENCES
    private fun storeTokens(token: String, refreshToken: String) {
        val sharedPreferences = context.getSharedPreferences("TokenPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.putString("refreshToken", refreshToken)
        editor.apply()
    }

    // FUNCTION TO RETRIEVE TOKENS FROM SHARED PREFERENCES
    fun getTokens(): Pair<String?, String?> {
        val sharedPreferences = context.getSharedPreferences("TokenPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val refreshToken = sharedPreferences.getString("refreshToken", null)
        return Pair(token, refreshToken)
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
}