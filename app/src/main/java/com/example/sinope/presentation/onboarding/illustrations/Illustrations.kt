package com.example.sinope.presentation.onboarding.illustrations

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.sinope.core.utils.SinopeColors


/** Welcome art: a phone holding a shield, with a QR card tucked against its lower corner. */
fun DrawScope.drawPhoneShieldQr(s: Float, accent: Color) {
    val line = s * 0.018f
    val brand = Brush.linearGradient(listOf(accent, SinopeColors.VioletDeep))

    val pw = s * 0.44f
    val ph = s * 0.70f
    val left = center.x - pw / 2f - s * 0.07f
    val top = center.y - ph / 2f - s * 0.04f
    val pcx = left + pw / 2f

    // Phone body.
    drawRoundRect(
        color = SinopeColors.SurfaceAlt,
        topLeft = Offset(left, top),
        size = Size(pw, ph),
        cornerRadius = CornerRadius(s * 0.075f),
    )
    drawRoundRect(
        brush = brand,
        topLeft = Offset(left, top),
        size = Size(pw, ph),
        cornerRadius = CornerRadius(s * 0.075f),
        style = Stroke(width = line),
    )
    // Earpiece slit.
    drawRoundRect(
        color = accent.copy(alpha = 0.55f),
        topLeft = Offset(pcx - s * 0.06f, top + s * 0.042f),
        size = Size(s * 0.12f, line),
        cornerRadius = CornerRadius(line),
    )

    // Shield on the screen.
    val shield = shieldPath(pcx, top + ph * 0.22f, pw * 0.56f, ph * 0.44f)
    drawPath(shield, color = accent.copy(alpha = 0.12f))
    drawPath(shield, color = accent, style = Stroke(width = line, join = StrokeJoin.Round))
    // Tick inside the shield.
    val tickY = top + ph * 0.42f
    drawPath(
        Path().apply {
            moveTo(pcx - pw * 0.12f, tickY)
            lineTo(pcx - pw * 0.02f, tickY + pw * 0.10f)
            lineTo(pcx + pw * 0.14f, tickY - pw * 0.10f)
        },
        color = accent,
        style = Stroke(width = line, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
    // Code line under the shield, standing in for a generated token.
    drawRoundRect(
        color = accent.copy(alpha = 0.35f),
        topLeft = Offset(pcx - pw * 0.22f, top + ph * 0.76f),
        size = Size(pw * 0.44f, line * 1.4f),
        cornerRadius = CornerRadius(line),
    )

    // QR card overlapping the phone's bottom-right corner.
    val qs = s * 0.34f
    val qLeft = center.x + s * 0.09f
    val qTop = center.y + s * 0.08f
    drawRoundRect(
        color = SinopeColors.Background,
        topLeft = Offset(qLeft, qTop),
        size = Size(qs, qs),
        cornerRadius = CornerRadius(s * 0.05f),
    )
    drawRoundRect(
        brush = brand,
        topLeft = Offset(qLeft, qTop),
        size = Size(qs, qs),
        cornerRadius = CornerRadius(s * 0.05f),
        style = Stroke(width = line),
    )
    val pad = qs * 0.16f
    drawQrGlyph(Offset(qLeft + pad, qTop + pad), qs - pad * 2f, accent)
}

/** Privacy art: a large shield with a padlock at its heart. */
fun DrawScope.drawShieldLock(s: Float, accent: Color) {
    val line = s * 0.022f
    val w = s * 0.54f
    val h = s * 0.64f
    val top = center.y - h / 2f

    val outer = shieldPath(center.x, top, w, h)
    drawPath(
        outer,
        brush = Brush.verticalGradient(
            listOf(accent.copy(alpha = 0.16f), SinopeColors.VioletDeep.copy(alpha = 0.04f)),
        ),
    )
    drawPath(
        outer,
        brush = Brush.linearGradient(listOf(accent, SinopeColors.VioletDeep)),
        style = Stroke(width = line, join = StrokeJoin.Round),
    )
    // Inner echo line for depth.
    drawPath(
        shieldPath(center.x, top + h * 0.07f, w * 0.80f, h * 0.82f),
        color = accent.copy(alpha = 0.28f),
        style = Stroke(width = line * 0.5f, join = StrokeJoin.Round),
    )

    // Padlock.
    val bw = s * 0.17f
    val bh = s * 0.145f
    val bLeft = center.x - bw / 2f
    val bTop = center.y - bh * 0.30f
    val shackleR = bw * 0.32f
    drawArc(
        color = accent,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(center.x - shackleR, bTop - shackleR * 1.25f),
        size = Size(shackleR * 2f, shackleR * 2f),
        style = Stroke(width = line * 0.9f, cap = StrokeCap.Round),
    )
    drawRoundRect(
        color = accent,
        topLeft = Offset(bLeft, bTop),
        size = Size(bw, bh),
        cornerRadius = CornerRadius(s * 0.028f),
    )
    drawCircle(
        color = SinopeColors.Background,
        radius = s * 0.017f,
        center = Offset(center.x, bTop + bh * 0.44f),
    )
}

/** Ready art: a QR code framed by scanner brackets. */
 fun DrawScope.drawScannedQrCode(s: Float, accent: Color) {
    val line = s * 0.024f
    val frame = s * 0.80f
    val fLeft = center.x - frame / 2f
    val fTop = center.y - frame / 2f
    val arm = frame * 0.24f

    // Four corner brackets of the scanner viewfinder.
    val corners = Path().apply {
        moveTo(fLeft, fTop + arm); lineTo(fLeft, fTop); lineTo(fLeft + arm, fTop)
        moveTo(fLeft + frame - arm, fTop); lineTo(fLeft + frame, fTop)
        lineTo(fLeft + frame, fTop + arm)
        moveTo(fLeft + frame, fTop + frame - arm); lineTo(fLeft + frame, fTop + frame)
        lineTo(fLeft + frame - arm, fTop + frame)
        moveTo(fLeft + arm, fTop + frame); lineTo(fLeft, fTop + frame)
        lineTo(fLeft, fTop + frame - arm)
    }
    drawPath(
        corners,
        brush = Brush.linearGradient(listOf(accent, SinopeColors.VioletDeep)),
        style = Stroke(width = line, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )

    val qs = s * 0.52f
    drawQrGlyph(Offset(center.x - qs / 2f, center.y - qs / 2f), qs, accent)
}

private val QR_MODULES = listOf(
    0 to 4, 1 to 3, 1 to 5, 2 to 4,
    3 to 0, 3 to 2, 3 to 4, 3 to 7,
    4 to 1, 4 to 3, 4 to 6, 4 to 8,
    5 to 0, 5 to 4, 5 to 5, 5 to 7,
    6 to 4, 6 to 6, 6 to 8,
    7 to 3, 7 to 5, 7 to 7,
    8 to 4, 8 to 6, 8 to 8,
)

/** A shield outline of [w]×[h] whose apex sits at ([cx], [top]). */
fun shieldPath(cx: Float, top: Float, w: Float, h: Float): Path {
    val hw = w / 2f
    return Path().apply {
        moveTo(cx, top)
        lineTo(cx + hw, top + h * 0.20f)
        lineTo(cx + hw, top + h * 0.52f)
        quadraticTo(cx + hw, top + h * 0.87f, cx, top + h)
        quadraticTo(cx - hw, top + h * 0.87f, cx - hw, top + h * 0.52f)
        lineTo(cx - hw, top + h * 0.20f)
        close()
    }
}

/**
 * Draws a stylised QR code — three finder squares plus a fixed scatter of data modules — filling a
 * square of [side] pixels at [origin]. The module map is fixed so the artwork never shifts.
 */

fun DrawScope.drawQrGlyph(origin: Offset, side: Float, accent: Color) {
    val cell = side / 9f
    val finderStroke = cell * 0.5f

    listOf(0 to 0, 0 to 6, 6 to 0).forEach { (row, col) ->
        val topLeft = Offset(origin.x + col * cell, origin.y + row * cell)
        drawRoundRect(
            color = accent,
            topLeft = Offset(topLeft.x + finderStroke / 2f, topLeft.y + finderStroke / 2f),
            size = Size(cell * 3f - finderStroke, cell * 3f - finderStroke),
            cornerRadius = CornerRadius(cell * 0.7f),
            style = Stroke(width = finderStroke),
        )
        drawRoundRect(
            color = accent,
            topLeft = Offset(topLeft.x + cell, topLeft.y + cell),
            size = Size(cell, cell),
            cornerRadius = CornerRadius(cell * 0.25f),
        )
    }

    QR_MODULES.forEach { (row, col) ->
        drawRoundRect(
            color = accent.copy(alpha = 0.85f),
            topLeft = Offset(origin.x + col * cell + cell * 0.1f, origin.y + row * cell + cell * 0.1f),
            size = Size(cell * 0.8f, cell * 0.8f),
            cornerRadius = CornerRadius(cell * 0.2f),
        )
    }
}
