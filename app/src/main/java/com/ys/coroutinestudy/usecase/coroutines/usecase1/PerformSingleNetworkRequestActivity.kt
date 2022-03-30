package com.ys.coroutinestudy.usecase.coroutines.usecase1

import android.os.Bundle
import androidx.activity.viewModels
import com.ys.coroutinestudy.base.BaseActivity
import com.ys.coroutinestudy.common.KEY_DESCRIPTION
import com.ys.coroutinestudy.databinding.ActivityPerformsinglenetworkrequestBinding
import com.ys.coroutinestudy.util.fromHtml
import com.ys.coroutinestudy.util.setGone
import com.ys.coroutinestudy.util.setVisible
import com.ys.coroutinestudy.util.toast
import kotlinx.android.synthetic.main.activity_performsinglenetworkrequest.btnPerformSingleNetworkRequest
import kotlinx.android.synthetic.main.activity_performsinglenetworkrequest.progressBar
import kotlinx.android.synthetic.main.activity_performsinglenetworkrequest.textViewResult

class PerformSingleNetworkRequestActivity: BaseActivity() {

	private val descriptions: String by lazy {
		intent.getStringExtra(KEY_DESCRIPTION).orEmpty()
	}

	private val binding by lazy { ActivityPerformsinglenetworkrequestBinding.inflate(layoutInflater) }
	private val viewModel: PerformSingleNetworkRequestViewModel by viewModels()

	override fun getToolbarTitle() = descriptions

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)

		viewModel.uiState().observe(this) { uiState ->
			if (uiState != null) {
				render(uiState)
			}
		}

		binding.btnPerformSingleNetworkRequest.setOnClickListener {
			viewModel.performSingleNetworkRequest()
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

	private fun onSuccess(uiState: UiState.Success) {
		progressBar.setGone()
		btnPerformSingleNetworkRequest.isEnabled = true
		val readableVersions = uiState.recentVersions.map { "API ${it.apiLevel}: ${it.name}" }

		textViewResult.text = fromHtml(
			"<b>Recent Android Versions</b><br>${readableVersions.joinToString(separator = "<br>")}"
		)
	}

	private fun onError(uiState: UiState.Error) {
		progressBar.setGone()
		btnPerformSingleNetworkRequest.isEnabled = true
		toast(uiState.message)
	}
}