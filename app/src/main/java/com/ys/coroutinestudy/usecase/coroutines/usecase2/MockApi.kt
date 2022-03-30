package com.ys.coroutinestudy.usecase.coroutines.usecase2

import com.google.gson.Gson
import com.ys.coroutinestudy.mock.createMockApi
import com.ys.coroutinestudy.mock.mockAndroidVersions
import com.ys.coroutinestudy.mock.mockVersionFeaturesAndroid10
import com.ys.coroutinestudy.util.MockNetworkInterceptor

fun mockApi() = createMockApi(
	MockNetworkInterceptor()
		.mock(
			"http://localhost/recent-android-versions",
			Gson().toJson(mockAndroidVersions),
			200,
			1500
		)
		.mock(
			"http://localhost/android-version-features/29",
			Gson().toJson(mockVersionFeaturesAndroid10),
			200,
			1500
		)
)