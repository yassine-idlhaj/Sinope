package com.example.sinope.core.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.sinope.core.utils.SinopeColors.Background
import com.example.sinope.core.utils.SinopeColors.BrandGradient
import com.example.sinope.core.utils.SinopeColors.ButtonGradient
import com.example.sinope.core.utils.SinopeColors.Violet
import com.example.sinope.core.utils.SinopeColors.VioletDeep

internal object SinopeColors {
    val Background = Color(0xFF0A0A0F)
    val Surface = Color(0xFF181820)
    val SurfaceAlt = Color(0xFF111118)
    val InputBg = Color(0xFF1A1A25)
    val Border = Color(0xFF2A2A3A)
    val Track = Color(0xFF22222F)

    val TextPrimary = Color(0xFFF0F0FF)
    val TextSecondary = Color(0xFF8888AA)
    val TextMuted = Color(0xFF55556A)

    /**
     * Label colour for text sitting on top of [ButtonGradient]. Matches [Background] so the text
     * reads as cut out of the screen behind the button rather than as a pure-black hole.
     */
    val OnBrand = Background

    val Cyan = Color(0xFF00D4FF)
    val Violet = Color(0xFFA78BFA)
    val VioletDeep = Color(0xFF7C3AED)
    val Pink = Color(0xFFE040FB)
    val Green = Color(0xFF00E5A0)
    val Amber = Color(0xFFFFB700)
    val Danger = Color(0xFFFF4C6B)

    /** Cyan → violet → pink brand sweep used for the title and FAB. */
    val BrandGradient = Brush.linearGradient(listOf(Cyan, VioletDeep, Pink))

    /**
     * Filled-button sweep. Same hues as [BrandGradient] but with the lighter [Violet] in the middle
     * instead of [VioletDeep]: a centred label sits over the midpoint, where the deep violet only
     * gives dark text 3.7:1. [Violet] lifts that to 7.7:1, so every stop clears AA.
     */
    val ButtonGradient = Brush.linearGradient(listOf(Cyan, Violet, Pink))



    val AccountColors: List<Color> = buildList {
        for (hue in 90 until 360 step 6) {
            add(
                Color.hsl(
                    hue = hue.toFloat(),
                    saturation = 0.65f,
                    lightness = 0.50f
                )
            )
        }
    }

}

