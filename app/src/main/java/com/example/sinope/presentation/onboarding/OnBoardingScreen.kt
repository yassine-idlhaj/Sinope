package com.example.sinope.presentation.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sinope.R
import com.example.sinope.core.common.SinopeButton
import com.example.sinope.core.enums.OnboardingArt
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.presentation.onboarding.components.PagerIndicator
import com.example.sinope.presentation.onboarding.illustrations.drawPhoneShieldQr
import com.example.sinope.presentation.onboarding.illustrations.drawScannedQrCode
import com.example.sinope.presentation.onboarding.illustrations.drawShieldLock
import com.example.sinope.presentation.onboarding.viewmodel.OnBoardingEvent
import kotlinx.coroutines.launch


private val onboardingPages = listOf(
    OnBoardingPage(
        title = R.string.onboarding_welcome_title,
        body = R.string.onboarding_welcome_body,
        art = OnboardingArt.PhoneShieldQr,
        accent = SinopeColors.Cyan,
    ),
    OnBoardingPage(
        title = R.string.onboarding_control_title,
        bullets = listOf(
            R.string.onboarding_bullet_stored_locally,
            R.string.onboarding_bullet_no_account,
            R.string.onboarding_bullet_offline,
            R.string.onboarding_bullet_backups,
        ),
        art = OnboardingArt.ShieldLock,
        accent = SinopeColors.Violet,
    ),
    OnBoardingPage(
        title = R.string.onboarding_first_account_title,
        body = R.string.onboarding_first_account_body,
        art = OnboardingArt.QrCode,
        accent = SinopeColors.Pink,
    ),
)

/**
 * First-run onboarding: three swipeable pages sharing the Sinope's dark-neon look, each with a
 * static line-art illustration of what the page describes. UI only — [onFinish] fires from both
 * the "Skip" action and the final "Get Started" button.
 */
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {},
    onEvent: (OnBoardingEvent) -> Unit = {},
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == onboardingPages.lastIndex
    val showBack = pagerState.currentPage > 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SinopeColors.Background)
            .systemBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // "Skip" is offered on the welcome page only; later pages carry Back/Next instead.
            if (pagerState.currentPage == 0) {
                Text(
                    text = stringResource(R.string.skip),
                    color = SinopeColors.TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onFinish),
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { page ->
            OnboardingPageContent(onboardingPages[page])
        }

        PagerIndicator(onboardingPages = onboardingPages, pagerState = pagerState)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (showBack) {
                SinopeButton(
                    text = stringResource(R.string.back),
                    textColor = SinopeColors.TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    borderColor = SinopeColors.Border,
                    backgroundColor = SolidColor(Color.Transparent),
                    borderWidth = 1.dp,
                    roundedCornerSize = 18.dp,
                    padHor = 13.dp,
                    padVer = 12.dp,
                    contentPadVer = 13.dp,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
            }

            SinopeButton(
                text = stringResource(if (isLast) R.string.get_started else R.string.next),
                fontSize = 14.sp,
                padHor = 13.dp,
                padVer = 12.dp,
                contentPadVer = 13.dp,
                fontWeight = FontWeight.ExtraBold,
                onClick = {
                    scope.launch {
                        if (isLast) {
                            onEvent(OnBoardingEvent.SaveAppEntry)
                        } else scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnBoardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        PageIllustration(
            art = page.art,
            accent = page.accent,
            modifier = Modifier.size(200.dp),
        )
        Spacer(Modifier.height(40.dp))
        Text(
            text = stringResource(page.title),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                brush = SinopeColors.BrandGradient,
            ),
            textAlign = TextAlign.Center,
        )

        if (page.body != null) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(page.body),
                color = SinopeColors.TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
            )
        }

        if (page.bullets.isNotEmpty()) {
            Spacer(Modifier.height(22.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                page.bullets.forEach { bullet ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = SinopeColors.Green,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = stringResource(bullet),
                            color = SinopeColors.TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Static line-art illustration for a page, drawn in the vault's neon-on-dark style: a soft accent
 * halo behind a stroked figure that matches the page's message.
 */
@Composable
private fun PageIllustration(art: OnboardingArt, accent: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val s = size.minDimension
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accent.copy(alpha = 0.15f), Color.Transparent),
                center = center,
                radius = s * 0.52f,
            ),
            radius = s * 0.52f,
            center = center,
        )
        when (art) {
            OnboardingArt.PhoneShieldQr -> drawPhoneShieldQr(s, accent)
            OnboardingArt.ShieldLock -> drawShieldLock(s, accent)
            OnboardingArt.QrCode -> drawScannedQrCode(s, accent)
        }
    }
}


@Preview(
    name = "Onboarding",
    showBackground = true,
    backgroundColor = 0xFF0A0A0F,
    widthDp = 300,
    heightDp = 620
)
@Composable
private fun OnboardingPreview() {
    OnboardingScreen()
}