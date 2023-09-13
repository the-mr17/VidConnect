package com.mr_17.vidconnect.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mr_17.vidconnect.data.Resource
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

    fun getAllUserData() = viewModelScope.launch {
        _allUserDataFlow.value = Resource.Loading
        val result = repository.getAllUserData()
        _allUserDataFlow.value = result
    }
}