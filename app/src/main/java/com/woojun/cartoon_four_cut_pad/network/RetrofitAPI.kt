package com.woojun.cartoon_four_cut_pad.network

import com.woojun.cartoon_four_cut_pad.BuildConfig
import com.woojun.cartoon_four_cut_pad.data.Filter
import com.woojun.cartoon_four_cut_pad.data.FrameResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part


interface RetrofitAPI {
    @GET("${BuildConfig.baseUrl}frame")
    suspend fun getFrame(): Response<List<FrameResponse>>

    @GET("${BuildConfig.baseUrl}filter?image=true")
    fun getFilter(): Call<List<Filter>>

    @Multipart
    @PUT("${BuildConfig.baseUrl}image")
    fun putImage(
        @Header("auth") auth: String,
        @Header("type") type: String,
        @Part body: List<MultipartBody.Part>,
    ): Call<List<String>>
}