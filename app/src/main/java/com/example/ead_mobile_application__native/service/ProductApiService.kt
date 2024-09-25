package com.example.ead_mobile_application__native.service

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ProductApiService {
    private val client = OkHttpClient()

    // SERVICE FUNCTION TO SEARCH PRODUCT
    fun searchProducts(value: String, callback: (String?) -> Unit) {
        // CONSTRUCT THE URL
        val url = "http://BACKEND_SERVER_URL/api/v1/products?search=$value"

        // BUILD THE REQUEST
        val request = Request.Builder()
            .url(url)
            .get()
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

    // SERVICE FUNCTION TO GET HOME PRODUCTS
    fun fetchHomeProducts(callback: (String?) -> Unit) {
        val url = "http://BACKEND_SERVER_URL/api/v1/products"

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

    // SERVICE FUNCTION TO GET PRODUCT DETAILS
    fun fetchProductDetails(productId: String, callback: (String?) -> Unit) {
        val url = "http://BACKEND_SERVER_URL/api/v1/product?search=$productId"

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