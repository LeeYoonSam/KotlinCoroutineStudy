package com.ys.coroutinestudy.usecase.coroutines.usecase1

import com.ys.coroutinestudy.mock.AndroidVersion
import com.ys.coroutinestudy.mock.MockApi
import com.ys.coroutinestudy.mock.VersionFeatures
import com.ys.coroutinestudy.mock.mockAndroidVersions
import com.ys.coroutinestudy.utils.EndpointShouldNotBeCalledException

class FakeSuccessApi : MockApi {

	override suspend fun getRecentAndroidVersions(): List<AndroidVersion> {
		return mockAndroidVersions
	}

	override suspend fun getAndroidVersionFeatures(apiLevel: Int): VersionFeatures {
		throw EndpointShouldNotBeCalledException()
	}
}