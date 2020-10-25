package com.sizeofanton.mappeddatabaseandroid.data.remote

import com.sizeofanton.mappeddatabaseandroid.data.pojo.LoginInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @GET("/login")
    fun login(
        @Query("user", encoded = true) user: String,
        @Query("password", encoded = true) password: String
    ): Call<LoginInfo>

    @POST("/logout")
    fun logout(
        @Query("user", encoded = true) user: String
    ): Call<String>

    @POST("/change_pass")
    fun changePassword(
        @Query("user", encoded = true) user: String,
        @Query("old_password", encoded = true) oldPassword: String,
        @Query("new_password", encoded = true) newPassword: String
    ): Call<String>

    @POST("/register")
    fun register(
        @Query("user", encoded = true) user: String,
        @Query("password", encoded = true) password: String
    ): Call<String>

    @POST("/register_firebase_token")
    fun registerFirebaseToken(
        @Query("token", encoded = true) token: String,
        @Query("firebase_token", encoded = true) firebaseToken: String
    ): Call<String>

    @POST("/set_user_language")
    fun setUserLanguage(
        @Query("token", encoded = true) token: String,
        @Query("language", encoded = true) language: String
    ): Call<String>

}