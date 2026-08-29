package com.example.sinope.presentation.onboarding

import androidx.compose.ui.graphics.Color
import com.example.sinope.core.enums.OnboardingArt
import androidx.annotation.StringRes


data class OnBoardingPage(
    @param:StringRes val title: Int,
    @param:StringRes val body: Int? = null,
    val bullets: List<Int> = emptyList(),
    val art: OnboardingArt,
    val accent: Color,
)
