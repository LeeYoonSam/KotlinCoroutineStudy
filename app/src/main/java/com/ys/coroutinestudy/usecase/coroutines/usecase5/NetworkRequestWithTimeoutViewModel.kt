package com.ys.coroutinestudy.usecase.coroutines.usecase5

import androidx.lifecycle.viewModelScope
import com.ys.coroutinestudy.base.BaseViewModel
import com.ys.coroutinestudy.mock.MockApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

class NetworkRequestWithTimeoutViewModel(
	private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

	fun performNetworkRequest(timeout: Long) {
		uiState.value = UiState.Loading

		// usingWithTimeout(timeout)
		usingWithTimeoutOrNull(timeout)
	}

	private fun usingWithTimeout(timeout: Long) {
		viewModelScope.launch {
			try {
				val recentVersions = withTimeout(timeout) {
					mockApi.getRecentAndroidVersions()
				}
				uiState.value = UiState.Success(recentVersions)
			} catch (e: TimeoutCancellationException) {
				uiState.value = UiState.Error("Network Request timed out!")
			} catch (e: Exception) {
				uiState.value = UiState.Error("Network Request failed!")
			}
		}
	}

	private fun usingWithTimeoutOrNull(timeout: Long) {
		viewModelScope.launch {
			try {
				val recentVersions = withTimeoutOrNull(timeout) {
					mockApi.getRecentAndroidVersions()
				}

				if (recentVersions != null) {
					uiState.value = UiState.Success(recentVersions)
				} else {
					uiState.value = UiState.Error("Network Request timed out!")
				}
			} catch (e: Exception) {
				uiState.value = UiState.Error("Network Request failed!")
			}
		}
	}
}