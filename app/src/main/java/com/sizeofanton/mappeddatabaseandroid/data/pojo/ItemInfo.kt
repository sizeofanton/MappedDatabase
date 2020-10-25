package com.sizeofanton.mappeddatabaseandroid.data.pojo

import com.google.gson.annotations.SerializedName

data class ItemInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("count") val count: Int,
    @SerializedName("required") val required: Boolean
)
