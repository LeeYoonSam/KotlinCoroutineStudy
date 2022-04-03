package com.ys.coroutinestudy.usecase.coroutines.usecase3

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.ys.coroutinestudy.R
import com.ys.coroutinestudy.base.BaseActivity
import com.ys.coroutinestudy.common.KEY_DESCRIPTION
import com.ys.coroutinestudy.databinding.ActivityPerformnetworkrequestsconcurrentlyBinding
import com.ys.coroutinestudy.util.fromHtml
import com.ys.coroutinestudy.util.setGone
import com.ys.coroutinestudy.util.setVisible
import com.ys.coroutinestudy.util.toast

class PerformNetworkRequestsConcurrentlyActivity : BaseActivity() {

    private val descriptions: String by lazy {
        intent.getStringExtra(KEY_DESCRIPTION).orEmpty()
    }

    private val binding by lazy {
        ActivityPerformnetworkrequestsconcurrentlyBinding.inflate(
            layoutInflater
        )
    }

    private val viewModel: PerformNetworkRequestsConcurrentlyViewModel by viewModels()

    override fun getToolbarTitle() = descriptions

    private var operationStartTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.uiState().observe(this, Observer { uiState ->
            if (uiState != null) {
                render(uiState)
            }
        })
        binding.btnRequestsSequentially.setOnClickListener {
            viewModel.performNetworkRequestsSequentially()
        }
        binding.btnRequestsConcurrently.setOnClickListener {
            viewModel.performNetworkRequestsConcurrently()
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
        operationStartTime = System.currentTimeMillis()
        progressBar.setVisible()
        textViewDuration.text = ""
        textViewResult.text = ""
        disableButtons()
    }

    private fun onSuccess(uiState: UiState.Success) = with(binding) {
        enableButtons()
        progressBar.setGone()
        val duration = System.currentTimeMillis() - operationStartTime
        textViewDuration.text = getString(R.string.duration, duration)

        val versionFeatures = uiState.versionFeatures
        val versionFeaturesString = versionFeatures.joinToString(separator = "<br><br>") {
            "<b>New Features of ${it.androidVersion.name} </b> <br> ${it.features.joinToString(
                prefix = "- ",
                separator = "<br>- "
            )}"
        }

        textViewResult.text = fromHtml(versionFeaturesString)
    }

    private fun onError(uiState: UiState.Error) {
        binding.progressBar.setGone()
        binding.textViewDuration.setGone()
        toast(uiState.message)
        enableButtons()
    }

    private fun enableButtons() {
        binding.btnRequestsSequentially.isEnabled = true
        binding.btnRequestsConcurrently.isEnabled = true
    }

    private fun disableButtons() {
        binding.btnRequestsSequentially.isEnabled = false
        binding.btnRequestsConcurrently.isEnabled = false
    }
}