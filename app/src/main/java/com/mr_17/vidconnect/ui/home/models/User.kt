package com.mr_17.vidconnect.ui.home.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("uId")
    val uId: String,
    @SerializedName("emailAddress")
    val emailAddress: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("status")
    val status: String
)