package com.ys.coroutinestudy.usecase.coroutines.usecase2.callbacks

import com.ys.coroutinestudy.mock.AndroidVersion
import com.ys.coroutinestudy.mock.VersionFeatures
import com.ys.coroutinestudy.mock.mockAndroidVersions
import retrofit2.Call
import retrofit2.mock.Calls
import java.io.IOException

class FakeFeaturesErrorApi : CallbackMockApi {
	override fun getRecentAndroidVersions(): Call<List<AndroidVersion>> {
		return Calls.response(mockAndroidVersions)
	}

	override fun getAndroidVersionFeatures(apiLevel: Int): Call<VersionFeatures> {
		return Calls.failure(IOException())
	}
}