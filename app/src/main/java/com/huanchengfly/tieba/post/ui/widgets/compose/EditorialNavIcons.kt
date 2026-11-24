package com.huanchengfly.tieba.post.ui.widgets.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

@Composable
fun EditorialNavIcon(id: String, selected: Boolean, tint: Color, modifier: Modifier = Modifier) {
    val anim = remember { Animatable(0f) }
    var prevSelected by remember { mutableStateOf(selected) }

    LaunchedEffect(selected) {
        val activated = selected && !prevSelected
        // Record the transition before animation can suspend and be cancelled.
        prevSelected = selected
        if (activated) {
            anim.snapTo(0f)
            anim.animateTo(1f, animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing))
        }
    }

    Canvas(modifier = modifier.size(21.dp)) {
        val scale = size.minDimension / 24f
        val stroke = Stroke(width = 1.75.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val t = if (selected) anim.value else 0f
        val wave = if (t > 0f && t < 1f) sin(t * PI).toFloat() else 0f

        when (id) {
            "explore" -> drawExploreIcon(scale, stroke, tint, t, wave)
            "notification" -> drawNotificationIcon(scale, stroke, tint, t)
            "user" -> drawUserIcon(scale, stroke, tint, wave)
            else -> drawHomeIcon(scale, stroke, tint, t, wave)
        }
    }
}

private fun DrawScope.drawHomeIcon(scale: Float, stroke: Stroke, tint: Color, t: Float, wave: Float) {
    val house = Path().apply {
        moveTo(3.5f * scale, 10.5f * scale)
        lineTo(12f * scale, 3.5f * scale)
        lineTo(20.5f * scale, 10.5f * scale)

        moveTo(5.5f * scale, 9.5f * scale)
        lineTo(5.5f * scale, 18.5f * scale)
        quadraticTo(5.5f * scale, 19.5f * scale, 6.5f * scale, 19.5f * scale)
        lineTo(17.5f * scale, 19.5f * scale)
        quadraticTo(18.5f * scale, 19.5f * scale, 18.5f * scale, 18.5f * scale)
        lineTo(18.5f * scale, 9.5f * scale)

        moveTo(9.5f * scale, 19.5f * scale)
        lineTo(9.5f * scale, 14f * scale)
        lineTo(14.5f * scale, 14f * scale)
        lineTo(14.5f * scale, 19.5f * scale)
    }
    val bounce = 1f + 0.13f * wave * (1f - t * 0.5f)
    translate(top = -1.6f * scale * wave) {
        scale(scale = bounce, pivot = Offset(12f * scale, 12f * scale)) {
            drawPath(house, tint, style = stroke)
        }
    }
}

private fun DrawScope.drawExploreIcon(scale: Float, stroke: Stroke, tint: Color, t: Float, wave: Float) {
    val iconCenter = Offset(12f * scale, 12f * scale)
    drawCircle(tint, radius = 8.8f * scale * (1f + 0.08f * wave), center = iconCenter, style = stroke)

    val needle = Path().apply {
        moveTo(15.2f * scale, 8.8f * scale)
        lineTo(13.3f * scale, 13.3f * scale)
        lineTo(8.8f * scale, 15.2f * scale)
        lineTo(10.7f * scale, 10.7f * scale)
        close()
    }
    val angle = if (t > 0f && t < 1f) {
        (sin(t * 2f * PI) * exp(-2.2 * t) * 48.0).toFloat()
    } else {
        0f
    }
    rotate(degrees = angle, pivot = iconCenter) {
        drawPath(needle, tint, style = stroke)
    }
}

private fun DrawScope.drawNotificationIcon(scale: Float, stroke: Stroke, tint: Color, t: Float) {
    val bell = Path().apply {
        moveTo(6f * scale, 15.5f * scale)
        lineTo(6f * scale, 10.5f * scale)
        cubicTo(6f * scale, 7.2f * scale, 8.7f * scale, 4.5f * scale, 12f * scale, 4.5f * scale)
        cubicTo(15.3f * scale, 4.5f * scale, 18f * scale, 7.2f * scale, 18f * scale, 10.5f * scale)
        lineTo(18f * scale, 15.5f * scale)
        lineTo(19.5f * scale, 16.5f * scale)
        lineTo(4.5f * scale, 16.5f * scale)
        close()
    }
    val clapper = Path().apply {
        moveTo(10.2f * scale, 19.2f * scale)
        quadraticTo(12f * scale, 21.2f * scale, 13.8f * scale, 19.2f * scale)
    }
    val pivot = Offset(12f * scale, 4.5f * scale)
    val swing = if (t > 0f && t < 1f) {
        (sin(t * 3f * PI) * exp(-2.6 * t) * 20.0).toFloat()
    } else {
        0f
    }
    rotate(degrees = swing, pivot = pivot) {
        drawPath(bell, tint, style = stroke)
    }
    rotate(degrees = swing * 1.25f, pivot = pivot) {
        drawPath(clapper, tint, style = stroke)
    }
}

private fun DrawScope.drawUserIcon(scale: Float, stroke: Stroke, tint: Color, wave: Float) {
    val iconCenter = Offset(12f * scale, 12f * scale)
    drawCircle(tint, radius = 8.8f * scale * (1f + 0.08f * wave), center = iconCenter, style = stroke)

    val shoulders = Path().apply {
        moveTo(6.6f * scale, 18.2f * scale)
        quadraticTo(12f * scale, 13.8f * scale, 17.4f * scale, 18.2f * scale)
    }
    translate(top = -1.4f * scale * wave) {
        drawCircle(tint, radius = 2.8f * scale, center = Offset(12f * scale, 9.5f * scale), style = stroke)
        drawPath(shoulders, tint, style = stroke)
    }
}

@Composable
fun EditorialBackIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(24.dp)) {
        val scale = size.minDimension / 24f
        val chevron = Path().apply {
            moveTo(14.5f * scale, 6.5f * scale)
            lineTo(9f * scale, 12f * scale)
            lineTo(14.5f * scale, 17.5f * scale)
        }
        val stroke = Stroke(width = 1.85.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawPath(chevron, tint, style = stroke)
    }
}
