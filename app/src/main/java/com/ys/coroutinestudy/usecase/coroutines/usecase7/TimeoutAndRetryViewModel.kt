package com.ys.coroutinestudy.usecase.coroutines.usecase7

import androidx.lifecycle.viewModelScope
import com.ys.coroutinestudy.base.BaseViewModel
import com.ys.coroutinestudy.mock.MockApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber

class TimeoutAndRetryViewModel(
	private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

	fun performNetworkRequest() {
		uiState.value = UiState.Loading
		val numberOfRetries = 2
		val timeout = 1000L

		/**
		 * 첫번째 호출시 timeout -> retry(결과 무시)
		 * 두번째 호출시 500 에러 -> retry(결과 무시)
		 * 세번째 호출시 200 정상 호출
		 */

		val oreoVersionsDeferred = viewModelScope.async {
			retryWithTimeout(numberOfRetries, timeout) {
				mockApi.getAndroidVersionFeatures(27)
			}
		}

		val pieVersionsDeferred = viewModelScope.async {
			retryWithTimeout(numberOfRetries, timeout) {
				mockApi.getAndroidVersionFeatures(28)
			}
		}

		viewModelScope.launch {
			try {
				val versionFeatures = listOf(
					oreoVersionsDeferred,
					pieVersionsDeferred
				).awaitAll()

				uiState.value = UiState.Success(versionFeatures)

			} catch (e: Exception) {
				Timber.e(e)
				uiState.value = UiState.Error("Network Request failed")
			}
		}
	}

	private suspend fun <T> retryWithTimeout(
		numberOfRetries: Int,
		timeout: Long,
		block: suspend () -> T
	) = retry(numberOfRetries) {
		withTimeout(timeout) {
			block()
		}
	}

	private suspend fun <T> retry(
		numberOfRetries: Int,
		delayBetweenRetries: Long = 100,
		block: suspend () -> T
	): T {
		repeat(numberOfRetries) {
			try {
				return block()
			} catch (e: Exception) {
				Timber.e(e)
			}
			delay(delayBetweenRetries)
		}

		// 마지막 시도
		return block()
	}
}