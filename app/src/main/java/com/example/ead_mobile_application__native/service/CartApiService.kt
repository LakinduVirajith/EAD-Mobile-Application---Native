package com.example.ead_mobile_application__native.service

import android.content.Context
import com.example.ead_mobile_application__native.BuildConfig
import com.example.ead_mobile_application__native.model.Cart
import com.example.ead_mobile_application__native.utils.ApiUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
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
    fun cartProducts(callback: (result: Result<List<Cart>>) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/cart/all"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(Exception("408: Cart fetching failed: Please check your internet connection.")))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        try {
                            val gson = Gson()
                            val cartType = object : TypeToken<List<Cart>>() {}.type
                            val cartItems: List<Cart> = gson.fromJson(responseBody, cartType)

                            callback(Result.success(cartItems))
                        } catch (e: Exception) {
                            callback(Result.failure(Exception("500: Failed to parse cart data")))
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

    // SERVICE FUNCTION TO INGRESS QUANTITY
    fun cartProductPlus(cartId: String, callback: (Int?, String?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/cart/plus/${cartId}"

        val request = Request.Builder()
            .url(url)
            .put(RequestBody.create(null, ""))
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        ApiUtils.makeRequest(request, callback)
    }

    // SERVICE FUNCTION TO DECREES QUANTITY
    fun cartProductMinus(cartId: String, callback: (Int?, String?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/cart/minus/${cartId}"

        val request = Request.Builder()
            .url(url)
            .put(RequestBody.create(null, ""))
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        ApiUtils.makeRequest(request, callback)
    }

    // SERVICE FUNCTION TO REMOVE CART ITEMS
    fun cartProductRemove(cartId: String, callback: (Int?, String?) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/cart/remove/${cartId}"

        val request = Request.Builder()
            .url(url)
            .delete()
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        ApiUtils.makeRequest(request, callback)
    }
}