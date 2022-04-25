package com.ys.coroutinestudy.usecase.coroutines.usecase6

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ys.coroutinestudy.mock.mockAndroidVersions
import com.ys.coroutinestudy.utils.MainCoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class RetryNetworkRequestViewModelTest {

	@get:Rule
	val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

	@get:Rule
	val mainCoroutineScopeRule: MainCoroutineScopeRule = MainCoroutineScopeRule()

	private val receivedUiStates = mutableListOf<UiState>()

	@Test
	fun `performNetworkRequest() should return Success UiState on successful network response`() =
		mainCoroutineScopeRule.runBlockingTest {
			val responseDelay = 1000L
			val fakeApi = FakeSuccessApi(responseDelay)
			val viewModel = RetryNetworkRequestViewModel(fakeApi)
			viewModel.observe()

			assertTrue(receivedUiStates.isEmpty())

			viewModel.performNetworkRequest()

			advanceUntilIdle()

			assertEquals(
				listOf(
					UiState.Loading,
					UiState.Success(mockAndroidVersions)
				),
				receivedUiStates
			)
		}

	@Test
	fun `performNetworkRequest() should retry network request two times`() =
		mainCoroutineScopeRule.runBlockingTest {
			val responseDelay = 1000L
			val fakeApi = FakeSuccessOnThirdAttemptApi(responseDelay)
			val viewModel = RetryNetworkRequestViewModel(fakeApi)
			viewModel.observe()

			assertTrue(receivedUiStates.isEmpty())

			viewModel.performNetworkRequest()

			val elapsedTime = advanceUntilIdle()

			assertEquals(
				listOf(
					UiState.Loading,
					UiState.Success(mockAndroidVersions)
				),
				receivedUiStates
			)

			assertEquals(
				3,
				fakeApi.requestCount
			)

			// 3*1000 (Request delays) + 100 (initial delay) + 200 (second delay)
			assertEquals(
				3300,
				elapsedTime
			)
		}

	@Test
	fun `performNetworkRequest() should return Error UiState on 3 unsuccessful network responses`() =
		mainCoroutineScopeRule.runBlockingTest {
			val responseDelay = 1000L
			val fakeApi = FakeVersionsErrorApi(responseDelay)
			val viewModel = RetryNetworkRequestViewModel(fakeApi)
			viewModel.observe()

			assertTrue(receivedUiStates.isEmpty())

			viewModel.performNetworkRequest()

			val elapsedTime = advanceUntilIdle()

			assertEquals(
				listOf(
					UiState.Loading,
					UiState.Error("Network Request failed")
				),
				receivedUiStates
			)

			assertEquals(
				3,
				fakeApi.requestCount
			)

			// 3*1000 response delays + 100 (initial delay) + 200 (second delay)
			Assert.assertEquals(
				3300,
				elapsedTime
			)
		}

	private fun RetryNetworkRequestViewModel.observe() {
		uiState().observeForever { uiState ->
			if (uiState != null) {
				receivedUiStates.add(uiState)
			}
		}
	}
}