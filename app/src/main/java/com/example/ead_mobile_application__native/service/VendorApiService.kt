package com.example.ead_mobile_application__native.service

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class VendorApiService {
    private val client = OkHttpClient()

    // SERVICE FUNCTION TO ADD RANKING TO THE VENDOR
    fun addRanking(comment: String, rating: Int, callback: (String?) -> Unit) {
        // CONSTRUCT THE URL
        val url = "http://BACKEND_SERVER_URL/api/v1/ranking/vendor/add"

        // CREATE THE JSON OBJECT FOR THE REQUEST BODY
        val jsonBody = JSONObject().apply {
            put("comment", comment)
            put("rating", rating)
        }

        // CREATE THE REQUEST BODY
        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        // BUILD THE REQUEST
        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        // EXECUTE THE REQUEST
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)  // NOTIFY FAILURE
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(response.body?.string())  // RETURN RESPONSE BODY
                } else {
                    callback(null)  // HANDLE ERROR CASE
                }
            }
        })
    }
}