package com.ys.coroutinestudy.usecase.coroutines.usecase2.rx

import com.ys.coroutinestudy.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SequentialNetworkRequestsRxViewModel(
	private val mockApi: RxMockApi = mockApi()
) : BaseViewModel<UiState>() {

	private val disposable = CompositeDisposable()

	fun perform2SequentialNetworkRequest() {

		var callCount = 0

		uiState.value = UiState.Loading

		mockApi.getRecentAndroidVersions()
			.doOnSubscribe {
				callCount = 0
			}
			.flatMap { androidVersions ->
				Timber.d("callCount: $callCount")
				callCount ++

				val recentVersion = androidVersions.last()
				mockApi.getAndroidVersionFeatures(recentVersion.apiLevel)
			}
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeBy(
				onSuccess = { featureVersions ->
					uiState.value = UiState.Success(featureVersions)
				},
				onError = {
					uiState.value = UiState.Error("Network Request failed.")
				}
			)
			.addTo(disposable)
	}

	override fun onCleared() {
		super.onCleared()

		disposable.clear()
	}
}