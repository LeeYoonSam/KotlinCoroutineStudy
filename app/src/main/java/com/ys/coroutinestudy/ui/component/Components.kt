package com.ys.coroutinestudy.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VerticalDivider(
	modifier: Modifier = Modifier,
	alpha: Float = 1f,
	color: Color = MaterialTheme.colors.onSurface.copy(alpha = alpha),
	thickness: Dp = 1.dp,
) {
	Box(
		modifier
			.fillMaxHeight()
			.width(thickness)
			.background(color = color)
	)
}

@Composable
fun HorizontalDivider(
	modifier: Modifier = Modifier,
	alpha: Float = 1f,
	color: Color = MaterialTheme.colors.onSurface.copy(alpha = alpha),
	thickness: Dp = 1.dp
) {
	Box(
		modifier
			.fillMaxWidth()
			.height(thickness)
			.background(color = color)
	)
}