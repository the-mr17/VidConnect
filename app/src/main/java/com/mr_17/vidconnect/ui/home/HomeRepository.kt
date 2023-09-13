package com.mr_17.vidconnect.ui.home

import com.mr_17.vidconnect.data.Resource
import com.mr_17.vidconnect.ui.home.models.User

interface HomeRepository {
    suspend fun getAllUserData() : Resource<List<User>>?
}