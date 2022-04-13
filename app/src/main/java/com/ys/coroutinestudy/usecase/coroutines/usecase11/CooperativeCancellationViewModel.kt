package com.ys.coroutinestudy.usecase.coroutines.usecase11

import androidx.lifecycle.viewModelScope
import com.ys.coroutinestudy.base.BaseViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.math.BigInteger
import kotlin.system.measureTimeMillis

class CooperativeCancellationViewModel(
	private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : BaseViewModel<UiState>() {

	private var calculationJob: Job? = null

	fun performCalculation(factorialOf: Int) {
		uiState.value = UiState.Loading
		calculationJob = viewModelScope.launch {
			try {
				var result: BigInteger
				val computationDuration = measureTimeMillis {
					result = calculateFactorialOf(factorialOf)
				}

				var resultString: String
				val stringConversionDuration = measureTimeMillis {
					resultString = convertToString(result)
				}

				uiState.value =
					UiState.Success(resultString, computationDuration, stringConversionDuration)

			} catch (e: Exception) {
				uiState.value = if (e is CancellationException) {
					UiState.Error("Calculation was cancelled")
				} else {
					UiState.Error("Error while calculating result")
				}
			}
		}
	}

	// factorial of n (n!) = 1 * 2 * 3 * 4 * ... * n
	private suspend fun calculateFactorialOf(number: Int): BigInteger =
		withContext(defaultDispatcher) {
			var factorial = BigInteger.ONE
			for (i in 1..number) {
				/**
				 * yield 는 협력 취소를 가능하게 합니다.
				 *
				 * 대안:
				 * - ensureActive()
				 * - isActive() - 정리 작업을 수행할 수 있습니다.
 				 */
				yield()

				factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
			}
			factorial
		}

	private suspend fun convertToString(number: BigInteger): String =
		withContext(defaultDispatcher) {
			number.toString()
		}

	fun cancelCalculation() {
		calculationJob?.cancel()
	}
}