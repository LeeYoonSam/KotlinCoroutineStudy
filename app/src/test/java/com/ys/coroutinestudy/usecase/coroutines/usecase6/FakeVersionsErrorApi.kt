package com.ys.coroutinestudy.usecase.coroutines.usecase6

import com.ys.coroutinestudy.mock.AndroidVersion
import com.ys.coroutinestudy.mock.MockApi
import com.ys.coroutinestudy.mock.VersionFeatures
import com.ys.coroutinestudy.utils.EndpointShouldNotBeCalledException
import kotlinx.coroutines.delay
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

class FakeVersionsErrorApi (private val responseDelay: Long) : MockApi {

	var requestCount = 0

	override suspend fun getRecentAndroidVersions(): List<AndroidVersion> {
		requestCount ++
		delay(responseDelay)
		throw HttpException(
			Response.error<List<AndroidVersion>>(
				500,
				ResponseBody.create(MediaType.parse("application/json"), "")
			)
		)
	}

	override suspend fun getAndroidVersionFeatures(apiLevel: Int): VersionFeatures {
		throw EndpointShouldNotBeCalledException()
	}
}