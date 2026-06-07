package com.example.foodflow.data.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object ImageUploader {
    private const val IMGBB_API_KEY = "275d5d29386d3304ee6cdd1f5625194c"

    suspend fun upload(uri: Uri, context: Context): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("Cannot open image")
                val bytes = inputStream.readBytes()
                inputStream.close()

                val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)

                val client = OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("image", base64Image)
                    .build()

                val request = Request.Builder()
                    .url("https://api.imgbb.com/1/upload?key=$IMGBB_API_KEY")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody == null) {
                    throw Exception("ImgBB upload failed: ${response.code}")
                }

                val json = JSONObject(responseBody)
                val imageUrl = json.getJSONObject("data").getString("url")

                Result.success(imageUrl)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}