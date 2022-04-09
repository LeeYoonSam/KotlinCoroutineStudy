package com.ys.coroutinestudy.usecase.coroutines.usecase7.rx

import com.google.gson.Gson
import com.ys.coroutinestudy.mock.AndroidVersion
import com.ys.coroutinestudy.mock.VersionFeatures
import com.ys.coroutinestudy.mock.mockVersionFeaturesOreo
import com.ys.coroutinestudy.mock.mockVersionFeaturesPie
import com.ys.coroutinestudy.util.MockNetworkInterceptor
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
		// oreo 기능에 대한 첫번째 요청시 시간 초과
		.mock(
			"http://localhost/android-version-features/27",
			Gson().toJson(mockVersionFeaturesOreo),
			200,
			1050,
			persist = false
		)
		// 두번째 요청은 네트워크 에러
		.mock(
			"http://localhost/android-version-features/27",
			Gson().toJson(mockVersionFeaturesOreo),
			500,
			300,
			persist = false
		)
		// 세 번째 요청이 성공했으며 타임아웃을 시간이 초과하지 않음
		.mock(
			"http://localhost/android-version-features/27",
			Gson().toJson(mockVersionFeaturesOreo),
			200,
			100,
		)
		// pie 기능에 대한 첫번째 요청시 시간 초과
		.mock(
			"http://localhost/android-version-features/28",
			Gson().toJson(mockVersionFeaturesPie),
			200,
			1200,
			persist = false
		)
		// 두번째 요청은 네트워크 에러
		.mock(
			"http://localhost/android-version-features/28",
			"Something went wrong on servers side",
			500,
			200,
			persist = false
		)
		// 세 번째 요청이 성공했으며 타임아웃을 시간이 초과하지 않음
		.mock(
			"http://localhost/android-version-features/28",
			Gson().toJson(mockVersionFeaturesPie),
			200,
			100
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