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

    // SERVICE FUNCTION TO GET HOME PRODUCTS
    fun homeProducts(callback: (String?) -> Unit) {
        // CONSTRUCT THE URL WITH THE SEARCH QUERY
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

    // SERVICE FUNCTION TO ADD PRODUCT TO CARD
    fun cartProduct(productId: String, callback: (String?) -> Unit) {
        // CONSTRUCT THE URL FOR ADDING TO THE CART
        val url = "http://BACKEND_SERVER_URL/api/v1/cart"

        // CREATE THE JSON OBJECT FOR THE REQUEST BODY
        val jsonBody = JSONObject().apply {
            put("productId", productId)
        }

        // CREATE THE REQUEST BODY
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
            jsonBody.toString()
        )

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

    private fun String.toMediaType(): MediaType? {
        return this.toMediaType()
    }
}