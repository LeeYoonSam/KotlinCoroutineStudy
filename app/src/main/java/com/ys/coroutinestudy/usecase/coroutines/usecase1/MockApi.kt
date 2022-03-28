package com.ys.coroutinestudy.usecase.coroutines.usecase1

import com.google.gson.Gson
import com.ys.coroutinestudy.mock.createMockApi
import com.ys.coroutinestudy.mock.mockAndroidVersions
import com.ys.coroutinestudy.util.MockNetworkInterceptor

fun mockApi() =
	createMockApi(
		MockNetworkInterceptor()
			.mock(
				path = "http://localhost/recent-android-versions",
				body = Gson().toJson(mockAndroidVersions),
				status = 200,
				delayInMs = 1500
			)
	)