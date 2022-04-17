package com.ys.coroutinestudy.usecase.coroutines.usecase15

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.NetworkType.CONNECTED
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit.SECONDS

class WorkManagerViewModel(private val context: Context) : ViewModel() {

	fun performAnalyticsRequest() {
		val constraints =
			Constraints.Builder().setRequiredNetworkType(CONNECTED).build()

		val request = OneTimeWorkRequestBuilder<AnalyticsWorker>()
			.setConstraints(constraints)
			.setInitialDelay(10, SECONDS)
			.addTag("analyitcs-work-request")
			.build()

		// queue 에서 소비되지 못하면 큐에 대기하다가 인터넷이 연결되면 다시 소비
		WorkManager.getInstance(context).enqueue(request)
	}
}