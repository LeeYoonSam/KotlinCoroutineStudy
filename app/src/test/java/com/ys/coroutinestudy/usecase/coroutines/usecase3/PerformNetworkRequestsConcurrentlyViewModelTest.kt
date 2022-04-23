package com.ys.coroutinestudy.usecase.coroutines.usecase3

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ys.coroutinestudy.mock.mockVersionFeaturesAndroid10
import com.ys.coroutinestudy.mock.mockVersionFeaturesOreo
import com.ys.coroutinestudy.mock.mockVersionFeaturesPie
import com.ys.coroutinestudy.utils.MainCoroutineScopeRule
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class PerformNetworkRequestsConcurrentlyViewModelTest {

	@get:Rule
	val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

	@get:Rule
	val mainCoroutineScopeRule: MainCoroutineScopeRule = MainCoroutineScopeRule()

	private val receivedUiStates = mutableListOf<UiState>()

	@Test
	fun `performNetworkRequestsSequentially should return data after 3 times the response delay`() =
		mainCoroutineScopeRule.runBlockingTest {
			val responseDelay = 1000L
			val fakeApi = FakeSuccessApi(responseDelay)
			val viewModel = PerformNetworkRequestsConcurrentlyViewModel(fakeApi)
			viewModel.observe()

			assertTrue(receivedUiStates.isEmpty())

			viewModel.performNetworkRequestsSequentially()

			val forwardedTime = advanceUntilIdle()

			assertEquals(
				listOf(
					UiState.Loading,
					UiState.Success(
						listOf(
							mockVersionFeaturesOreo,
							mockVersionFeaturesPie,
							mockVersionFeaturesAndroid10
						)
					)
				),
				receivedUiStates
			)

			// 요청이 실제로 순차적으로 실행되었는지 확인하고 모든 데이터를 수신하는 데 3000ms가 걸렸습니다.
			assertEquals(
				3000,
				forwardedTime
			)
		}

	@Test
	fun `performNetworkRequestsConcurrently should return data after the response delay`() =
		mainCoroutineScopeRule.runBlockingTest {
			val responseDelay = 1000L
			val fakeApi = FakeSuccessApi(responseDelay)
			val viewModel = PerformNetworkRequestsConcurrentlyViewModel(fakeApi)
			viewModel.observe()

			assertTrue(receivedUiStates.isEmpty())

			viewModel.performNetworkRequestsConcurrently()

			val forwardedTime = advanceUntilIdle()

			assertEquals(
				listOf(
					UiState.Loading,
					UiState.Success(
						listOf(
							mockVersionFeaturesOreo,
							mockVersionFeaturesPie,
							mockVersionFeaturesAndroid10
						)
					)
				),
				receivedUiStates
			)

			// 요청이 실제로 1000ms 이내에 동시에 실행되었는지 확인
			assertEquals(
				1000,
				forwardedTime
			)
		}

	@Test
	fun `performNetworkRequestsConcurrently should return Error when network request fails`() =
		mainCoroutineScopeRule.runBlockingTest {
			val responseDelay = 1000L
			val fakeApi = FakeErrorApi(responseDelay)
			val viewModel = PerformNetworkRequestsConcurrentlyViewModel(fakeApi)
			viewModel.observe()

			assertTrue(receivedUiStates.isEmpty())

			viewModel.performNetworkRequestsConcurrently()

			println("advanceUntilIdle: ${advanceUntilIdle()}")

			assertEquals(
				listOf(
					UiState.Loading,
					UiState.Error("Network Request failed")
				),
				receivedUiStates
			)
		}

	private fun PerformNetworkRequestsConcurrentlyViewModel.observe() {
		uiState().observeForever { uiState ->
			if (uiState != null) {
				receivedUiStates.add(uiState)
			}
		}
	}
}