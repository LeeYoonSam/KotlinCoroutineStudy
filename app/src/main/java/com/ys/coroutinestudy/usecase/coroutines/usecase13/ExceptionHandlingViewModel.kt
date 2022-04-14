package com.ys.coroutinestudy.usecase.coroutines.usecase13

import androidx.lifecycle.viewModelScope
import com.ys.coroutinestudy.base.BaseViewModel
import com.ys.coroutinestudy.mock.MockApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import timber.log.Timber

class ExceptionHandlingViewModel(
	private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

	fun handleExceptionWithTryCatch() {
		uiState.value = UiState.Loading
		viewModelScope.launch {
			try {
				mockApi.getAndroidVersionFeatures(27)
			} catch (exception: Exception) {
				uiState.value = UiState.Error("Network Request failed: $exception")
			}
		}
	}

	fun handleWithCoroutineExceptionHandler() {
		val exceptionHandler = CoroutineExceptionHandler { _, exception ->
			uiState.value = UiState.Error("Network Request failed!! $exception")
		}

		uiState.value = UiState.Loading
		viewModelScope.launch(exceptionHandler) {
			mockApi.getAndroidVersionFeatures(27)
		}
	}

	fun showResultsEvenIfChildCoroutineFails() {
		uiState.value = UiState.Loading
		viewModelScope.launch {
			// supervisorScope 이 없으면 앱 크래시 발생
			supervisorScope {
				val oreoFeatureDeferred = async { mockApi.getAndroidVersionFeatures(27) }
				val pieFeatureDeferred = async { mockApi.getAndroidVersionFeatures(28) }
				val android10FeatureDeferred = async { mockApi.getAndroidVersionFeatures(29) }

				val versionFeatures = listOf(
					oreoFeatureDeferred,
					pieFeatureDeferred,
					android10FeatureDeferred
				).mapNotNull {
					try {
						it.await()
					} catch (exception: Exception) {
						// 코루틴이 즉시 취소되도록 취소 예외를 다시 던져야 합니다.
						// 그렇지 않으면 CancellationException이 무시되고 코루틴은 다음 일시 중단 지점에 도달할 때까지 계속 실행됩니다.
						if (exception is CancellationException) {
							throw exception
						}
						Timber.e("Error loading feature data!")
						null
					}
				}
				uiState.value = UiState.Success(versionFeatures)
			}
		}
	}

	fun showResultsEvenIfChildCoroutineFailsWithRunCatching() {
		uiState.value = UiState.Loading
		viewModelScope.launch {
			val oreoFeatureDeferred = async {
				runCatching {
					mockApi.getAndroidVersionFeatures(27)
				}.onFailure { exception ->
					if (exception is CancellationException) {
						throw exception
					}
					Timber.e("Error loading feature data!")
				}.getOrNull()
			}
			val pieFeatureDeferred = async { mockApi.getAndroidVersionFeatures(28) }
			val android10FeatureDeferred = async { mockApi.getAndroidVersionFeatures(29) }

			val versionFeatures = listOf(
				oreoFeatureDeferred,
				pieFeatureDeferred,
				android10FeatureDeferred
			).mapNotNull {
				it.await()
			}

			uiState.value = UiState.Success(versionFeatures)
		}
	}
}