package com.example.ead_mobile_application__native.service

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ProductApiService {
    private val client = OkHttpClient()

    // SERVICE FUNCTION TO SIGN UP
    fun searchProducts(value: String, callback: (String?) -> Unit) {
        // CONSTRUCT THE URL WITH THE SEARCH QUERY
        val url = "http://BACKEND_SERVER_URL/api/v1/products?search=$value"

        val request = Request.Builder()
            .url(url)
            .get()
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