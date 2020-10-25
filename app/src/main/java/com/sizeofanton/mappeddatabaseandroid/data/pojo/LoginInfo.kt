package com.sizeofanton.mappeddatabaseandroid.data.pojo

import com.google.gson.annotations.SerializedName

data class LoginInfo(
    @SerializedName("token") val token: String,
    @SerializedName("isAdmin") val isAdmin: Boolean
)
