package com.ys.coroutinestudy.usecase.coroutines.usecase7.callbacks

import com.google.gson.Gson
import com.ys.coroutinestudy.mock.AndroidVersion
import com.ys.coroutinestudy.mock.VersionFeatures
import com.ys.coroutinestudy.mock.mockVersionFeaturesOreo
import com.ys.coroutinestudy.mock.mockVersionFeaturesPie
import com.ys.coroutinestudy.util.MockNetworkInterceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

fun mockApi(): CallbackMockApi = createMockApi(
	MockNetworkInterceptor()
		// oreo 기능에 대한 첫번째 요청시 시간 초과
		.mock(
			"http://localhost/android-version-features/27",
			Gson().toJson(mockVersionFeaturesOreo),
			200,
			1200,
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

interface CallbackMockApi {

	@GET("recent-android-versions")
	fun getRecentAndroidVersions(): Call<List<AndroidVersion>>

	@GET("android-version-features/{apiLevel}")
	fun getAndroidVersionFeatures(@Path("apiLevel") apiLevel: Int): Call<VersionFeatures>
}

fun createMockApi(interceptor: MockNetworkInterceptor): CallbackMockApi {
	val okHttpClient = OkHttpClient.Builder()
		.addInterceptor(interceptor)
		.build()

	val retrofit = Retrofit.Builder()
		.baseUrl("http://localhost/")
		.client(okHttpClient)
		.addConverterFactory(GsonConverterFactory.create())
		.build()

	return retrofit.create(CallbackMockApi::class.java)
}