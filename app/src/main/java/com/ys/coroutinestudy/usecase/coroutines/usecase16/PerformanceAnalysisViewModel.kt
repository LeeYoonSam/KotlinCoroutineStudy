package com.ys.coroutinestudy.usecase.coroutines.usecase16

import androidx.lifecycle.viewModelScope
import com.ys.coroutinestudy.base.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import kotlin.system.measureTimeMillis

class PerformanceAnalysisViewModel(
	private val factorialCalculator: FactorialCalculator = FactorialCalculator()
) : BaseViewModel<UiState>() {

	fun performCalculation(
		factorialOf: Int,
		numberOfCoroutines: Int,
		dispatcher: CoroutineDispatcher,
		yieldDuringCalculation: Boolean = true
	) {
		uiState.value = UiState.Loading
		viewModelScope.launch {

			var factorialResult: BigInteger
			val computationDuration = measureTimeMillis {
				factorialResult =
					factorialCalculator.calculateFactorial(
						factorialOf,
						numberOfCoroutines,
						dispatcher,
						yieldDuringCalculation
					)
			}

			var resultString: String
			val stringConversionDuration = measureTimeMillis {
				resultString = convertToString(factorialResult, dispatcher)
			}

			uiState.value =
				UiState.Success(
					resultString,
					computationDuration,
					stringConversionDuration,
					factorialOf.toString(),
					numberOfCoroutines.toString(),
					dispatcher.toString(),
					yieldDuringCalculation
				)
		}
	}

	private suspend fun convertToString(
		number: BigInteger,
		dispatcher: CoroutineDispatcher
	): String =
		withContext(dispatcher) {
			number.toString()
		}
}