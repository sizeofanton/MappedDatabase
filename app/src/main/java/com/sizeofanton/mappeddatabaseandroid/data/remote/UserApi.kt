package com.sizeofanton.mappeddatabaseandroid.data.remote

import com.sizeofanton.mappeddatabaseandroid.data.pojo.ItemInfo
import com.sizeofanton.mappeddatabaseandroid.data.pojo.LocationInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {

    @GET("/db/items/get")
    fun getItems(
        @Query("token", encoded = true) token: String,
        @Query("location_id", encoded = true) locationId: Int
    ): Call<List<ItemInfo>>

    @POST("/db/items/add")
    fun addItem(
        @Query("token") token: String,
        @Query("location_id") locationId: Int,
        @Query("title") title: String,
        @Query("count") count: Int,
        @Query("is_required") isRequired: Int
    ): Call<String>

    @POST("/db/items/remove")
    fun removeItem(
        @Query("token") token: String,
        @Query("id") id: Int
    ): Call<String>

    @POST("/db/items/edit")
    fun editItem(
        @Query("token") token: String,
        @Query("id") id: Int,
        @Query("title") title: String,
        @Query("count") count: Int,
        @Query("is_required") isRequired: Int
    ): Call<String>

    @GET("/db/locations/get")
    fun getLocations(
        @Query("token", encoded = true) token: String
    ): Call<List<LocationInfo>>

}