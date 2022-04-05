package com.ys.coroutinestudy.usecase.coroutines.usecase6

import androidx.lifecycle.viewModelScope
import com.ys.coroutinestudy.base.BaseViewModel
import com.ys.coroutinestudy.mock.MockApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class RetryNetworkRequestViewModel(
	private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

	fun performNetworkRequest() {
		uiState.value = UiState.Loading

		viewModelScope.launch {
			val numberOfRetries = 2
			try {
				retry(times = numberOfRetries) {
					val recentVersions = mockApi.getRecentAndroidVersions()
					uiState.value = UiState.Success(recentVersions)
				}
			} catch (e: Exception) {
				uiState.value = UiState.Error("Network Request failed")
			}
		}
	}

	private suspend fun <T> retry(
		times: Int,
		initialDelayMillis: Long = 100,
		maxDelayMillis: Long = 1000,
		factor: Double = 2.0,
		block: suspend () -> T
	): T {
		var currentDelay = initialDelayMillis
		repeat(times) {
			try {
				return block()
			} catch (exception: Exception) {
				Timber.e(exception)
			}
			delay(currentDelay)
			currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
		}

		// 마지막 시도
		return block()
	}
}