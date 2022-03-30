package com.ys.coroutinestudy.base

import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ys.coroutinestudy.R
import com.ys.coroutinestudy.util.setGone

abstract class BaseActivity : AppCompatActivity() {

	abstract fun getToolbarTitle(): String

	override fun onStart() {
		super.onStart()
		supportActionBar?.hide()

		setToolbarTitle(getToolbarTitle())
		getUpButton().setOnClickListener {
			finish()
		}
	}

	private fun getUpButton(): ImageView = findViewById(R.id.btnToolbarBack)
	private fun getToolbar(): TextView = findViewById(R.id.toolbarTitle)

	fun hideUpButton() {
		getUpButton().setGone()
	}

	private fun setToolbarTitle(toolbarTitle: String) {
		getToolbar().text = toolbarTitle
	}
}