package com.ys.coroutinestudy

import android.app.Application
import timber.log.Timber

class KotlinCoroutineStudyApplication : Application() {
	override fun onCreate() {
		super.onCreate()

		Timber.plant(Timber.DebugTree())

		// 디버그 빌드에서 Kotlin 코루틴에 대한 디버깅 활성화 Thread.currentThread().name을 로깅할 때 코루틴 이름을 인쇄합니다.
		System.setProperty("kotlinx.coroutines.debug", if (BuildConfig.DEBUG) "on" else "off")
	}
}