package com.ys.coroutinestudy.usecase.coroutines.usecase2

import com.ys.coroutinestudy.mock.AndroidVersion
import com.ys.coroutinestudy.mock.MockApi
import com.ys.coroutinestudy.mock.VersionFeatures
import com.ys.coroutinestudy.mock.mockAndroidVersions
import com.ys.coroutinestudy.mock.mockVersionFeaturesOreo
import com.ys.coroutinestudy.mock.mockVersionFeaturesPie
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

class FakeFeaturesErrorApi : MockApi {

	override suspend fun getRecentAndroidVersions(): List<AndroidVersion> {
		return mockAndroidVersions
	}

	override suspend fun getAndroidVersionFeatures(apiLevel: Int): VersionFeatures {
		return when (apiLevel) {
			27 -> mockVersionFeaturesOreo
			28 -> mockVersionFeaturesPie
			29 -> throw HttpException(
				Response.error<List<VersionFeatures>>(
					500,
					ResponseBody.create(MediaType.parse("application/json"), "")
				)
			)
			else -> throw IllegalArgumentException("apiLevel not found")
		}
	}
}