package com.ys.coroutinestudy.usecase.coroutines.usecase17

import android.os.Bundle
import androidx.activity.viewModels
import com.ys.coroutinestudy.base.BaseActivity
import com.ys.coroutinestudy.common.KEY_DESCRIPTION
import com.ys.coroutinestudy.databinding.ActivityCalculateonmainBinding
import com.ys.coroutinestudy.util.setGone
import com.ys.coroutinestudy.util.setVisible
import com.ys.coroutinestudy.util.toast

class PerformCalculationOnMainThreadActivity : BaseActivity() {

    private val descriptions: String by lazy {
        intent.getStringExtra(KEY_DESCRIPTION).orEmpty()
    }

    override fun getToolbarTitle() = descriptions

    private val binding by lazy { ActivityCalculateonmainBinding.inflate(layoutInflater) }
    private val viewModel: PerformCalculationOnMainThreadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.uiState().observe(this) { uiState ->
            if (uiState != null) {
                render(uiState)
            }
        }
        binding.btnCalculateOnMain.setOnClickListener {
            val factorialOf = binding.editTextFactorialOf.text.toString().toIntOrNull()
            if (factorialOf != null) {
                viewModel.performCalculationOnMainThread(factorialOf)
            }
        }
        binding.btnCalculateOnMainUsingYield.setOnClickListener {
            val factorialOf = binding.editTextFactorialOf.text.toString().toIntOrNull()
            if (factorialOf != null) {
                viewModel.performCalculationOnMainThreadUsingYield(factorialOf)
            }
        }
        binding.btnCalculateWithDefaultDispatcher.setOnClickListener {
            val factorialOf = binding.editTextFactorialOf.text.toString().toIntOrNull()
            if (factorialOf != null) {
                viewModel.performCalculationWithDefaultDispatcher(factorialOf)
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                onLoad()
            }
            is UiState.Success -> {
                onSuccess(uiState)
            }
            is UiState.Error -> {
                onError(uiState)
            }
        }
    }

    private fun onLoad() = with(binding) {
        progressBar.setVisible()
        textViewCalculationDuration.text = ""
        btnCalculateOnMain.isEnabled = false
        btnCalculateOnMainUsingYield.isEnabled = false
        btnCalculateWithDefaultDispatcher.isEnabled = false
    }

    private fun onSuccess(uiState: UiState.Success) = with(binding) {
        progressBar.setGone()
        btnCalculateOnMain.isEnabled = true
        btnCalculateOnMainUsingYield.isEnabled = true
        btnCalculateWithDefaultDispatcher.isEnabled = true
        textViewCalculationDuration.text =
            "${uiState.thread}: Calculation took ${uiState.duration}ms"
    }

    private fun onError(uiState: UiState.Error) = with(binding) {
        progressBar.setGone()
        btnCalculateOnMain.isEnabled = true
        btnCalculateOnMainUsingYield.isEnabled = true
        btnCalculateWithDefaultDispatcher.isEnabled = true
        toast(uiState.message)
    }
}