package com.huanchengfly.tieba.post.ui.widgets.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos

@Composable
fun EditorialSettingIcon(
    kind: String,
    tint: Color,
    modifier: Modifier = Modifier,
    trigger: Int = 0,
    delayMillis: Int = 0,
) {
    // A new key restarts the shared canvas, including on the initial trigger of zero.
    key(trigger) {
        EditorialIconCanvas(modifier, 1.65.dp, trigger = 1, delayMillis = delayMillis) { stroke, progress ->
            drawSettingIcon(kind, tint, stroke, progress)
        }
    }
}

private fun DrawScope.drawSettingIcon(kind: String, tint: Color, stroke: Stroke, progress: Float) {
    val center = Offset(12f, 12f)
    val pulse = dampedMotion(progress, PI)
    val rebound = dampedMotion(progress)

    fun outline(build: Path.() -> Unit) = drawPath(Path().apply(build), tint, style = stroke)

    fun line(x1: Float, y1: Float, x2: Float, y2: Float) =
        drawLine(tint, Offset(x1, y1), Offset(x2, y2), stroke.width, stroke.cap)

    fun ring(x: Float = 12f, y: Float = 12f, radius: Float = 8.5f) =
        drawCircle(tint, radius, Offset(x, y), style = stroke)

    fun dot(x: Float, y: Float, radius: Float = 1f) = drawCircle(tint, radius, Offset(x, y))

    fun box(x: Float, y: Float, width: Float, height: Float, radius: Float = 1.5f) =
        drawRoundRect(tint, Offset(x, y), Size(width, height), CornerRadius(radius), style = stroke)

    fun stagger(index: Int, spacing: Float, duration: Float) =
        dampedMotion((progress - index * spacing) / duration, PI)

    when (kind) {
        "account" -> {
            translate(top = -3f * pulse) { ring(y = 8.4f, radius = 3.1f) }
            outline {
                moveTo(5f, 19f)
                cubicTo(5f, 12f, 19f, 12f, 19f, 19f)
            }
        }

        "block" -> {
            ring()
            rotate(85f * rebound, center) { line(6f, 18f, 18f, 6f) }
        }

        "brush" -> translate(left = 2f * pulse) {
            rotate(28f * rebound, Offset(7f, 17f)) {
                outline {
                    moveTo(9f, 13f)
                    lineTo(17.4f, 4.6f)
                    quadraticTo(19.5f, 2.8f, 20.2f, 4.5f)
                    quadraticTo(20.5f, 5.3f, 19.5f, 6.7f)
                    lineTo(12.2f, 15.8f)
                    close()
                    moveTo(9f, 13f)
                    cubicTo(4.5f, 12f, 6f, 18.6f, 3.8f, 19.8f)
                    cubicTo(8.3f, 20.5f, 12.2f, 19.6f, 12.2f, 15.8f)
                }
            }
        }

        "layout" -> {
            for (index in 0..3) {
                val x = 4.5f + (index % 2) * 9f
                val y = 4.5f + (index / 2) * 9f
                val pop = stagger(index, 0.12f, 0.64f)
                scale(1f + 0.3f * pop, Offset(x + 3f, y + 3f)) { box(x, y, 6f, 6f) }
            }
        }

        "rocket" -> translate(top = -3.6f * pulse) {
            outline {
                moveTo(9f, 15.5f)
                cubicTo(7.3f, 11.2f, 8.7f, 6.7f, 12f, 4.5f)
                cubicTo(15.3f, 6.7f, 16.7f, 11.2f, 15f, 15.5f)
                close()
                moveTo(8.4f, 12.2f)
                lineTo(6f, 17.5f)
                lineTo(9f, 15.5f)
                moveTo(15.6f, 12.2f)
                lineTo(18f, 17.5f)
                lineTo(15f, 15.5f)
            }
            ring(y = 10.1f, radius = 1.45f)
            outline {
                moveTo(10.5f, 18f)
                quadraticTo(10.8f, 19.5f, 12f, 20.5f + 1.6f * pulse)
                quadraticTo(13.2f, 19.5f, 13.5f, 18f)
            }
        }

        "more" -> {
            for (index in 0..2) {
                val hop = stagger(index, 0.15f, 0.7f)
                dot(6f + index * 6f, 12f - 4.2f * hop, 1.25f)
            }
        }

        "font" -> scale(1f + 0.24f * rebound, Offset(12f, 18.5f)) {
            outline {
                moveTo(6.8f, 15.7f)
                lineTo(12f, 4.7f)
                lineTo(17.2f, 15.7f)
            }
            line(8.4f, 12.3f, 15.6f, 12.3f)
            line(5.2f, 19.3f, 18.8f, 19.3f)
        }

        "moon" -> rotate(-68f * rebound, center) {
            outline {
                moveTo(12f, 4.3f)
                cubicTo(8.4f, 10.2f, 13.5f, 16.1f, 19.6f, 13.3f)
                cubicTo(18.8f, 17.9f, 14.5f, 20.6f, 10.2f, 19.4f)
                cubicTo(2.3f, 17.1f, 3.1f, 5.4f, 12f, 4.3f)
                close()
            }
        }

        "apps" -> {
            for (row in 0..2) {
                for (column in 0..2) {
                    val ripple = stagger(row + column, 0.1f, 0.6f)
                    dot(6f + column * 6f, 6f + row * 6f, 1.05f + 0.72f * ripple)
                }
            }
        }

        "palette" -> rotate(-14f * rebound, center) {
            outline {
                moveTo(12f, 3.8f)
                cubicTo(7.2f, 3.8f, 3.7f, 7.5f, 3.7f, 12f)
                cubicTo(3.7f, 17.6f, 8f, 20.1f, 11.5f, 20.1f)
                cubicTo(14.7f, 20.1f, 12.4f, 16.6f, 14.6f, 16.1f)
                cubicTo(17.2f, 15.5f, 20.3f, 16.5f, 20.3f, 12.1f)
                cubicTo(20.3f, 7.4f, 16.6f, 3.8f, 12f, 3.8f)
                close()
            }
            dot(7.8f, 13.3f, 1f + 0.45f * stagger(0, 0.12f, 0.64f))
            dot(9f, 8.8f, 1f + 0.45f * stagger(1, 0.12f, 0.64f))
            dot(13f, 7.5f, 1f + 0.45f * stagger(2, 0.12f, 0.64f))
            dot(16.5f, 10.4f, 1f + 0.45f * stagger(3, 0.12f, 0.64f))
        }

        "auto_brightness" -> {
            ring(radius = 4.1f)
            for (index in 0..7) {
                rotate(index * 45f + 54f * rebound, center) { line(12f, 3.2f, 12f, 5.2f) }
            }
        }

        "flask" -> {
            outline {
                moveTo(8f, 4.5f)
                lineTo(16f, 4.5f)
                moveTo(9.2f, 4.5f)
                lineTo(9.2f, 9.4f)
                lineTo(5f, 17.3f)
                quadraticTo(3.9f, 19.5f, 6.3f, 19.5f)
                lineTo(17.7f, 19.5f)
                quadraticTo(20.1f, 19.5f, 19f, 17.3f)
                lineTo(14.8f, 9.4f)
                lineTo(14.8f, 4.5f)
            }
            outline {
                moveTo(7f, 13.5f)
                cubicTo(10.5f, 11.2f + 4f * rebound, 13.5f, 16f - 4f * rebound, 17f, 13.5f)
            }
        }

        "fill" -> {
            rotate(30f * rebound, Offset(13f, 9.5f)) {
                outline {
                    moveTo(10.5f, 6f)
                    lineTo(17.5f, 13f)
                    lineTo(12.3f, 18.2f)
                    quadraticTo(10.5f, 20f, 8.7f, 18.2f)
                    lineTo(4.4f, 13.9f)
                    quadraticTo(3.5f, 13f, 4.4f, 12.1f)
                    close()
                    moveTo(6.8f, 4.4f)
                    lineTo(12f, 9.5f)
                }
                line(4.4f, 13f, 17.5f, 13f)
            }
            dot(19.1f, 17f + 3f * pulse, 1.1f)
        }

        "agenda" -> {
            translate(left = -1.9f * pulse) { box(4.5f, 4.5f, 15f, 6f) }
            translate(left = 1.9f * pulse) { box(4.5f, 13.5f, 15f, 6f) }
        }

        "compass" -> {
            ring()
            rotate(92f * rebound, center) {
                outline {
                    moveTo(16f, 8f)
                    lineTo(13.6f, 13.6f)
                    lineTo(8f, 16f)
                    lineTo(10.4f, 10.4f)
                    close()
                }
            }
        }

        "bell" -> rotate(28f * dampedMotion(progress, 3 * PI), Offset(12f, 4.5f)) {
            outline {
                moveTo(5f, 16.5f)
                lineTo(6.5f, 14.4f)
                lineTo(6.5f, 10.5f)
                cubicTo(6.5f, 2.7f, 17.5f, 2.7f, 17.5f, 10.5f)
                lineTo(17.5f, 14.4f)
                lineTo(19f, 16.5f)
                close()
                moveTo(10.2f, 19f)
                quadraticTo(12f, 21.3f, 13.8f, 19f)
            }
        }

        "shield" -> scale(1f + 0.18f * pulse, center) {
            outline {
                moveTo(12f, 4f)
                quadraticTo(15.4f, 5.6f, 19f, 6.4f)
                lineTo(19f, 11.8f)
                quadraticTo(19f, 17.1f, 12f, 20.4f)
                quadraticTo(5f, 17.1f, 5f, 11.8f)
                lineTo(5f, 6.4f)
                quadraticTo(8.6f, 5.6f, 12f, 4f)
                close()
                moveTo(9f, 12f)
                lineTo(11.2f, 14.2f)
                lineTo(15.3f, 9.8f)
            }
        }

        "video_off" -> {
            outline {
                moveTo(9f, 6.7f)
                lineTo(13f, 6.7f)
                quadraticTo(14.5f, 6.7f, 14.5f, 8.2f)
                lineTo(14.5f, 10.7f)
                lineTo(20.4f, 7.9f)
                lineTo(20.4f, 15.6f)
                moveTo(3.6f, 9.5f)
                lineTo(3.6f, 15.8f)
                quadraticTo(3.6f, 17.3f, 5.1f, 17.3f)
                lineTo(11.9f, 17.3f)
            }
            translate(left = 2f * pulse, top = -2f * pulse) { line(4.7f, 4.7f, 19.3f, 19.3f) }
        }

        "hide" -> {
            scale(scaleX = 1f, scaleY = 1f - 0.45f * pulse, pivot = center) {
                outline {
                    moveTo(9.6f, 7.3f)
                    quadraticTo(15.5f, 5.2f, 20.9f, 12f)
                    quadraticTo(20f, 13.4f, 18.5f, 14.6f)
                    moveTo(5.4f, 9.2f)
                    quadraticTo(4.2f, 10.2f, 3.1f, 12f)
                    quadraticTo(8.4f, 18.9f, 14.4f, 16.8f)
                    moveTo(9.6f, 11.6f)
                    quadraticTo(9.3f, 14.3f, 12.4f, 14.4f)
                }
            }
            line(4.7f, 4.7f, 19.3f, 19.3f)
        }

        "copy" -> {
            val slide = 2.4f * pulse
            outline {
                moveTo(9f + slide, 7.7f)
                lineTo(9f + slide, 5.5f - slide)
                quadraticTo(9f + slide, 4f - slide, 10.5f + slide, 4f - slide)
                lineTo(18f + slide, 4f - slide)
                quadraticTo(19.5f + slide, 4f - slide, 19.5f + slide, 5.5f - slide)
                lineTo(19.5f + slide, 14f - slide)
                quadraticTo(19.5f + slide, 15.5f - slide, 18f + slide, 15.5f - slide)
                lineTo(15f, 15.5f - slide)
            }
            box(4.5f, 7.7f, 10.5f, 12.3f)
        }

        "edit" -> {
            rotate(23f * rebound, Offset(6.5f, 16.3f)) {
                outline {
                    moveTo(6.5f, 16.3f)
                    lineTo(7.5f, 12.2f)
                    lineTo(15.8f, 3.9f)
                    quadraticTo(16.8f, 2.9f, 17.8f, 3.9f)
                    lineTo(19f, 5.1f)
                    quadraticTo(20f, 6.1f, 19f, 7.1f)
                    lineTo(10.7f, 15.4f)
                    close()
                    moveTo(14.4f, 5.3f)
                    lineTo(17.6f, 8.5f)
                }
            }
            outline {
                moveTo(5.5f, 19f)
                cubicTo(8.5f, 17.2f + 2.1f * rebound, 9.8f, 21f, 12.5f, 19.3f - 1.4f * rebound)
                quadraticTo(15f, 17.7f, 18f, 19f)
            }
        }

        "add" -> {
            ring()
            rotate(90f * progress + 32f * rebound, center) {
                line(8.6f, 12f, 15.4f, 12f)
                line(12f, 8.6f, 12f, 15.4f)
            }
        }

        "close" -> {
            ring()
            scale(1f - 0.5f * pulse, center) {
                line(9.2f, 9.2f, 14.8f, 14.8f)
                line(9.2f, 14.8f, 14.8f, 9.2f)
            }
        }

        "switch_user" -> {
            val spread = 4.2f * cos(PI * progress).toFloat()
            for (direction in -1..1 step 2) {
                translate(left = direction * spread, top = direction * 4f * pulse) {
                    ring(y = 8.7f, radius = 2.4f)
                    outline {
                        moveTo(8.6f, 17.7f)
                        cubicTo(8.6f, 12.4f, 15.4f, 12.4f, 15.4f, 17.7f)
                    }
                }
            }
        }

        "info" -> {
            ring()
            dot(12f, 8.2f - 2f * pulse, 1f + 0.4f * pulse)
            line(12f, 11.5f, 12f, 16.3f + 1.5f * pulse)
        }

        "time" -> {
            ring()
            rotate(360f * progress + 22f * rebound, center) { line(12f, 12f, 12f, 6.6f) }
            rotate(50f * pulse, center) { line(12f, 12f, 15.7f, 13.9f) }
        }

        "download" -> {
            outline {
                moveTo(4.5f, 15f)
                lineTo(4.5f, 18f)
                quadraticTo(4.5f, 19.5f, 6f, 19.5f)
                lineTo(18f, 19.5f)
                quadraticTo(19.5f, 19.5f, 19.5f, 18f)
                lineTo(19.5f, 15f)
            }
            translate(top = -3.6f * rebound) {
                line(12f, 4.5f, 12f, 15.2f)
                outline {
                    moveTo(8.3f, 11.5f)
                    lineTo(12f, 15.2f)
                    lineTo(15.7f, 11.5f)
                }
            }
        }

        "tune" -> {
            for (index in 0..2) {
                val y = 6f + index * 6f
                val slide = 4.3f * stagger(index, 0.1f, 0.8f)
                val x = if (index == 1) 15.5f - slide else 8.5f + slide
                line(4.2f, y, x - 1.8f, y)
                ring(x, y, 1.8f)
                line(x + 1.8f, y, 19.8f, y)
            }
        }

        else -> {
            ring()
            dot(12f, 12f, 1.2f + 0.65f * pulse)
        }
    }
}
