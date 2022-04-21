package com.ys.coroutinestudy.usecase.coroutines.usecase2.callbacks

import com.ys.coroutinestudy.mock.AndroidVersion
import com.ys.coroutinestudy.mock.VersionFeatures
import com.ys.coroutinestudy.mock.mockVersionFeaturesAndroid10
import com.ys.coroutinestudy.mock.mockVersionFeaturesOreo
import com.ys.coroutinestudy.mock.mockVersionFeaturesPie
import retrofit2.Call
import retrofit2.mock.Calls
import java.io.IOException

class FakeVersionsErrorApi : CallbackMockApi {
	override fun getRecentAndroidVersions(): Call<List<AndroidVersion>> {
		return Calls.failure(IOException())
	}

	override fun getAndroidVersionFeatures(apiLevel: Int): Call<VersionFeatures> {
		val featureMocks = when (apiLevel) {
			27 -> mockVersionFeaturesOreo
			28 -> mockVersionFeaturesPie
			29 -> mockVersionFeaturesAndroid10
			else -> throw IllegalArgumentException("apiLevel not found")
		}
		return Calls.response(featureMocks)
	}
}