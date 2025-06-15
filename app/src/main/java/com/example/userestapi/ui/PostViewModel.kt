package com.example.userestapi.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userestapi.models.Post
import com.example.userestapi.network.RetrofitInstance
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class PostViewModel : ViewModel() {
    private val _uiState = mutableStateOf<UiState>(UiState.loading)
    val uiState: State<UiState> get() = _uiState

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            _uiState.value = try {
                val response = RetrofitInstance.apiService.getPosts()
                if (response.isSuccessful) {
                    UiState.Success(response.body() ?: emptyList())
                } else {
                    UiState.Error("Error: ${response.code()}")
                }
            } catch (e: IOException) {
                UiState.Error("Network Error: ${e.message}")
            } catch (e: HttpException) {
                UiState.Error("Http Error: ${e.message}")
            }
        }
    }
}

sealed class UiState {
    object loading : UiState()
    data class Success(val posts: List<Post>) : UiState()
    data class Error(val message: String) : UiState()
}