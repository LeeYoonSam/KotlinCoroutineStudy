package com.ys.coroutinestudy.usecase.coroutines.usecase1

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ys.coroutinestudy.mock.mockAndroidVersions
import com.ys.coroutinestudy.utils.MainCoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class PerformSingleNetworkRequestViewModelTest {

	@get:Rule
	val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

	@get:Rule
	val mainCoroutineScopeRule: MainCoroutineScopeRule = MainCoroutineScopeRule()

	private val receivedUiState = mutableListOf<UiState>()

	@Test
	fun `should return Success when network request is successful`() =
		mainCoroutineScopeRule.runBlockingTest {
			val fakeApi = FakeSuccessApi()
			val viewModel = PerformSingleNetworkRequestViewModel(fakeApi)
			observeViewModel(viewModel)

			assertTrue(receivedUiState.isEmpty())

			viewModel.performSingleNetworkRequest()

			assertEquals(
				listOf(
					UiState.Loading,
					UiState.Success(mockAndroidVersions)
				),
				receivedUiState
			)
		}

	@Test
	fun `should return Error when network request fails`() =
		mainCoroutineScopeRule.runBlockingTest {
			val fakeApi = FakeErrorApi()
			val viewModel = PerformSingleNetworkRequestViewModel(fakeApi)
			observeViewModel(viewModel)

			assertTrue(receivedUiState.isEmpty())

			viewModel.performSingleNetworkRequest()

			assertEquals(
				listOf(
					UiState.Loading,
					UiState.Error("Network Request failed!")
				),
				receivedUiState
			)
		}

	private fun observeViewModel(viewModel: PerformSingleNetworkRequestViewModel) {
		viewModel.uiState().observeForever { uiState ->
			if (uiState != null) {
				receivedUiState.add(uiState)
			}
		}
	}
}