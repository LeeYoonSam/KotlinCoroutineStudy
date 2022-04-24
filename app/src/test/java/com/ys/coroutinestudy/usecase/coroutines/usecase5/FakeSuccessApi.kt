package com.ys.coroutinestudy.usecase.coroutines.usecase5

import com.ys.coroutinestudy.mock.AndroidVersion
import com.ys.coroutinestudy.mock.MockApi
import com.ys.coroutinestudy.mock.VersionFeatures
import com.ys.coroutinestudy.mock.mockAndroidVersions
import com.ys.coroutinestudy.mock.mockVersionFeaturesAndroid10
import com.ys.coroutinestudy.mock.mockVersionFeaturesOreo
import com.ys.coroutinestudy.mock.mockVersionFeaturesPie
import kotlinx.coroutines.delay

class FakeSuccessApi(private val responseDelay: Long) : MockApi {

	override suspend fun getRecentAndroidVersions(): List<AndroidVersion> {
		delay(responseDelay)
		return mockAndroidVersions
	}

	override suspend fun getAndroidVersionFeatures(apiLevel: Int): VersionFeatures {
		delay(responseDelay)
		return when (apiLevel) {
			27 -> mockVersionFeaturesOreo
			28 -> mockVersionFeaturesPie
			29 -> mockVersionFeaturesAndroid10
			else -> throw IllegalArgumentException("apiLevel not found")
		}
	}
}