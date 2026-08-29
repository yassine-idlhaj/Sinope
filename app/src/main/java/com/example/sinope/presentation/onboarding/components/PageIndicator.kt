package com.example.sinope.presentation.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.presentation.onboarding.OnBoardingPage

@Composable
fun PagerIndicator(
    modifier: Modifier = Modifier,
    onboardingPages: List<OnBoardingPage>,
    pagerState: PagerState,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        onboardingPages.indices.forEach { index ->
            val active = index == pagerState.currentPage
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(6.dp)
                    .width(if (active) 20.dp else 6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (active) SinopeColors.Cyan else SinopeColors.Border),
            )
        }
    }
}