package com.woojun.cartoon_four_cut_pad.network

import com.woojun.cartoon_four_cut_pad.BuildConfig
import com.woojun.cartoon_four_cut_pad.data.Filter
import com.woojun.cartoon_four_cut_pad.data.FrameResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET


interface RetrofitAPI {
    @GET("${BuildConfig.baseUrl}frame")
    suspend fun getFrame(): Response<List<FrameResponse>>

    @GET("${BuildConfig.baseUrl}filter?image=true")
    fun getFilter(): Call<List<Filter>>

}