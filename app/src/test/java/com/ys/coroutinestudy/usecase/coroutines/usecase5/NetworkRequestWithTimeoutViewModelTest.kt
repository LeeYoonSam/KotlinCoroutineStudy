package com.ys.coroutinestudy.usecase.coroutines.usecase5

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ys.coroutinestudy.mock.mockAndroidVersions
import com.ys.coroutinestudy.usecase.coroutines.usecase3.FakeErrorApi
import com.ys.coroutinestudy.utils.MainCoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class NetworkRequestWithTimeoutViewModelTest {

	@get:Rule
	val testInstantTaskExecutorRule : TestRule = InstantTaskExecutorRule()

	@get:Rule
	val mainCoroutineScopeRule = MainCoroutineScopeRule()

	private val receivedUiStates = mutableListOf<UiState>()

	@Test
	fun `performNetworkRequest() should return Success UiState on successful network request within timeout`() =
		mainCoroutineScopeRule.runBlockingTest {
			val responseDelay = 1000L
			val timeout = 1001L
			val fakeApi = FakeSuccessApi(responseDelay)
			val viewModel = NetworkRequestWithTimeoutViewModel(fakeApi)
			viewModel.observe()

			assertTrue(receivedUiStates.isEmpty())

			viewModel.performNetworkRequest(timeout)

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
	fun `performNetworkRequest() should return Error UiState with timeout error message if timeout gets exceeded`() =
		mainCoroutineScopeRule.runBlockingTest {
			val responseDelay = 1000L
			val timeout = 999L
			val fakeApi = FakeSuccessApi(responseDelay)
			val viewModel = NetworkRequestWithTimeoutViewModel(fakeApi)
			viewModel.observe()

			assertTrue(receivedUiStates.isEmpty())

			viewModel.performNetworkRequest(timeout)

			advanceUntilIdle()

			assertEquals(
				listOf(
					UiState.Loading,
					UiState.Error("Network Request timed out!")
				),
				receivedUiStates
			)
		}

	@Test
	fun `performNetworkRequest() should return Error UiState on unsuccessful network response`() =
		mainCoroutineScopeRule.runBlockingTest {
			val responseDelay = 1000L
			val timeout = 1001L
			val fakeApi = FakeVersionErrorApi(responseDelay)
			val viewModel = NetworkRequestWithTimeoutViewModel(fakeApi)
			viewModel.observe()

			assertTrue(receivedUiStates.isEmpty())

			viewModel.performNetworkRequest(timeout)

			advanceUntilIdle()

			assertEquals(
				listOf(
					UiState.Loading,
					UiState.Error("Network Request failed!")
				),
				receivedUiStates
			)
		}

	private fun NetworkRequestWithTimeoutViewModel.observe() {
		uiState().observeForever { uiState ->
			if (uiState != null) {
				receivedUiStates.add(uiState)
			}
		}
	}
}