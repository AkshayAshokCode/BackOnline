package com.akshayAshokCode.backonline.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

/** The BackOnline mark (same points as the app icon), on a 24-unit grid: a pulse that ends in a check. */
private val MARK_POINTS = listOf(
    2f to 13f, 6.5f to 13f, 8.5f to 9f, 11f to 17f, 13.5f to 10.5f, 15.5f to 14f, 21.5f to 6.5f
)

/**
 * The mark in a soft disc, coloured by status.
 * - [waiting]: calm ripples spread outward — "we are watching".
 * - otherwise: a slow breathing glow.
 */
@Composable
fun PulseMark(color: Color, waiting: Boolean, modifier: Modifier = Modifier) {
    val animatedColor by animateColorAsState(color, tween(600), label = "markColor")
    val transition = rememberInfiniteTransition(label = "mark")
    val ripple by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing)),
        label = "ripple"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse),
        label = "breathe"
    )

    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        val discRadius = radius * 0.62f

        if (waiting) {
            repeat(2) { k ->
                val p = (ripple + k / 2f) % 1f
                drawCircle(
                    color = animatedColor.copy(alpha = (1f - p) * 0.35f),
                    radius = discRadius + (radius - discRadius) * p,
                    style = Stroke(width = radius * 0.025f)
                )
            }
        }

        drawCircle(color = animatedColor.copy(alpha = if (waiting) 0.14f else breathe), radius = discRadius)

        // Mark, scaled to sit inside the disc
        val unit = discRadius * 1.25f / 24f
        val origin = Offset(center.x - 12f * unit, center.y - 12f * unit)
        val path = Path().apply {
            MARK_POINTS.forEachIndexed { i, (x, y) ->
                val px = origin.x + x * unit
                val py = origin.y + y * unit
                if (i == 0) moveTo(px, py) else lineTo(px, py)
            }
        }
        drawPath(
            path = path,
            color = animatedColor,
            style = Stroke(width = 2.2f * unit, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}
