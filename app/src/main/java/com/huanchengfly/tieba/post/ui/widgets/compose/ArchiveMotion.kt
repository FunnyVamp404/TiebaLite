package com.huanchengfly.tieba.post.ui.widgets.compose

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.min

val LocalArchiveMotionProgress = compositionLocalOf { 1f }

val ArchivePlaneEasing = CubicBezierEasing(0.18f, 0.72f, 0.18f, 1.0f)

fun Modifier.archivePlaneTransition(
    progress: Float,
    isOutgoing: Boolean,
    direction: Float,
): Modifier {
    val eased = ArchivePlaneEasing.transform(progress.coerceIn(0f, 1f))
    return graphicsLayer {
        if (isOutgoing) {
            translationX = 10.dp.toPx() * direction * eased
            translationY = -4.dp.toPx() * eased
        } else {
            translationX = -24.dp.toPx() * direction * (1f - eased)
            translationY = 12.dp.toPx() * (1f - eased)
        }
    }.drawWithContent {
        if (isOutgoing || eased >= 0.999f) {
            drawContent()
        } else if (eased > 0.001f) {
            val slant = min(180.dp.toPx(), size.width * 0.46f)
            val edge = (size.width + slant) * eased
            fun x(value: Float) = if (direction >= 0f) value else size.width - value

            val path = Path().apply {
                moveTo(x(0f), 0f)
                lineTo(x(edge), 0f)
                lineTo(x(edge - slant), size.height)
                lineTo(x(0f), size.height)
                close()
            }
            clipPath(path) { this@drawWithContent.drawContent() }
        }
    }
}

fun Modifier.archiveReveal(
    start: Float = 0.08f,
    end: Float = 0.72f,
    vertical: Boolean = false,
): Modifier = composed {
    val clock = LocalArchiveMotionProgress.current
    if (clock >= 0.999f) return@composed this

    val u = ((clock - start) / (end - start)).coerceIn(0f, 1f)
    val p = 1f - (1f - u) * (1f - u) * (1f - u)
    graphicsLayer {
        translationX = if (!vertical) -8.dp.toPx() * (1f - p) else 0f
        translationY = if (vertical) -6.dp.toPx() * (1f - p) else 0f
    }.drawWithContent {
        if (p > 0.001f) {
            clipRect(
                right = if (!vertical) size.width * p else size.width,
                bottom = if (vertical) size.height * p else size.height,
            ) {
                this@drawWithContent.drawContent()
            }
        }
    }
}
