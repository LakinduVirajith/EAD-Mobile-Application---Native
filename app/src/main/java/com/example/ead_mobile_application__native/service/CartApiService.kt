package com.example.ead_mobile_application__native.service

import android.content.Context
import com.example.ead_mobile_application__native.BuildConfig
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

class CartApiService(private val context: Context) {
    private val client = OkHttpClient()

    // INSTANCE OF THE AUTH API SERVICES
    private val authApiService = AuthApiService(context)

    // SERVICE FUNCTION TO ADD PRODUCT TO CARD
    fun addCart(productId: String, size: String, color: String, quantity: Int, callback: (Int?, String?) -> Unit) {
        // CONSTRUCT THE URL
        val url = "${BuildConfig.BASE_URL}/cart/add"

        // CREATE THE JSON OBJECT FOR THE REQUEST BODY
        val jsonBody = JSONObject().apply {
            put("productId", productId)
            put("size", size)
            put("color", color)
            put("quantity", quantity)
        }

        // CREATE THE REQUEST BODY
        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        // BUILD THE REQUEST
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        // EXECUTE THE REQUEST
        ApiUtils.makeRequest(request, callback)
    }

    // SERVICE FUNCTION TO GET CART PRODUCT
    fun cartProducts(callback: (String?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/api/v1/cart"

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

    // SERVICE FUNCTION TO INGRESS QUANTITY
    fun cartProductPlus(productId: String, callback: (String?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/api/v1/cart/plus"

        val jsonBody = JSONObject().apply {
            put("productId", productId)
        }

        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
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

    // SERVICE FUNCTION TO DECREES QUANTITY
    fun cartProductMinus(productId: String, callback: (String?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/api/v1/cart/minus"

        val jsonBody = JSONObject().apply {
            put("productId", productId)
        }

        val requestBody = jsonBody.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
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