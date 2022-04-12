package com.ys.coroutinestudy.usecase.coroutines.usecase9

import androidx.lifecycle.viewModelScope
import com.ys.coroutinestudy.base.BaseViewModel
import com.ys.coroutinestudy.mock.MockApi
import com.ys.coroutinestudy.util.addCoroutineDebugInfo
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class DebuggingCoroutinesViewModel(
	private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

	fun performSingleNetworkRequest() {
		uiState.value = UiState.Loading

		// 로깅 시 코루틴 이름이 출력되도록 이 속성을 설정해야 합니다.
		// Thread.currentName() System.setProperty("kotlinx.coroutines.debug", if (BuildConfig.DEBUG) "on" else "off")
		// [KotlinCoroutineStudyApplication]에서 설정

		viewModelScope.launch(CoroutineName("Initial Coroutine")) {
			Timber.d(addCoroutineDebugInfo("Initial coroutine launched"))

			try {
				val recentVersions = mockApi.getRecentAndroidVersions()
				Timber.d(addCoroutineDebugInfo("Recent Android Versions returned"))
				uiState.value = UiState.Success(recentVersions)
			} catch (e: Exception) {
				Timber.d(addCoroutineDebugInfo("Loading recent Android Versions failed"))
				uiState.value = UiState.Error("Network Request failed")
			}

			// 두 개의 계산을 병렬로 수행
			val calculation1Deferred =
				async(CoroutineName("Calculation1")) { performCalculation1() }

			val calculation2Deferred =
				async(CoroutineName("Calculation2")) { performCalculation2() }

			Timber.d(addCoroutineDebugInfo("Result of Calculation1: ${calculation1Deferred.await()}"))
			Timber.d(addCoroutineDebugInfo("Result of Calculation2: ${calculation2Deferred.await()}"))
		}
	}

	private suspend fun performCalculation1() = withContext(Dispatchers.Default) {
		Timber.d(addCoroutineDebugInfo("Starting Calculation1"))
		delay(1000)
		Timber.d(addCoroutineDebugInfo("Calculation1 completed"))
		13
	}

	private suspend fun performCalculation2() = withContext(Dispatchers.Default) {
		Timber.d(addCoroutineDebugInfo("Starting Calculation2"))
		delay(2000)
		Timber.d(addCoroutineDebugInfo("Calculation2 completed"))
		42
	}
}