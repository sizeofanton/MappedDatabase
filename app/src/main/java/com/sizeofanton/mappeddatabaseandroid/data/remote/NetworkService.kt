package com.sizeofanton.mappeddatabaseandroid.data.remote

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "localhost"

class NetworkService {
    private val retrofit: Retrofit

    init {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    companion object {
        private var instance: NetworkService? = null
        fun getInstance(): NetworkService {
            if (instance == null) {
                instance =
                    NetworkService()
            }
            return instance!!
        }
    }

    fun getAuthApi(): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    fun getUserApi(): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    fun getAdminApi(): AdminApi {
        return retrofit.create(AdminApi::class.java)
    }

    fun getNotificationApi(): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }
}
