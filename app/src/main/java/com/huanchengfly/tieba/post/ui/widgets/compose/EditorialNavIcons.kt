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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
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
            anim.animateTo(1f, animationSpec = tween(580, easing = FastOutSlowInEasing))
        }
    }

    Canvas(modifier = modifier.size(21.dp)) {
        val scale = size.minDimension / 24f
        val stroke = Stroke(width = 1.75.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val t = if (selected) anim.value else 0f
        val wave = dampedMotion(t, PI)

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
    val pivot = Offset(12f * scale, 12f * scale)
    val bounce = 1f + 0.22f * wave * (1f - t * 0.4f)
    val tilt = (sin(t * 2 * PI) * exp(-1.8 * t) * 8.0).toFloat()
    translate(top = -3.2f * scale * wave) {
        rotate(degrees = tilt, pivot = pivot) {
            scale(scale = bounce, pivot = pivot) {
                drawPath(house, tint, style = stroke)
            }
        }
    }
}

private fun DrawScope.drawExploreIcon(scale: Float, stroke: Stroke, tint: Color, t: Float, wave: Float) {
    val iconCenter = Offset(12f * scale, 12f * scale)
    drawCircle(tint, radius = 8.8f * scale * (1f + 0.14f * wave), center = iconCenter, style = stroke)

    val needle = Path().apply {
        moveTo(15.2f * scale, 8.8f * scale)
        lineTo(13.3f * scale, 13.3f * scale)
        lineTo(8.8f * scale, 15.2f * scale)
        lineTo(10.7f * scale, 10.7f * scale)
        close()
    }
    val angle = if (t > 0f && t < 1f) {
        (sin(t * 2 * PI) * exp(-1.5 * t) * 78.0).toFloat()
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
        (sin(t * 3 * PI) * exp(-1.8 * t) * 28.0).toFloat()
    } else {
        0f
    }
    rotate(degrees = swing, pivot = pivot) {
        drawPath(bell, tint, style = stroke)
    }
    rotate(degrees = 1.35f * swing, pivot = pivot) {
        drawPath(clapper, tint, style = stroke)
    }
}

private fun DrawScope.drawUserIcon(scale: Float, stroke: Stroke, tint: Color, wave: Float) {
    val iconCenter = Offset(12f * scale, 12f * scale)
    drawCircle(tint, radius = 8.8f * scale * (1f + 0.14f * wave), center = iconCenter, style = stroke)

    val shoulders = Path().apply {
        moveTo(6.6f * scale, 18.2f * scale)
        quadraticTo(12f * scale, 13.8f * scale, 17.4f * scale, 18.2f * scale)
    }
    translate(top = -2.8f * scale * wave) {
        scale(scale = 1f + 0.16f * wave, pivot = iconCenter) {
            drawCircle(tint, radius = 2.8f * scale, center = Offset(12f * scale, 9.5f * scale), style = stroke)
            drawPath(shoulders, tint, style = stroke)
        }
    }
}

@Composable
fun EditorialMenuIcon(
    kind: String,
    tint: Color,
    modifier: Modifier = Modifier,
    trigger: Int = 0,
    active: Boolean = false,
    delayMillis: Int = 0,
) {
    EditorialIconCanvas(modifier, 1.65.dp, trigger, delayMillis) { stroke, progress ->
        val pulse = dampedMotion(progress, PI)
        val rebound = dampedMotion(progress)
        val center = Offset(12f, 12f)
        val activeFill = tint.copy(alpha = tint.alpha * 0.14f)

        fun drawOutlineCircle(radius: Float = 8.5f) {
            drawCircle(tint, radius = radius, center = center, style = stroke)
        }

        when (kind) {
            "collect" -> {
                val bookmark = Path().apply {
                    moveTo(6.5f, 4.5f)
                    lineTo(17.5f, 4.5f)
                    lineTo(17.5f, 19.5f)
                    lineTo(12f, 15.5f)
                    lineTo(6.5f, 19.5f)
                    close()
                }
                translate(top = -4.2f * pulse) {
                    scale(scale = 1f + 0.20f * pulse, pivot = center) {
                        if (active) drawPath(bookmark, activeFill)
                        drawPath(bookmark, tint, style = stroke)
                    }
                }
            }

            "history" -> {
                drawOutlineCircle()
                val hands = Path().apply {
                    moveTo(12f, 7.5f)
                    lineTo(12f, 12f)
                    lineTo(15.2f, 13.8f)
                }
                rotate(degrees = 180f * pulse, pivot = center) {
                    drawPath(hands, tint, style = stroke)
                }
            }

            "theme" -> {
                val topLayer = Path().apply {
                    moveTo(12f, 4.5f)
                    lineTo(19.5f, 9.5f)
                    lineTo(12f, 14.5f)
                    lineTo(4.5f, 9.5f)
                    close()
                }
                val bottomLayer = Path().apply {
                    moveTo(4.5f, 14.5f)
                    lineTo(12f, 19.5f)
                    lineTo(19.5f, 14.5f)
                }
                scale(scale = 1f + 0.12f * pulse, pivot = center) {
                    translate(top = -2.8f * pulse) {
                        drawPath(topLayer, tint, style = stroke)
                    }
                    translate(top = 3.0f * pulse) {
                        drawPath(bottomLayer, tint, style = stroke)
                    }
                }
            }

            "service" -> {
                drawOutlineCircle(radius = 8.5f * (1f + 0.12f * pulse))
                val questionMark = Path().apply {
                    moveTo(9.8f, 9.5f)
                    quadraticTo(10f, 7.5f, 12f, 7.5f)
                    quadraticTo(14.2f, 7.5f, 14.2f, 9.5f)
                    cubicTo(14.2f, 11f, 12f, 11f, 12f, 12.2f)
                    lineTo(12f, 13.4f)
                }
                rotate(degrees = 24f * rebound, pivot = center) {
                    drawPath(questionMark, tint, style = stroke)
                    drawCircle(tint, radius = 0.9f, center = Offset(12f, 16.2f))
                }
            }

            "settings" -> {
                fun drawSlider(y: Float, knobX: Float) {
                    val knobRadius = 2f
                    drawLine(
                        tint, Offset(4.5f, y), Offset(knobX - knobRadius, y),
                        stroke.width, stroke.cap,
                    )
                    drawCircle(tint, radius = knobRadius, center = Offset(knobX, y), style = stroke)
                    drawLine(
                        tint, Offset(knobX + knobRadius, y), Offset(19.5f, y),
                        stroke.width, stroke.cap,
                    )
                }

                drawSlider(y = 7f, knobX = 10.2f + 5.0f * rebound)
                drawSlider(y = 12f, knobX = 15.2f - 5.2f * rebound)
                drawSlider(y = 17f, knobX = 9.2f + 4.6f * rebound)
            }

            "about" -> {
                drawOutlineCircle(radius = 8.5f * (1f + 0.12f * pulse))
                drawLine(tint, Offset(12f, 11.2f), Offset(12f, 16.2f), stroke.width, stroke.cap)
                drawCircle(
                    tint,
                    radius = 0.9f + 0.85f * pulse,
                    center = Offset(12f, 8.2f - 2.8f * pulse),
                )
            }

            "see_lz" -> {
                val shoulders = Path().apply {
                    moveTo(4.5f, 19.5f)
                    cubicTo(4.5f, 13f, 18.5f, 13f, 18.5f, 19.5f)
                    close()
                }
                val head = Offset(11.5f, 8.9f)
                translate(top = -3.4f * pulse) {
                    scale(scale = 1f + 0.16f * pulse, pivot = center) {
                        if (active) {
                            drawCircle(activeFill, radius = 3.2f, center = head)
                            drawPath(shoulders, activeFill)
                        }
                        drawCircle(tint, radius = 3.2f, center = head, style = stroke)
                        drawPath(shoulders, tint, style = stroke)
                    }
                }
                drawCircle(tint, radius = 1.05f * (1f + 0.65f * pulse), center = Offset(19.2f, 5.6f))
            }

            "pure_read" -> {
                val leftPage = Path().apply {
                    moveTo(12f, 7.3f)
                    quadraticTo(8.2f, 4.1f, 3.8f, 6f)
                    lineTo(3.8f, 17.7f)
                    quadraticTo(8.2f, 16f, 12f, 19.2f)
                }
                val spine = Offset(12f, 12f)
                val spread = 1f + 0.26f * pulse
                for (direction in -1..1 step 2) {
                    scale(scaleX = -direction * spread, scaleY = 1f, pivot = spine) {
                        // The open contour closes along the spine for the fill.
                        if (active) drawPath(leftPage, activeFill)
                        drawPath(leftPage, tint, style = stroke)
                    }
                }
                drawLine(tint, Offset(12f, 7.3f), Offset(12f, 19.2f), stroke.width, stroke.cap)
            }

            "sort" -> {
                val arrow = Path().apply {
                    moveTo(4f, 8.5f)
                    lineTo(8f, 4.5f)
                    lineTo(12f, 8.5f)
                    moveTo(8f, 4.5f)
                    lineTo(8f, 18f)
                }
                translate(top = -3.5f * pulse) {
                    drawPath(arrow, tint, style = stroke)
                }
                translate(top = 3.5f * pulse) {
                    rotate(degrees = 180f, pivot = center) {
                        drawPath(arrow, tint, style = stroke)
                    }
                }
            }

            "jump_page" -> {
                val plane = Path().apply {
                    moveTo(3.5f, 10.3f)
                    lineTo(19.2f, 4.8f)
                    lineTo(14.1f, 20f)
                    lineTo(10.2f, 13.9f)
                    close()
                    moveTo(10.2f, 13.9f)
                    lineTo(19.2f, 4.8f)
                }
                translate(left = 3.6f * pulse, top = -3.6f * pulse) {
                    scale(scale = 1f + 0.16f * pulse, pivot = center) {
                        drawPath(plane, tint, style = stroke)
                    }
                }
            }

            "share" -> {
                val tray = Path().apply {
                    moveTo(4.5f, 13.5f)
                    lineTo(4.5f, 18f)
                    quadraticTo(4.5f, 19.8f, 6.3f, 19.8f)
                    lineTo(17.7f, 19.8f)
                    quadraticTo(19.5f, 19.8f, 19.5f, 18f)
                    lineTo(19.5f, 13.5f)
                }
                val arrow = Path().apply {
                    moveTo(8.1f, 9f)
                    lineTo(12f, 5.1f)
                    lineTo(15.9f, 9f)
                    moveTo(12f, 5.1f)
                    lineTo(12f, 15.6f)
                }
                drawPath(tray, tint, style = stroke)
                translate(top = -4.2f * pulse) {
                    drawPath(arrow, tint, style = stroke)
                }
            }

            "copy_link" -> {
                val links = Path().apply {
                    moveTo(10.4f, 13.6f)
                    cubicTo(9f, 12.2f, 9f, 9.9f, 10.4f, 8.5f)
                    lineTo(14.1f, 4.8f)
                    cubicTo(15.5f, 3.4f, 17.8f, 3.4f, 19.2f, 4.8f)
                    cubicTo(20.6f, 6.2f, 20.6f, 8.5f, 19.2f, 9.9f)
                    lineTo(17.6f, 11.5f)

                    moveTo(13.6f, 10.4f)
                    cubicTo(15f, 11.8f, 15f, 14.1f, 13.6f, 15.5f)
                    lineTo(9.9f, 19.2f)
                    cubicTo(8.5f, 20.6f, 6.2f, 20.6f, 4.8f, 19.2f)
                    cubicTo(3.4f, 17.8f, 3.4f, 15.5f, 4.8f, 14.1f)
                    lineTo(6.4f, 12.5f)
                }
                rotate(degrees = 12f * rebound, pivot = center) {
                    scale(scale = 1f + 0.24f * pulse, pivot = center) {
                        drawPath(links, tint, style = stroke)
                    }
                }
            }

            "report" -> {
                val triangle = Path().apply {
                    moveTo(10.7f, 5.1f)
                    quadraticTo(12f, 2.9f, 13.3f, 5.1f)
                    lineTo(20.4f, 17.2f)
                    quadraticTo(21.7f, 19.5f, 19f, 19.5f)
                    lineTo(5f, 19.5f)
                    quadraticTo(2.3f, 19.5f, 3.6f, 17.2f)
                    close()
                }
                rotate(degrees = 20f * rebound, pivot = center) {
                    drawPath(triangle, tint, style = stroke)
                    drawLine(tint, Offset(12f, 9.1f), Offset(12f, 13.2f), stroke.width, stroke.cap)
                    drawCircle(tint, radius = 0.9f, center = Offset(12f, 16.1f))
                }
            }

            "delete" -> {
                val bin = Path().apply {
                    moveTo(6.2f, 8.7f)
                    lineTo(7.2f, 18.4f)
                    quadraticTo(7.4f, 20f, 9f, 20f)
                    lineTo(15f, 20f)
                    quadraticTo(16.6f, 20f, 16.8f, 18.4f)
                    lineTo(17.8f, 8.7f)
                    moveTo(10.3f, 11.6f)
                    lineTo(10.6f, 16.8f)
                    moveTo(13.7f, 11.6f)
                    lineTo(13.4f, 16.8f)
                }
                val lid = Path().apply {
                    moveTo(4.8f, 7f)
                    lineTo(19.2f, 7f)
                    moveTo(9.2f, 7f)
                    lineTo(9.2f, 5.6f)
                    quadraticTo(9.2f, 4.5f, 10.3f, 4.5f)
                    lineTo(13.7f, 4.5f)
                    quadraticTo(14.8f, 4.5f, 14.8f, 5.6f)
                    lineTo(14.8f, 7f)
                }
                drawPath(bin, tint, style = stroke)
                rotate(degrees = -28f * pulse, pivot = Offset(6.2f, 7f)) {
                    drawPath(lid, tint, style = stroke)
                }
            }
        }
    }
}

@Composable
fun EditorialActionIcon(
    kind: String,
    tint: Color,
    modifier: Modifier = Modifier,
    trigger: Int = 0,
    delayMillis: Int = 0,
) {
    EditorialIconCanvas(modifier, 1.7.dp, trigger, delayMillis) { stroke, progress ->
        val pulse = dampedMotion(progress, PI)
        val rebound = dampedMotion(progress)
        val center = Offset(12f, 12f)

        fun drawRoundedOutline(x: Float, y: Float, width: Float, height: Float, radius: Float) {
            drawRoundRect(
                color = tint,
                topLeft = Offset(x, y),
                size = Size(width, height),
                cornerRadius = CornerRadius(radius),
                style = stroke,
            )
        }

        when (kind) {
            "sign" -> {
                drawRoundedOutline(4.2f, 5.8f, 15.6f, 14f, 2.4f)
                drawLine(tint, Offset(8f, 4.1f), Offset(8f, 7.4f), stroke.width, stroke.cap)
                drawLine(tint, Offset(16f, 4.1f), Offset(16f, 7.4f), stroke.width, stroke.cap)
                val checkmark = Path().apply {
                    moveTo(8.6f, 13.2f)
                    lineTo(11f, 15.6f)
                    lineTo(15.5f, 11f)
                }
                translate(top = -3.4f * rebound) {
                    scale(scale = 1f + 0.18f * pulse, pivot = Offset(12f, 13.3f)) {
                        drawPath(checkmark, tint, style = stroke)
                    }
                }
            }

            "layout_single" -> {
                scale(scale = 1f + 0.22f * pulse, pivot = center) {
                    drawRoundedOutline(4.3f, 5f - pulse, 15.4f, 5.4f, 1.5f)
                    drawRoundedOutline(4.3f, 13.6f + pulse, 15.4f, 5.4f, 1.5f)
                }
            }

            "layout_grid" -> {
                rotate(degrees = 18f * rebound, pivot = center) {
                    scale(scale = 1f + 0.22f * pulse, pivot = center) {
                        drawRoundedOutline(4.5f, 4.5f, 5.8f, 5.8f, 1.25f)
                        drawRoundedOutline(13.7f, 4.5f, 5.8f, 5.8f, 1.25f)
                        drawRoundedOutline(4.5f, 13.7f, 5.8f, 5.8f, 1.25f)
                        drawRoundedOutline(13.7f, 13.7f, 5.8f, 5.8f, 1.25f)
                    }
                }
            }

            "search" -> {
                val lensCenter = Offset(10.5f, 10.5f)
                val radius = 5.7f * (1f + 0.22f * pulse)
                val joint = lensCenter + Offset(radius, radius) * 0.7071f
                rotate(degrees = 22f * rebound, pivot = joint) {
                    drawLine(tint, joint, joint + Offset(4.9f, 4.9f), stroke.width, stroke.cap)
                }
                drawCircle(tint, radius = radius, center = lensCenter, style = stroke)
            }

            "more" -> {
                for (index in 0..2) {
                    val dotProgress = (progress - index * 0.14f) / 0.72f
                    val hop = dampedMotion(dotProgress, PI)
                    drawCircle(
                        tint,
                        radius = 1.1f,
                        center = Offset(12f, 6f + index * 6f - 4.8f * hop),
                    )
                }
            }
        }
    }
}

@Composable
internal fun EditorialIconCanvas(
    modifier: Modifier,
    strokeWidth: Dp,
    trigger: Int,
    delayMillis: Int,
    drawIcon: DrawScope.(Stroke, Float) -> Unit,
) {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            if (delayMillis > 0) delay(delayMillis.toLong())
            anim.snapTo(0f)
            anim.animateTo(1f, tween(560, easing = FastOutSlowInEasing))
        }
    }

    Canvas(modifier = modifier.size(20.dp)) {
        val unit = size.minDimension / 24f
        if (unit <= 0f) return@Canvas

        val stroke = Stroke(
            width = strokeWidth.toPx() / unit,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        val progress = if (trigger > 0) anim.value else 0f
        translate(left = (size.width - 24f * unit) / 2f, top = (size.height - 24f * unit) / 2f) {
            scale(scale = unit, pivot = Offset.Zero) {
                drawIcon(stroke, progress)
            }
        }
    }
}

internal fun dampedMotion(progress: Float, radians: Double = 2 * PI): Float =
    if (progress > 0f && progress < 1f) {
        (sin(progress * radians) * exp(-1.45 * progress)).toFloat()
    } else {
        0f
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

@Composable
fun EditorialChevronRightIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(16.dp)) {
        val scale = size.minDimension / 24f
        val chevron = Path().apply {
            moveTo(9.5f * scale, 6.5f * scale)
            lineTo(15f * scale, 12f * scale)
            lineTo(9.5f * scale, 17.5f * scale)
        }
        val stroke = Stroke(width = 1.65.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        drawPath(chevron, tint, style = stroke)
    }
}
