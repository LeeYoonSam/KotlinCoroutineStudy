package com.ys.coroutinestudy.usecase.coroutines.usecase5

import com.google.gson.Gson
import com.ys.coroutinestudy.mock.createMockApi
import com.ys.coroutinestudy.mock.mockAndroidVersions
import com.ys.coroutinestudy.util.MockNetworkInterceptor

fun mockApi() = createMockApi(
    MockNetworkInterceptor()
        .mock(
            "http://localhost/recent-android-versions",
            Gson().toJson(mockAndroidVersions),
            200,
            1000
        )
)