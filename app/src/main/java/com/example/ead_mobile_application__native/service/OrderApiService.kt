package com.example.ead_mobile_application__native.service

import android.content.Context
import com.example.ead_mobile_application__native.BuildConfig
import com.example.ead_mobile_application__native.model.Order
import com.example.ead_mobile_application__native.model.OrderDetails
import com.example.ead_mobile_application__native.utils.ApiUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
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
    fun fetchOrders(pageNumber: Int, pageSize: Int, callback: (result: Result<List<Order>>)  -> Unit) {
        val url = "${BuildConfig.BASE_URL}/order/customer/get?pageNumber=${pageNumber}&pageSize=${pageSize}"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(Exception("408: Order fetching failed: Please check your internet connection.")))
            }

            override fun onResponse(call: Call, response: Response) {
                // CHECK IF RESPONSE IS SUCCESSFUL
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        try {
                            // PARSE JSON RESPONSE USING GSON
                            val gson = Gson()
                            val jsonObject = JSONObject(responseBody)
                            val bodyObject = jsonObject.getJSONObject("body")
                            val ordersJsonArray = bodyObject.getJSONArray("orders")

                            // CONVERT JSON ARRAY TO LIST OF PRODUCT OBJECTS
                            val orderType = object : TypeToken<List<Order>>() {}.type
                            val orders: List<Order> = gson.fromJson(ordersJsonArray.toString(), orderType)

                            // RETURN LIST OF ORDERS
                            callback(Result.success(orders))
                        } catch (e: Exception) {
                            // HANDLE PARSING ERROR
                            callback(Result.failure(Exception("500: Failed to parse order data")))
                        }
                    } ?: callback(Result.success(emptyList()))
                } else {
                    val errorBody = response.body?.string()
                    val (status, message) = ApiUtils.parseResponseBody(response.code, errorBody)
                    callback(Result.failure(Exception("$status: $message")))
                }
            }
        })
    }

    // SERVICE FUNCTION TO GET ORDER DETAILS
    fun fetchOrderDetails(orderId: String?, callback:(result: Result<OrderDetails>) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/order/get/details?orderId=${orderId}"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(Exception("408: Order details fetching failed: Please check your internet connection.")))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        try {
                            val gson = Gson()
                            val orderDetailsType = object : TypeToken<OrderDetails>() {}.type
                            val orderDetails: OrderDetails = gson.fromJson(responseBody, orderDetailsType)

                            callback(Result.success(orderDetails))
                        } catch (e: Exception) {
                            callback(Result.failure(Exception("500: Failed to parse order details data")))
                        }
                    } ?: callback(Result.failure(Exception("500: Failed to parse order details data")))
                } else {
                    val errorBody = response.body?.string()
                    val (status, message) = ApiUtils.parseResponseBody(response.code, errorBody)
                    callback(Result.failure(Exception("$status: $message")))
                }
            }
        })
    }
}