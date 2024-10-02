package com.example.ead_mobile_application__native.service

import android.content.Context
import com.example.ead_mobile_application__native.BuildConfig
import com.example.ead_mobile_application__native.utils.ApiUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class OrderApiService(private val context: Context) {
    private val client = OkHttpClient()

    // INSTANCE OF THE AUTH API SERVICES
    private val authApiService = AuthApiService(context)

    // SERVICE FUNCTION TO PLACE ORDER
    fun placeOrder(date: String, callback: (Int?, String?) -> Unit) {
        // CONSTRUCT THE URL
        val url = "${BuildConfig.BASE_URL}/order/add/${date}"

        // CREATE THE REQUEST BODY
        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create(null, ""))
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        // BUILD THE REQUEST
        ApiUtils.makeRequest(request, callback)
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