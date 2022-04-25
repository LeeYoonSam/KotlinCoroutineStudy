package com.ys.coroutinestudy.usecase.coroutines.usecase6

import com.ys.coroutinestudy.mock.AndroidVersion
import com.ys.coroutinestudy.mock.MockApi
import com.ys.coroutinestudy.mock.VersionFeatures
import com.ys.coroutinestudy.mock.mockAndroidVersions
import com.ys.coroutinestudy.utils.EndpointShouldNotBeCalledException
import kotlinx.coroutines.delay
import java.io.IOException

class FakeSuccessOnThirdAttemptApi(private val responseDelay: Long) : MockApi {

	var requestCount = 0

	override suspend fun getRecentAndroidVersions(): List<AndroidVersion> {
		requestCount++
		delay(responseDelay)

		if (requestCount < 3) {
			throw IOException()
		} else {
			return mockAndroidVersions
		}
	}

	override suspend fun getAndroidVersionFeatures(apiLevel: Int): VersionFeatures {
		throw EndpointShouldNotBeCalledException()
	}
}