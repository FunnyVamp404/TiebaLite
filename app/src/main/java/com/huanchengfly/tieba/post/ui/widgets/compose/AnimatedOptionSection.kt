package com.huanchengfly.tieba.post.ui.widgets.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun AnimatedOptionSection(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val clock = remember { Animatable(if (expanded) 1f else 0f) }
    LaunchedEffect(expanded) {
        clock.animateTo(
            targetValue = if (expanded) 1f else 0f,
            animationSpec = if (expanded) {
                tween(durationMillis = 420, easing = CubicBezierEasing(0.215f, 0.61f, 0.355f, 1f))
            } else {
                tween(durationMillis = 320, easing = CubicBezierEasing(0.55f, 0.055f, 0.675f, 0.19f))
            },
        )
    }

    if (!expanded && clock.value == 0f) return

    val touchTargetWidth = LocalViewConfiguration.current.minimumTouchTargetSize.width
    Box(
        modifier = modifier
            .clipToBounds()
            .layout { measurable, constraints ->
                val child = measurable.measure(constraints)
                val height = constraints.constrainHeight((child.height * clock.value).roundToInt())
                layout(child.width, height) {
                    child.place(0, 0)
                }
            },
    ) {
        Box(
            modifier = Modifier
                .drawWithContent {
                    val progress = clock.value
                    val remaining = 1f - progress
                    val exitOffset = if (expanded) 0f else size.width + touchTargetWidth.roundToPx()
                    clipRect(bottom = size.height * progress) {
                        translate(-exitOffset - 8.dp.toPx() * remaining, -6.dp.toPx() * remaining) {
                            this@drawWithContent.drawContent()
                        }
                    }
                }
                .layout { measurable, constraints ->
                    val child = measurable.measure(constraints)
                    layout(child.width, child.height) {
                        // Move the exiting content beyond hit-test bounds and offset only its drawing back.
                        val exitOffset = if (expanded) 0 else child.width + touchTargetWidth.roundToPx()
                        child.place(exitOffset, 0)
                    }
                }
                .clipToBounds(),
            content = { content() },
        )
    }
}
