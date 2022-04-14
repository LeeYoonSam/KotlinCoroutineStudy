package com.ys.coroutinestudy.base

import androidx.appcompat.app.AppCompatActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase1.PerformSingleNetworkRequestActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase10.CalculationInBackgroundActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase11.CooperativeCancellationActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase12.CalculationInSeveralCoroutinesActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase13.ExceptionHandlingActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase2.Perform2SequentialNetworkRequestsActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase2.callbacks.SequentialNetworkRequestsCallbacksActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase2.rx.SequentialNetworkRequestsRxActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase3.PerformNetworkRequestsConcurrentlyActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase4.VariableAmountOfNetworkRequestsActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase5.NetworkRequestWithTimeoutActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase6.RetryNetworkRequestActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase7.TimeoutAndRetryActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase7.callbacks.TimeoutAndRetryCallbackActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase7.rx.TimeoutAndRetryRxActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase8.RoomAndCoroutinesActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase9.DebuggingCoroutinesActivity

sealed class Demo(val description: String) {
    override fun toString() = description
}

class UseCase(
    description: String,
    val targetActivity: Class<out AppCompatActivity>
) : Demo(description)

class UseCaseCategory(
    categoryName: String,
    val useCases: List<Demo>
) : Demo(categoryName)

const val useCase1Description = "#1 Perform single network request"
const val useCase2Description = "#2 Perform two sequential network requests"
const val useCase2UsingCallbacksDescription = "#2 using Callbacks"
const val useCase2UsingRxDescription = "#2 using RxJava"
const val useCase3Description = "#3 Perform several network requests concurrently"
const val useCase4Description = "#4 Perform variable amount of network requests"
const val useCase5Description = "#5 Network request with TimeOut"
const val useCase6Description = "#6 Retry Network request"
const val useCase7Description = "#7 Network requests with timeout and retry"
const val useCase7UsingCallbacksDescription = "#7 Using callbacks"
const val useCase7UsingRxDescription = "#7 Using RxJava"
const val useCase8Description = "#8 Room and Coroutines"
const val useCase9Description = "#9 Debugging Coroutines"
const val useCase10Description = "#10 Offload expensive calculation to background thread"
const val useCase11Description = "#11 Cooperative Cancellation"
const val useCase12Description = "#12 Offload expensive calculation to several coroutines"
const val useCase13Description = "#13 Exception Handling"
const val useCase14Description = "#14 Continue Coroutine when User leaves screen"
const val useCase15Description = "#15 Using WorkManager with Coroutines"
const val useCase16Description =
    "#16 Performance Analysis of dispatchers, number of coroutines and yielding"
const val useCase17Description =
    "#17 Perform heavy calculation on Main Thread without freezing the UI"

val coroutinesUseCases =
    UseCaseCategory(
        categoryName = "Coroutine Use Cases",
        useCases = listOf(
            UseCase(
                useCase1Description,
                PerformSingleNetworkRequestActivity::class.java
            ),
            UseCase(
                useCase2Description,
                Perform2SequentialNetworkRequestsActivity::class.java
            ),
            UseCase(
                useCase2UsingCallbacksDescription,
                SequentialNetworkRequestsCallbacksActivity::class.java
            ),
            UseCase(
                useCase2UsingRxDescription,
                SequentialNetworkRequestsRxActivity::class.java
            ),
            UseCase(
                useCase3Description,
                PerformNetworkRequestsConcurrentlyActivity::class.java
            ),
            UseCase(
                useCase4Description,
                VariableAmountOfNetworkRequestsActivity::class.java
            ),
            UseCase(
                useCase5Description,
                NetworkRequestWithTimeoutActivity::class.java
            ),
            UseCase(
                useCase6Description,
                RetryNetworkRequestActivity::class.java
            ),
            UseCase(
                useCase7Description,
                TimeoutAndRetryActivity::class.java
            ),
            UseCase(
                useCase7UsingCallbacksDescription,
                TimeoutAndRetryCallbackActivity::class.java
            ),
            UseCase(
                useCase7UsingRxDescription,
                TimeoutAndRetryRxActivity::class.java
            ),
            UseCase(
                useCase8Description,
                RoomAndCoroutinesActivity::class.java
            ),
            UseCase(
                useCase9Description,
                DebuggingCoroutinesActivity::class.java
            ),
            UseCase(
                useCase10Description,
                CalculationInBackgroundActivity::class.java
            ),
            UseCase(
                useCase11Description,
                CooperativeCancellationActivity::class.java
            ),
            UseCase(
                useCase12Description,
                CalculationInSeveralCoroutinesActivity::class.java
            ),
            UseCase(
                useCase13Description,
                ExceptionHandlingActivity::class.java
            ),
        )
    )

val AllDemosCategory = UseCaseCategory(
    categoryName = "Activity UseCase",
    listOf(
        coroutinesUseCases,
    )
)