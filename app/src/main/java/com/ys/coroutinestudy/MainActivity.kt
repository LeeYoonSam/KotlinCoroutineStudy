package com.ys.coroutinestudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ys.coroutinestudy.base.useCaseCategories
import com.ys.coroutinestudy.ui.theme.CoroutineStudyTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			CoroutineStudyTheme {
				// A surface container using the 'background' color from the theme
				Surface(color = MaterialTheme.colors.background) {
					UseCaseApp()
				}
			}
		}
	}
}

@Composable
fun UseCaseApp() {
	Scaffold(
		topBar = { TopAppBar(title = "Test") },
		content = {
			CategoryContent()
		}
	)
}

@Composable
fun TopAppBar(
	title: String,
	navigationIcon: @Composable (() -> Unit)? = null
) {
	TopAppBar(
		title = {
			Text(text = title)
		},
		navigationIcon = navigationIcon
	)
}

@Composable
fun CategoryContent() {
	LazyColumn {
		item {
			useCaseCategories.forEach { useCase ->
				CategoryItem(useCase.categoryName)
			}
		}
	}
}

@Composable
fun CategoryItem(categoryName: String) {
	Text(modifier = Modifier
		.fillMaxWidth()
		.padding(32.dp),
		text = categoryName
	)
}

@Preview(showBackground = true)
@Composable
fun TopAppBarPreview() {
	TopAppBar("test")
}