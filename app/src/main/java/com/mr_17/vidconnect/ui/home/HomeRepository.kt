package com.mr_17.vidconnect.ui.home

import com.mr_17.vidconnect.data.Resource
import com.mr_17.vidconnect.ui.home.models.LatestEvent
import com.mr_17.vidconnect.ui.home.models.User

interface HomeRepository {
    suspend fun getAllUserData() : Resource<List<User>>?
    suspend fun sendMessageToOtherClient(latestEvent: LatestEvent) : Resource<String>?
    suspend fun subscribeForLatestEvent(listener: HomeRepositoryImpl.Listener)
}