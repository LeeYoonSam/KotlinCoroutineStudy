package com.ys.coroutinestudy.usecase.coroutines.usecase7.rx

import com.ys.coroutinestudy.base.BaseViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit.MILLISECONDS

class TimeoutAndRetryRxViewModel(
	private val mockApi: RxMockApi = mockApi()
) : BaseViewModel<UiState>() {

	private val disposable = CompositeDisposable()

	fun performNetworkRequest() {
		uiState.value = UiState.Loading

		val timeout = 1000L
		val numberOfRetries = 2

		Single.zip(
			mockApi.getAndroidVersionFeatures(27)
				.timeout(timeout, MILLISECONDS)
				.retry { x, e ->
					Timber.e(e)
					x <= numberOfRetries
				},
			mockApi.getAndroidVersionFeatures(28)
				.timeout(timeout, MILLISECONDS)
				.retry { x, e ->
					Timber.e(e)
					x <= numberOfRetries
				}
		) { versionFeaturesOreo, versionFeaturesPie ->
			listOf(versionFeaturesOreo, versionFeaturesPie)
		}
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribeBy(
				onSuccess = { versionFeatures ->
					uiState.value = UiState.Success(versionFeatures)
				},
				onError = { error ->
					Timber.e(error)
					uiState.value = UiState.Error("Network Request failed")
				})
			.addTo(disposable)
	}
}