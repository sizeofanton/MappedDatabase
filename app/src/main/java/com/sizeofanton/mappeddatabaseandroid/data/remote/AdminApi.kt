package com.sizeofanton.mappeddatabaseandroid.data.remote

import com.sizeofanton.mappeddatabaseandroid.data.pojo.UserInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AdminApi {

    @GET("/db/users/get")
    fun getUsers(
        @Query("token", encoded = true) token: String
    ): Call<List<UserInfo>>

    @POST("/db/users/add")
    fun addUser(
        @Query("token", encoded = true) token: String,
        @Query("user", encoded = true) user: String,
        @Query("password", encoded = true) password: String,
        @Query("is_admin", encoded = true) isAdmin: Int
    ): Call<String>

    @POST("/db/users/remove")
    fun removeUser(
        @Query("token", encoded = true) token: String,
        @Query("id", encoded = true) id: Int
    ): Call<String>

    @POST("/db/users/edit")
    fun editUser(
        @Query("token", encoded = true) token: String,
        @Query("id", encoded = true) id: Int,
        @Query("password", encoded = true) password: String,
        @Query("is_admin", encoded = true) isAdmin: Int,
        @Query("is_active", encoded = true) isActive: Int
    ): Call<String>

    @POST("/db/locations/add/")
    fun addLocation(
        @Query("token", encoded = true) token: String,
        @Query("title", encoded = true) title: String,
        @Query("latitude", encoded = true) latitude: Double,
        @Query("longitude", encoded = true) longitude: Double
    ): Call<String>

    @POST("/db/locations/remove")
    fun removeLocation(
        @Query("token", encoded = true) token: String,
        @Query("id", encoded = true) id: Int
    ): Call<String>

    @POST("/db/locations/edit")
    fun editLocation(
        @Query("token", encoded = true) token: String,
        @Query("id", encoded = true) id: Int,
        @Query("title", encoded = true) title: String,
        @Query("latitude", encoded = true) latitude: Double,
        @Query("longitude", encoded = true) longitude: Double
    ): Call<String>

}