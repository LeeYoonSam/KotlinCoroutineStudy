package com.ys.coroutinestudy.usecase.coroutines.usecase14

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.ys.coroutinestudy.CoroutineUsecasesOnAndroidApplication
import com.ys.coroutinestudy.R
import com.ys.coroutinestudy.base.BaseActivity
import com.ys.coroutinestudy.common.KEY_DESCRIPTION
import com.ys.coroutinestudy.databinding.ActivityQueryfromroomdatabaseBinding
import com.ys.coroutinestudy.util.fromHtml
import com.ys.coroutinestudy.util.setGone
import com.ys.coroutinestudy.util.setVisible
import com.ys.coroutinestudy.util.toast

class ContinueCoroutineWhenUserLeavesScreenActivity : BaseActivity() {

    private val descriptions: String by lazy {
        intent.getStringExtra(KEY_DESCRIPTION).orEmpty()
    }

    override fun getToolbarTitle() = descriptions

    private val binding by lazy { ActivityQueryfromroomdatabaseBinding.inflate(layoutInflater) }

    private val viewModel: ContinueCoroutineWhenUserLeavesScreenViewModel by viewModels {
        ViewModelFactory((application as CoroutineUsecasesOnAndroidApplication).androidVersionRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.uiState().observe(this, Observer { uiState ->
            if (uiState != null) {
                render(uiState)
            }
        })
        binding.btnLoadData.setOnClickListener {
            viewModel.loadData()
        }
        binding.btnClearDatabase.setOnClickListener {
            viewModel.clearDatabase()
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                onLoad(uiState)
            }
            is UiState.Success -> {
                onSuccess(uiState)
            }
            is UiState.Error -> {
                onError(uiState)
            }
        }
    }

    private fun onLoad(loadingState: UiState.Loading) = with(binding) {
        when (loadingState) {
            UiState.Loading.LoadFromDb -> {
                progressBarLoadFromDb.setVisible()
                textViewLoadFromDatabase.setVisible()
                imageViewDatabaseLoadSuccessOrError.setGone()
            }
            UiState.Loading.LoadFromNetwork -> {
                progressBarLoadFromNetwork.setVisible()
                textViewLoadFromNetwork.setVisible()
                imageViewNetworkLoadSuccessOrError.setGone()
            }
        }
    }

    private fun onSuccess(uiState: UiState.Success) = with(binding) {
        when (uiState.dataSource) {
            DataSource.Network -> {
                progressBarLoadFromNetwork.setGone()
                imageViewNetworkLoadSuccessOrError.setImageDrawable(getDrawable(R.drawable.ic_check_green_24dp))
                imageViewNetworkLoadSuccessOrError.setVisible()
            }
            DataSource.Database -> {
                progressBarLoadFromDb.setGone()
                imageViewDatabaseLoadSuccessOrError.setImageDrawable(getDrawable(R.drawable.ic_check_green_24dp))
                imageViewDatabaseLoadSuccessOrError.setVisible()
            }
        }

        val readableVersions = uiState.recentVersions.map { "API ${it.apiLevel}: ${it.name}" }
        textViewResult.text = fromHtml(
            "<b>Recent Android Versions [from ${uiState.dataSource.name}]</b><br>${readableVersions.joinToString(
                separator = "<br>"
            )}"
        )
    }

    private fun onError(uiState: UiState.Error) = with(binding) {
        when (uiState.dataSource) {
            is DataSource.Network -> {
                progressBarLoadFromNetwork.setGone()
                imageViewNetworkLoadSuccessOrError.setImageDrawable(getDrawable(R.drawable.ic_clear_red_24dp))
                imageViewNetworkLoadSuccessOrError.setVisible()
            }
            is DataSource.Database -> {
                progressBarLoadFromDb.setGone()
                imageViewDatabaseLoadSuccessOrError.setImageDrawable(getDrawable(R.drawable.ic_clear_red_24dp))
                imageViewDatabaseLoadSuccessOrError.setVisible()
            }
        }
        toast(uiState.message)
    }
}