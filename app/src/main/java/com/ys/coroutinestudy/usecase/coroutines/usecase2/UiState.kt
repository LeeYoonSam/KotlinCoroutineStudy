package com.ys.coroutinestudy.usecase.coroutines.usecase2

import com.ys.coroutinestudy.mock.VersionFeatures

sealed class UiState {
	object Loading: UiState()
	data class Success(
		val versionFeatures: VersionFeatures
	) : UiState()
	data class Error(val message: String) : UiState()
}