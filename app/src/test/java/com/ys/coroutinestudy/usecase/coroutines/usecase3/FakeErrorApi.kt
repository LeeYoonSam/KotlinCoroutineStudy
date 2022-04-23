package com.ys.coroutinestudy.usecase.coroutines.usecase3

import com.ys.coroutinestudy.mock.AndroidVersion
import com.ys.coroutinestudy.mock.MockApi
import com.ys.coroutinestudy.mock.VersionFeatures
import com.ys.coroutinestudy.mock.mockVersionFeaturesAndroid10
import com.ys.coroutinestudy.mock.mockVersionFeaturesOreo
import com.ys.coroutinestudy.utils.EndpointShouldNotBeCalledException
import kotlinx.coroutines.delay
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

class FakeErrorApi(private val responseDelay: Long) : MockApi {
	override suspend fun getRecentAndroidVersions(): List<AndroidVersion> {
		throw EndpointShouldNotBeCalledException()
	}

	override suspend fun getAndroidVersionFeatures(apiLevel: Int): VersionFeatures {
		delay(responseDelay)
		return when (apiLevel) {
			27 -> mockVersionFeaturesOreo
			28 -> throw HttpException(
				Response.error<List<AndroidVersion>>(
					500,
					ResponseBody.create(MediaType.parse("application/json"), "")
				)
			)
			29 -> mockVersionFeaturesAndroid10
			else -> throw IllegalArgumentException("apiLevel not found")
		}
	}
}