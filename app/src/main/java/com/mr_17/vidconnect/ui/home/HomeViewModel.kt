package com.mr_17.vidconnect.ui.home

import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mr_17.vidconnect.data.Resource
import com.mr_17.vidconnect.databinding.FragmentHomeBinding
import com.mr_17.vidconnect.enums.LatestEventType
import com.mr_17.vidconnect.ui.home.models.LatestEvent
import com.mr_17.vidconnect.ui.home.models.User
import com.mr_17.vidconnect.ui.home.models.isValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
): ViewModel() {
    private val _allUserDataFlow = MutableStateFlow<Resource<List<User>>?>(null)
    val allUserDataFlow: StateFlow<Resource<List<User>>?> = _allUserDataFlow

    private val _sendMessageFlow = MutableStateFlow<Resource<String>?>(null)
    val sendMessageFlow: StateFlow<Resource<String>?> = _sendMessageFlow

    private val _latestEventFlow = MutableStateFlow<Resource<LatestEvent>?>(null)
    val latestEventFlow: StateFlow<Resource<LatestEvent>?> = _latestEventFlow

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
        repository.subscribeForLatestEvent(object : HomeRepositoryImpl.Listener {
            override fun onLatestEventReceived(latestEvent: LatestEvent) {
                _latestEventFlow.value = Resource.Success(latestEvent)
            }
        })
    }

    fun sendConnectionRequest(targetId: String, isVideoCall: Boolean) = viewModelScope.launch {
        //_sendMessageFlow.value = Resource.Loading
        val result = repository.sendConnectionRequest(targetId, isVideoCall)
        //_sendMessageFlow.value = result
    }
}