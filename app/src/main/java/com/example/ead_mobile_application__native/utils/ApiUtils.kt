package com.example.ead_mobile_application__native.utils

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object ApiUtils {
    // CREATE AN OK-HTTP-CLIENT INSTANCE
    private val client = OkHttpClient()

    // FUNCTION TO MAKE API REQUESTS
    fun makeRequest(request: Request, callback: (Int?, String?) -> Unit) {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val (status, message) = parseResponseBody(response.code, responseBody)
                    callback(status, message)
                } else if (response.code == 401) {
                    callback(response.code, null)
                } else {
                    callback(null, null)
                }
            }
        })
    }

    // FUNCTION TO PARSE RESPONSE BODY
    fun parseResponseBody(statusCode: Int, responseBody: String?): Pair<Int, String> {
        return try {
            // TRY PARSING THE ERROR BODY AS A JSON ARRAY
            val jsonArray = JSONArray(responseBody)
            if (jsonArray.length() > 0) {
                val firstError = jsonArray.getJSONObject(0)
                val errorCode = firstError.getString("code")
                val errorDescription = firstError.getString("description")

                // RETURN ERROR CODE AND DESCRIPTION
                if (errorCode.length == 3) {
                    Pair(errorCode.toInt(), errorDescription)
                } else {
                    Pair(statusCode, errorDescription)
                }
            } else {
                Pair(statusCode, "NO SPECIFIC ERROR MESSAGE")
            }
        } catch (e: JSONException) {
            // IF PARSING AS JSON ARRAY FAILS, TRY PARSING AS A JSON OBJECT
            return parseErrorBodyAsJsonObject(statusCode, responseBody)
        }
    }

    // HELPER FUNCTION TO PARSE ERROR BODY AS A JSON OBJECT
    private fun parseErrorBodyAsJsonObject(statusCode: Int, responseBody: String?): Pair<Int, String> {
        return try {
            if (responseBody != null) {
                val jsonObject = JSONObject(responseBody)
                val status = jsonObject.optInt("status", statusCode)
                val errors = jsonObject.getJSONObject("errors")
                val firstKey = errors.keys().next()
                val messages = errors.getJSONArray(firstKey)
                Pair(status, messages.getString(0))
            } else {
                Pair(statusCode, "Invalid Error Response Format")
            }
        } catch (ex: JSONException) {
            // IF THAT ALSO FAILS, TRY TO EXTRACT THE MESSAGE DIRECTLY
            extractMessageFromJson(statusCode, responseBody)
        }
    }

    // HELPER FUNCTION TO EXTRACT MESSAGE DIRECTLY FROM JSON
    private fun extractMessageFromJson(statusCode: Int, responseBody: String?): Pair<Int, String> {
        return try {
            if (responseBody != null) {
                val jsonObject = JSONObject(responseBody)
                val message = jsonObject.optString("message", "Invalid Error Response Format")
                Pair(statusCode, message)
            } else {
                Pair(statusCode, "Invalid Error Response Format")
            }
        } catch (ex: JSONException) {
            Pair(statusCode, "Invalid Error Response Format")
        }
    }
}