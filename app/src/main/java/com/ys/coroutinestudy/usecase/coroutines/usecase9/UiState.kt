package com.ys.coroutinestudy.usecase.coroutines.usecase9

import com.ys.coroutinestudy.mock.AndroidVersion

sealed class UiState {
	object Loading : UiState()
	data class Success(val recentVersions: List<AndroidVersion>) : UiState()
	data class Error(val message: String) : UiState()
}