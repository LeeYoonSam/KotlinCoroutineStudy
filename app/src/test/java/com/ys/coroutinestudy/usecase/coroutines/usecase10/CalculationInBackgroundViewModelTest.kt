package com.ys.coroutinestudy.usecase.coroutines.usecase10

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ys.coroutinestudy.utils.MainCoroutineScopeRule
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class CalculationInBackgroundViewModelTest {

	@get:Rule
	val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

	@get:Rule
	val mainCoroutineScopeRule: MainCoroutineScopeRule = MainCoroutineScopeRule()

	private val receivedUiStates = mutableListOf<UiState>()

	@Test
	fun `performCalculation() should perform correct calculations`() =
		mainCoroutineScopeRule.runBlockingTest {
			val viewModel = CalculationInBackgroundViewModel(mainCoroutineScopeRule.testDispatcher).apply {
				observe()
			}

			assertTrue(receivedUiStates.isEmpty())

			viewModel.performCalculation(1)

			assertEquals(
				UiState.Loading,
				receivedUiStates.first()
			)

			assertEquals(
				"1",
				(receivedUiStates[1] as UiState.Success).result
			)

			receivedUiStates.clear()

			viewModel.performCalculation(2)

			assertEquals(
				UiState.Loading,
				receivedUiStates.first()
			)

			assertEquals(
				"2",
				(receivedUiStates[1] as UiState.Success).result
			)

			receivedUiStates.clear()

			viewModel.performCalculation(3)

			assertEquals(
				UiState.Loading,
				receivedUiStates.first()
			)

			assertEquals(
				"6",
				(receivedUiStates[1] as UiState.Success).result
			)
		}

	private fun CalculationInBackgroundViewModel.observe() {
		uiState().observeForever { uiState ->
			if (uiState != null) {
				receivedUiStates.add(uiState)
			}
		}
	}
}