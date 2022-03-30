package com.ys.coroutinestudy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.ys.coroutinestudy.base.AllDemosCategory
import com.ys.coroutinestudy.base.Demo
import com.ys.coroutinestudy.base.UseCase
import com.ys.coroutinestudy.base.UseCaseCategory
import com.ys.coroutinestudy.common.KEY_DESCRIPTION
import com.ys.coroutinestudy.common.Navigator
import com.ys.coroutinestudy.ui.theme.CoroutineStudyTheme
import com.ys.coroutinestudy.util.toast

class MainActivity : ComponentActivity() {

	private var backPressed = 0L

	private val finish: () -> Unit = {
		if (backPressed + 1500 > System.currentTimeMillis()) {
			finishAndRemoveTask()
		} else {
			toast(getString(R.string.app_exit_label))
		}
		backPressed = System.currentTimeMillis()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {

			val activityStarter = fun(useCase: UseCase) {
				startActivity(
					Intent(this@MainActivity, useCase.targetActivity).apply {
						putExtra(KEY_DESCRIPTION, useCase.description)
					}
				)
			}

			val navigator = rememberSaveable(
				saver = Navigator.Saver(AllDemosCategory, onBackPressedDispatcher, activityStarter, finish)
			) {
				Navigator(AllDemosCategory, onBackPressedDispatcher, activityStarter, finish)
			}

			CoroutineStudyTheme {
				// A surface container using the 'background' color from the theme
				Surface(color = MaterialTheme.colors.background) {
					UseCaseApp(
						currentUseCase = navigator.currentDemo,
						backStackTitle = navigator.backStackTitle,
						onNavigateToUseCase = { demo ->
							navigator.navigateTo(demo)
						}
					)
				}
			}
		}
	}
}

@Composable
fun UseCaseApp(
	currentUseCase: Demo,
	backStackTitle: String,
	onNavigateToUseCase: (Demo) -> Unit,

) {
	Scaffold(
		topBar = { DemoAppBar(title = backStackTitle) },
	) { innerPadding ->
		val modifier = Modifier.padding(innerPadding)
		CategoryContent(
			modifier = modifier,
			currentUseCase = currentUseCase,
			onNavigate = onNavigateToUseCase
		)
	}
}

@Composable
fun DemoAppBar(
	title: String,
) {
	TopAppBar(
		title = {
			Text(title, Modifier.testTag("AppBarTitle"))
		}
	)
}

@Composable
private fun CategoryContent(
	modifier: Modifier,
	currentUseCase: Demo,
	onNavigate: (Demo) -> Unit
) {
	Crossfade(targetState = currentUseCase) { demo ->
		Surface(modifier.fillMaxWidth(), color = MaterialTheme.colors.background) {
			DisplayDemo(demo, onNavigate)
		}
	}
}

@Composable
private fun DisplayDemo(demo: Demo, onNavigate: (Demo) -> Unit) {
	when (demo) {
		is UseCaseCategory -> DisplayDemoCategory(demo, onNavigate)
		else -> {}
	}
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun DisplayDemoCategory(category: UseCaseCategory, onNavigate: (Demo) -> Unit) {
	Column(Modifier.verticalScroll(rememberScrollState())) {
		category.useCases.forEach { demo ->
			ListItem(
				text = {
					Text(
						modifier = Modifier.height(56.dp)
							.wrapContentSize(Alignment.Center),
						text = demo.description
					)
				},
				modifier = Modifier.clickable {
					onNavigate(demo)
				}
			)
		}
	}
}