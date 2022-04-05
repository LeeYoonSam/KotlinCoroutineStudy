package com.ys.coroutinestudy.usecase.coroutines.usecase6

import com.google.gson.Gson
import com.ys.coroutinestudy.mock.createMockApi
import com.ys.coroutinestudy.mock.mockAndroidVersions
import com.ys.coroutinestudy.util.MockNetworkInterceptor

fun mockApi() = createMockApi(
    MockNetworkInterceptor()
        .mock(
            "http://localhost/recent-android-versions",
            "something went wrong on server side",
            500,
            1000,
            persist = false
        ).mock(
            "http://localhost/recent-android-versions",
            "something went wrong on server side",
            500,
            1000,
            persist = false
        ).mock(
            "http://localhost/recent-android-versions",
            Gson().toJson(mockAndroidVersions),
            200,
            1000
        )
)