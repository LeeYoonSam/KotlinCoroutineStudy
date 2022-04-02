package com.ys.coroutinestudy.usecase.coroutines.usecase2.rx

import com.google.gson.Gson
import com.ys.coroutinestudy.mock.AndroidVersion
import com.ys.coroutinestudy.mock.VersionFeatures
import com.ys.coroutinestudy.mock.mockAndroidVersions
import com.ys.coroutinestudy.mock.mockVersionFeaturesAndroid10
import com.ys.coroutinestudy.util.MockNetworkInterceptor
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

fun mockApi(): RxMockApi = createMockApi(
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

interface RxMockApi {

	@GET("recent-android-versions")
	fun getRecentAndroidVersions(): Single<List<AndroidVersion>>

	@GET("android-version-features/{apiLevel}")
	fun getAndroidVersionFeatures(@Path("apiLevel") apiLevel: Int): Single<VersionFeatures>
}

fun createMockApi(interceptor: MockNetworkInterceptor): RxMockApi {
	val okHttpClient = OkHttpClient.Builder()
		.addInterceptor(interceptor)
		.build()

	val retrofit = Retrofit.Builder()
		.baseUrl("http://localhost/")
		.client(okHttpClient)
		.addConverterFactory(GsonConverterFactory.create())
		.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
		.build()

	return retrofit.create(RxMockApi::class.java)
}