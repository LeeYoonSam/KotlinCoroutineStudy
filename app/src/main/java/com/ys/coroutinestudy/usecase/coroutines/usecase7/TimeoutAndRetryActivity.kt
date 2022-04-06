package com.ys.coroutinestudy.usecase.coroutines.usecase7

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.ys.coroutinestudy.base.BaseActivity
import com.ys.coroutinestudy.common.KEY_DESCRIPTION
import com.ys.coroutinestudy.databinding.ActivityRetrynetworkrequestBinding
import com.ys.coroutinestudy.util.fromHtml
import com.ys.coroutinestudy.util.setGone
import com.ys.coroutinestudy.util.setVisible
import com.ys.coroutinestudy.util.toast

class TimeoutAndRetryActivity : BaseActivity() {

    private val descriptions: String by lazy {
        intent.getStringExtra(KEY_DESCRIPTION).orEmpty()
    }

    override fun getToolbarTitle() = descriptions

    private val binding by lazy { ActivityRetrynetworkrequestBinding.inflate(layoutInflater) }
    private val viewModel: TimeoutAndRetryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.uiState().observe(this, Observer { uiState ->
            if (uiState != null) {
                render(uiState)
            }
        })
        binding.btnPerformSingleNetworkRequest.setOnClickListener {
            viewModel.performNetworkRequest()
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
        textViewResult.text = ""
        btnPerformSingleNetworkRequest.isEnabled = false
    }

    private fun onSuccess(uiState: UiState.Success) = with(binding) {
        progressBar.setGone()
        btnPerformSingleNetworkRequest.isEnabled = true

        val versionFeatures = uiState.versionFeatures
        val versionFeaturesString = versionFeatures.map {
            "<b>New Features of ${it.androidVersion.name} </b> <br> ${it.features.joinToString(
                prefix = "- ",
                separator = "<br>- "
            )}"
        }.joinToString(separator = "<br><br>")

        textViewResult.text = fromHtml(versionFeaturesString)
    }

    private fun onError(uiState: UiState.Error) = with(binding) {
        progressBar.setGone()
        btnPerformSingleNetworkRequest.isEnabled = true
        toast(uiState.message)
    }
}