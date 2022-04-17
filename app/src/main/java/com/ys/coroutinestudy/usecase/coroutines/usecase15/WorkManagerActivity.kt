package com.ys.coroutinestudy.usecase.coroutines.usecase15

import android.os.Bundle
import androidx.activity.viewModels
import com.ys.coroutinestudy.base.BaseActivity
import com.ys.coroutinestudy.common.KEY_DESCRIPTION
import com.ys.coroutinestudy.databinding.ActivityWorkmangerBinding

class WorkManagerActivity : BaseActivity() {

	private val descriptions: String by lazy {
		intent.getStringExtra(KEY_DESCRIPTION).orEmpty()
	}

	override fun getToolbarTitle() = descriptions

	private val binding by lazy { ActivityWorkmangerBinding.inflate(layoutInflater) }
	private val viewModel : WorkManagerViewModel by viewModels {
		ViewModelFactory(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		viewModel.performAnalyticsRequest()
	}
}