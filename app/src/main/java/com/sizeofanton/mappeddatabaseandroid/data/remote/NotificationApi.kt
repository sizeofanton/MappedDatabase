package com.sizeofanton.mappeddatabaseandroid.data.remote

import com.sizeofanton.mappeddatabaseandroid.data.pojo.Event
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NotificationApi {
    @GET("/notification")
    fun getEvent(
        @Query("token") token: String
    ): Call<Event>
}