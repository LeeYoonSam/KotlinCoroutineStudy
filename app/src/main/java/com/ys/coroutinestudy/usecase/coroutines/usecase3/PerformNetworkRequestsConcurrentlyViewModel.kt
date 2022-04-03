package com.ys.coroutinestudy.usecase.coroutines.usecase3

import androidx.lifecycle.viewModelScope
import com.ys.coroutinestudy.base.BaseViewModel
import com.ys.coroutinestudy.mock.MockApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class PerformNetworkRequestsConcurrentlyViewModel(
	private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

	fun performNetworkRequestsSequentially() {
		uiState.value = UiState.Loading
		viewModelScope.launch {
			try {
				val oreoFeatures = mockApi.getAndroidVersionFeatures(27)
				val pieFeatures = mockApi.getAndroidVersionFeatures(28)
				val android10Features = mockApi.getAndroidVersionFeatures(29)

				val versionFeatures = listOf(oreoFeatures, pieFeatures, android10Features)
				uiState.value = UiState.Success(versionFeatures)
			} catch (e: Exception) {
				uiState.value = UiState.Error("Network Request failed")
			}
		}
	}

	fun performNetworkRequestsConcurrently() {
		uiState.value = UiState.Loading

		val oreoFeaturesDeferred = viewModelScope.async { mockApi.getAndroidVersionFeatures(27) }
		val pieFeaturesDeferred = viewModelScope.async { mockApi.getAndroidVersionFeatures(28) }
		val android10FeaturesDeferred = viewModelScope.async { mockApi.getAndroidVersionFeatures(29) }

		viewModelScope.launch {
			try {
				val versionFeatures = awaitAll(oreoFeaturesDeferred, pieFeaturesDeferred, android10FeaturesDeferred)
				uiState.value = UiState.Success(versionFeatures)
			} catch (e: Exception) {
				uiState.value = UiState.Error("Network Request failed")
			}
		}


		/*
        // 대안으로:
        viewModelScope.launch {
            try {
                // 이 코드를 coroutineScope 블록으로 래핑해야 합니다. 그렇지 않으면 네트워크 요청이 실패하면 앱이 충돌합니다.
                coroutineScope {
                    val oreoFeaturesDeferred = async { mockApi.getAndroidVersionFeatures(27) }
                    val pieFeaturesDeferred = async { mockApi.getAndroidVersionFeatures(28) }
                    val android10FeaturesDeferred = async { mockApi.getAndroidVersionFeatures(29) }

                    val oreoFeatures = oreoFeaturesDeferred.await()
                    val pieFeatures = pieFeaturesDeferred.await()
                    val android10Features = android10FeaturesDeferred.await()

                    val versionFeatures = listOf(oreoFeatures, pieFeatures, android10Features)

                    // 다른 대안: (그러나 지연이 실패할 때 약간 다른 동작, 문서 참조)
                    // val versionFeatures = awaitAll(oreoFeaturesDeferred, pieFeaturesDeferred, android10FeaturesDeferred)

                    uiState.value = UiState.Success(versionFeatures)
                }

            } catch (exception: Exception) {
                uiState.value = UiState.Error("Network Request failed")
            }
        }
        */
	}
}