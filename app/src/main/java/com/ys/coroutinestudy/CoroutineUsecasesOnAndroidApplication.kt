package com.ys.coroutinestudy

import android.app.Application
import com.ys.coroutinestudy.usecase.coroutines.usecase14.AndroidVersionDatabase
import com.ys.coroutinestudy.usecase.coroutines.usecase14.AndroidVersionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CoroutineUsecasesOnAndroidApplication : Application() {

	private val applicationScope = CoroutineScope(SupervisorJob())

	val androidVersionRepository by lazy {
		val database = AndroidVersionDatabase.getInstance(applicationContext).androidVersionDao()
		AndroidVersionRepository(
			database,
			applicationScope
		)
	}
}