package com.example.ead_mobile_application__native.service

import com.example.ead_mobile_application__native.BuildConfig
import com.example.ead_mobile_application__native.model.OrderItem
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class OrderApiService {
    private val client = OkHttpClient()

    // SERVICE FUNCTION TO PLACE ORDER
    fun placeOrder(order: List<OrderItem>, callback: (String?) -> Unit) {
        // CONSTRUCT THE URL
        val url = "${BuildConfig.BASE_URL}/api/v1/order/add"

        // CONVERT THE LIST OF ORDERS TO JSON
        val gson = Gson()
        val requestBodyJson = gson.toJson(order)

        // CREATE THE REQUEST BODY WITH JSON TYPE
        val requestBody = requestBodyJson
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        // CREATE THE REQUEST BODY
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // BUILD THE REQUEST
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.body?.string())
            }
        })
    }

    // SERVICE FUNCTION TO GET ORDERS
    fun fetchOrders(callback: (String?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/api/v1/orders"

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

    // SERVICE FUNCTION TO GET ORDER DETAILS
    fun fetchOrderDetails(orderId: Int, callback: (String?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/api/v1/order/details/${orderId}"

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

    // SERVICE FUNCTION TO GET ORDER DETAILS
    fun fetchOrder(orderId: Int, callback: (String?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/api/v1/order/${orderId}"

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