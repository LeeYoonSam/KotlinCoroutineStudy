package com.ys.coroutinestudy.usecase.coroutines.usecase14

import com.ys.coroutinestudy.mock.AndroidVersion
import com.ys.coroutinestudy.mock.MockApi
import com.ys.coroutinestudy.mock.VersionFeatures
import com.ys.coroutinestudy.mock.mockAndroidVersions
import com.ys.coroutinestudy.utils.EndpointShouldNotBeCalledException
import kotlinx.coroutines.delay

class FakeApi : MockApi {

	override suspend fun getRecentAndroidVersions(): List<AndroidVersion> {
		delay(1)
		return mockAndroidVersions
	}

	override suspend fun getAndroidVersionFeatures(apiLevel: Int): VersionFeatures {
		throw EndpointShouldNotBeCalledException()
	}
}