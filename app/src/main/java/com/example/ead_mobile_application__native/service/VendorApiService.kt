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

class VendorApiService(private val context: Context)  {
    private val client = OkHttpClient()

    // INSTANCE OF THE AUTH API SERVICES
    private val authApiService = AuthApiService(context)

    // SERVICE FUNCTION TO ADD RANKING TO THE VENDOR
    fun addRanking(vendorId: String?, comment: String, rating: Int, createdAt: String, callback: (Int? ,String?) -> Unit) {
        // CONSTRUCT THE URL
        val url = "${BuildConfig.BASE_URL}/ranking/vendor"

        // CREATE THE JSON OBJECT FOR THE REQUEST BODY
        val jsonBody = JSONObject().apply {
            put("vendorId", vendorId?.trim())
            put("comment", comment.trim())
            put("rating", rating.toString().trim())
            put("createdAt", createdAt.trim())
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
}