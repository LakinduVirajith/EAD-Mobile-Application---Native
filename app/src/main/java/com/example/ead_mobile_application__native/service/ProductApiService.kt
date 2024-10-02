package com.example.ead_mobile_application__native.service

import android.content.Context
import com.example.ead_mobile_application__native.BuildConfig
import com.example.ead_mobile_application__native.model.Product
import com.example.ead_mobile_application__native.model.ProductDetails
import com.example.ead_mobile_application__native.utils.ApiUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ProductApiService(private val context: Context) {
    private val client = OkHttpClient()

    // INSTANCE OF THE AUTH API SERVICES
    private val authApiService = AuthApiService(context)

    // SERVICE FUNCTION TO GET HOME PRODUCTS
    fun fetchHomeProducts(pageNumber: Int, pageSize: Int, callback: (result: Result<List<Product>>) -> Unit) {
        // CONSTRUCT THE URL
        val url = "${BuildConfig.BASE_URL}/product/home?pageNumber=${pageNumber}&pageSize=${pageSize}"

        // BUILD THE REQUEST
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        // EXECUTE THE REQUEST
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(Exception("408: Product fetching failed: Please check your internet connection.")))
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
                            val productsJsonArray = bodyObject.getJSONArray("products")

                            // CONVERT JSON ARRAY TO LIST OF PRODUCT OBJECTS
                            val productType = object : TypeToken<List<Product>>() {}.type
                            val products: List<Product> = gson.fromJson(productsJsonArray.toString(), productType)

                            // RETURN LIST OF PRODUCTS
                            callback(Result.success(products))
                        } catch (e: Exception) {
                            // HANDLE PARSING ERROR
                            callback(Result.failure(Exception("500: Failed to parse product data")))
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

    // SERVICE FUNCTION TO SEARCH PRODUCT
    fun searchProducts(search: String, pageNumber: Int, pageSize: Int, callback: (result: Result<List<Product>>) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/product/search/${search}?pageNumber=${pageNumber}&pageSize=${pageSize}"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(Exception("408: Product fetching failed: Please check your internet connection.")))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(responseBody)
                            val bodyObject = jsonObject.getJSONObject("body")
                            val productsJsonArray = bodyObject.getJSONArray("products")

                            val productType = object : TypeToken<List<Product>>() {}.type
                            val products: List<Product> = gson.fromJson(productsJsonArray.toString(), productType)

                            callback(Result.success(products))
                        } catch (e: Exception) {
                            callback(Result.failure(Exception("500: Failed to parse product data")))
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

    // SERVICE FUNCTION TO GET PRODUCT DETAILS
    fun fetchProductDetails(productId: String, callback: (result: Result<ProductDetails>) -> Unit) {
        val url = "${BuildConfig.BASE_URL}/product/${productId}"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer ${authApiService.accessToken()}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(Exception("408: Product fetching failed: Please check your internet connection.")))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {

                    response.body?.string()?.let { responseBody ->
                        try {
                            val gson = Gson()
                            val jsonObject = JSONObject(responseBody)
                            val bodyObject = jsonObject.getJSONObject("body")
                            val productType = object : TypeToken<ProductDetails>() {}.type
                            val product: ProductDetails = gson.fromJson(bodyObject.toString(), productType)

                            callback(Result.success(product))
                        } catch (e: Exception) {
                            callback(Result.failure(Exception("500: Failed to parse product data")))
                        }
                    } ?: callback(Result.failure(Exception("500: Failed to parse product data")))
                } else {
                    val errorBody = response.body?.string()
                    val (status, message) = ApiUtils.parseResponseBody(response.code, errorBody)
                    callback(Result.failure(Exception("$status: $message")))
                }
            }
        })
    }
}