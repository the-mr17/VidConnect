package com.mr_17.vidconnect.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mr_17.vidconnect.data.Resource
import com.mr_17.vidconnect.ui.home.models.LatestEvent
import com.mr_17.vidconnect.ui.home.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
): ViewModel() {
    private val _allUserDataFlow = MutableStateFlow<Resource<List<User>>?>(null)
    val allUserDataFlow: StateFlow<Resource<List<User>>?> = _allUserDataFlow

    private val _sendMessageFlow = MutableStateFlow<Resource<String>?>(null)
    val sendMessageFlow: StateFlow<Resource<String>?> = _sendMessageFlow

    fun getAllUserData() = viewModelScope.launch {
        _allUserDataFlow.value = Resource.Loading
        val result = repository.getAllUserData()
        _allUserDataFlow.value = result
    }

    fun sendMessageToOtherClient(latestEvent: LatestEvent) = viewModelScope.launch {
        _sendMessageFlow.value = Resource.Loading
        val result = repository.sendMessageToOtherClient(latestEvent)
        _sendMessageFlow.value = result
    }

    fun subscribeForLatestEvent() = viewModelScope.launch {
        val result = repository.subscribeForLatestEvent(object: HomeRepositoryImpl.Listener{
            override fun onLatestEventReceived(latestEvent: LatestEvent) {
                when(latestEvent.type) {
                    else -> Log.d("event123", latestEvent.toString())
                }
            }

        })
    }
}