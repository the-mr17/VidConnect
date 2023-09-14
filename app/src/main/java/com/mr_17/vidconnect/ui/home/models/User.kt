package com.mr_17.vidconnect.ui.home.models

import com.google.gson.annotations.SerializedName
import com.mr_17.vidconnect.enums.LatestEventType

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
    val status: String,
    @SerializedName("latestEvent")
    val latestEvent: LatestEvent? = null
)

data class LatestEvent(
    @SerializedName("senderId")
    val senderId: String? = null,
    @SerializedName("targetId")
    val targetId: String,
    @SerializedName("type")
    val type: LatestEventType,
    @SerializedName("data")
    val data: String? = null,
    @SerializedName("timeStamp")
    val timeStamp: Long = System.currentTimeMillis(),
)

fun LatestEvent.isValid(): Boolean {
    return System.currentTimeMillis() - this.timeStamp < 60000
}