package com.ys.coroutinestudy.base

import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.ys.coroutinestudy.usecase.coroutines.usecase1.PerformSingleNetworkRequestActivity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UseCase(
    val description: String,
    val targetActivity: Class<out AppCompatActivity>
) : Parcelable

@Parcelize
data class UseCaseCategory(val categoryName: String, val useCases: List<UseCase>) : Parcelable

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

private val coroutinesUseCases =
    UseCaseCategory(
        "Coroutine Use Cases", listOf(
            UseCase(
                useCase1Description,
                PerformSingleNetworkRequestActivity::class.java
            )
        )
    )

val useCaseCategories = listOf(
    coroutinesUseCases
)