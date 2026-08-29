package com.example.sinope.presentation.addaccount.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FlashlightOff
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.NoPhotography
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.sinope.core.common.SinopeButton
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.R
import androidx.compose.ui.res.stringResource

/** Fraction of the card's shorter side taken up by the square scan window. */
private const val WINDOW_FRACTION = 0.70f

/**
 * "Scan QR Code" tab: a live CameraX viewfinder that decodes `otpauth://` QR codes on device.
 *
 * Handles the whole permission dance itself — first-run rationale, the system prompt, and the
 * "denied for good, send them to Settings" fallback — and freezes on the first successful decode
 * until the user either commits the result or asks to scan again.
 *
 * @param onQrScanned receives the raw decoded payload when the user confirms a scan.
 */
@Composable
fun ScanQrTab(
    modifier: Modifier = Modifier,
    onQrScanned: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val isPreview = LocalInspectionMode.current

    var granted by remember { mutableStateOf(isPreview || context.hasCameraPermission()) }
    var everAsked by rememberSaveable { mutableStateOf(false) }
    var scanned by remember { mutableStateOf<String?>(null) }
    var torchOn by remember { mutableStateOf(false) }
    var hasFlash by remember { mutableStateOf(false) }
    var cameraError by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { result ->
        granted = result
        everAsked = true
    }

    // The user may grant the permission in system Settings and come back; re-check on resume.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        granted = isPreview || context.hasCameraPermission()
    }

    // The torch is a camera-level toggle: drop it when the preview goes away so the next
    // binding doesn't come back with the flash silently on.
    LaunchedEffect(granted, scanned) {
        if (!granted || scanned != null) torchOn = false
    }

    LaunchedEffect(scanned) {
        if (scanned != null) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val cameraStartFailed = stringResource(R.string.camera_start_failed)

    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(SinopeColors.SurfaceAlt)
                .border(1.dp, SinopeColors.Border, RoundedCornerShape(24.dp)),
        ) {
            when {
                cameraError != null -> ViewfinderMessage(
                    icon = Icons.Outlined.NoPhotography,
                    title = stringResource(R.string.camera_unavailable),
                    body = cameraError.orEmpty(),
                )

                !granted -> PermissionRequest(
                    everAsked = everAsked,
                    onRequest = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    onOpenSettings = { context.openAppSettings() },
                )

                // Compose previews and Robolectric have no camera; keep the framing chrome so the
                // layout is still reviewable without a device.
                isPreview -> ScannerChrome(active = true)

                else -> {
                    QrCameraPreview(
                        torchEnabled = torchOn,
                        paused = scanned != null,
                        onQrCode = { code -> scanned = code },
                        onCameraReady = { hasFlash = it },
                        onError = { cameraError = it.message ?: cameraStartFailed },
                        modifier = Modifier.fillMaxSize(),
                    )
                    ScannerChrome(active = scanned == null)
                }
            }

            FadingOverlay(
                visible = granted && cameraError == null && scanned == null && hasFlash,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
            ) {
                TorchButton(on = torchOn, onToggle = { torchOn = !torchOn })
            }

            FadingOverlay(visible = scanned != null, modifier = Modifier.fillMaxSize()) {
                ScanSuccessOverlay(payload = scanned.orEmpty())
            }
        }

        Spacer(Modifier.height(16.dp))

        val code = scanned
        if (code == null) {
            HintRow(
                text = when {
                    cameraError != null -> stringResource(R.string.scan_hint_camera_error)
                    granted -> stringResource(R.string.scan_hint_point_camera)
                    else -> stringResource(R.string.scan_hint_permission)
                },
                showPulse = granted && cameraError == null,
            )
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SinopeButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.scan_again),
                    icon = Icons.Outlined.Refresh,
                    onClick = { scanned = null },
                    backgroundColor = SolidColor(SinopeColors.InputBg),
                    borderWidth = 1.dp,
                    borderColor = SinopeColors.Border,
                    textColor = SinopeColors.TextSecondary,
                    tintColor = SinopeColors.TextSecondary,
                    iconSize = 14.dp,
                    iconSpacing = 6.dp,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    contentPadVer = 13.dp,
                )
                SinopeButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.use_this_code),
                    onClick = { onQrScanned(code) },
                    backgroundColor = Brush.linearGradient(
                        listOf(SinopeColors.Cyan, SinopeColors.VioletDeep),
                    ),
                    textColor = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    contentPadVer = 13.dp,
                )
            }
        }
    }
}

/* ----------------------------------------------------------------------------------------------
 * Viewfinder chrome
 * -------------------------------------------------------------------------------------------- */

/**
 * The animated overlay drawn on top of the camera feed: a scrim with a square cut-out, gradient
 * corner brackets and a sweeping scan line. Purely decorative — [active] freezes it once a code
 * has been captured.
 */
@Composable
private fun ScannerChrome(active: Boolean) {
    val transition = rememberInfiniteTransition(label = "scanner")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "sweep",
    )
    val cornerPulse by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "cornerPulse",
    )
    val laserAlpha by animateFloatAsState(if (active) 1f else 0f, label = "laserAlpha")

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            // BlendMode.Clear needs its own layer, otherwise it would punch through the whole card.
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
    ) {
        val window = size.minDimension * WINDOW_FRACTION
        val left = (size.width - window) / 2f
        val top = (size.height - window) / 2f
        val radius = 22.dp.toPx()

        drawRect(SinopeColors.Background.copy(alpha = 0.62f))
        drawRoundRect(
            color = Color.Black,
            topLeft = Offset(left, top),
            size = Size(window, window),
            cornerRadius = CornerRadius(radius, radius),
            blendMode = BlendMode.Clear,
        )

        val brush = Brush.linearGradient(
            colors = listOf(SinopeColors.Cyan, SinopeColors.Violet, SinopeColors.Pink),
            start = Offset(left, top),
            end = Offset(left + window, top + window),
        )
        drawPath(
            path = cornerBrackets(left, top, window, radius, arm = window * 0.16f),
            brush = brush,
            alpha = if (active) cornerPulse else 0.35f,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )

        if (laserAlpha > 0f) {
            val inset = radius * 0.4f
            val laserY = top + inset + (window - inset * 2) * sweep
            val glow = 26.dp.toPx()
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        SinopeColors.Cyan.copy(alpha = 0.30f),
                        Color.Transparent,
                    ),
                    startY = laserY - glow,
                    endY = laserY + glow,
                ),
                topLeft = Offset(left, laserY - glow),
                size = Size(window, glow * 2),
                alpha = laserAlpha,
            )
            drawLine(
                brush = brush,
                start = Offset(left + inset, laserY),
                end = Offset(left + window - inset, laserY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
                alpha = laserAlpha,
            )
        }
    }
}

/** Four L-shaped brackets hugging the rounded corners of the scan window. */
private fun cornerBrackets(
    left: Float,
    top: Float,
    window: Float,
    radius: Float,
    arm: Float,
): Path {
    val right = left + window
    val bottom = top + window
    return Path().apply {
        // Top-left
        moveTo(left, top + radius + arm)
        lineTo(left, top + radius)
        quadraticTo(left, top, left + radius, top)
        lineTo(left + radius + arm, top)
        // Top-right
        moveTo(right - radius - arm, top)
        lineTo(right - radius, top)
        quadraticTo(right, top, right, top + radius)
        lineTo(right, top + radius + arm)
        // Bottom-right
        moveTo(right, bottom - radius - arm)
        lineTo(right, bottom - radius)
        quadraticTo(right, bottom, right - radius, bottom)
        lineTo(right - radius - arm, bottom)
        // Bottom-left
        moveTo(left + radius + arm, bottom)
        lineTo(left + radius, bottom)
        quadraticTo(left, bottom, left, bottom - radius)
        lineTo(left, bottom - radius - arm)
    }
}

/* ----------------------------------------------------------------------------------------------
 * States shown inside the card
 * -------------------------------------------------------------------------------------------- */

/** Success state: dims the frozen feed and confirms what was captured. */
@Composable
private fun ScanSuccessOverlay(payload: String) {
    // Start below full size and settle on the first frame so the badge pops in rather than
    // appearing fully formed.
    var settled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { settled = true }
    val scale by animateFloatAsState(
        targetValue = if (settled) 1f else 0.6f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "checkScale",
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SinopeColors.Background.copy(alpha = 0.88f))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .size(72.dp)
                .clip(CircleShape)
                .background(SinopeColors.Green.copy(alpha = 0.12f))
                .border(2.dp, SinopeColors.Green, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Rounded.Check,
                contentDescription = null,
                tint = SinopeColors.Green,
                modifier = Modifier.size(38.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.qr_code_captured),
            color = SinopeColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = payload.toAccountLabel(),
            color = SinopeColors.TextSecondary,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Pre-permission state: explains why the camera is needed before the system prompt appears. */
@Composable
private fun PermissionRequest(
    everAsked: Boolean,
    onRequest: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            SinopeColors.Cyan.copy(alpha = 0.18f),
                            SinopeColors.Pink.copy(alpha = 0.18f),
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.QrCodeScanner,
                contentDescription = null,
                tint = SinopeColors.Cyan,
                modifier = Modifier.size(30.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.camera_access_needed),
            color = SinopeColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(
                if (everAsked) {
                    R.string.camera_access_denied
                } else {
                    R.string.camera_access_rationale
                },
            ),
            color = SinopeColors.TextSecondary,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(18.dp))
        SinopeButton(
            modifier = Modifier.width(180.dp),
            text = stringResource(if (everAsked) R.string.open_settings else R.string.enable_camera),
            onClick = if (everAsked) onOpenSettings else onRequest,
            backgroundColor = Brush.linearGradient(
                listOf(SinopeColors.Cyan, SinopeColors.VioletDeep),
            ),
            textColor = Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            contentPadVer = 13.dp,
        )
    }
}

/** Terminal error state (no camera, hardware busy, binding failed). */
@Composable
private fun ViewfinderMessage(icon: ImageVector, title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = SinopeColors.TextMuted,
            modifier = Modifier.size(40.dp),
        )
        Spacer(Modifier.height(14.dp))
        Text(title, color = SinopeColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(
            body,
            color = SinopeColors.TextSecondary,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/* ----------------------------------------------------------------------------------------------
 * Small pieces
 * -------------------------------------------------------------------------------------------- */

/**
 * Cross-fades [content] in and out.
 *
 * Extracted rather than called inline because the viewfinder `Box` sits inside a `Column`, and the
 * `ColumnScope` overload of `AnimatedVisibility` would win overload resolution there.
 */
@Composable
private fun FadingOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        content()
    }
}

@Composable
private fun TorchButton(on: Boolean, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(SinopeColors.Background.copy(alpha = 0.55f))
            .border(
                1.dp,
                if (on) SinopeColors.Amber else SinopeColors.Border,
                CircleShape,
            )
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (on) Icons.Outlined.FlashlightOn else Icons.Outlined.FlashlightOff,
            contentDescription = stringResource(
                if (on) R.string.turn_off_flashlight else R.string.turn_on_flashlight,
            ),
            tint = if (on) SinopeColors.Amber else SinopeColors.TextSecondary,
            modifier = Modifier.size(18.dp),
        )
    }
}

/** Caption under the viewfinder, with a breathing cyan dot while the scanner is live. */
@Composable
private fun HintRow(text: String, showPulse: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (showPulse) {
            val transition = rememberInfiniteTransition(label = "hint")
            val pulse by transition.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "hintPulse",
            )
            Box(
                modifier = Modifier
                    .alpha(pulse)
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(SinopeColors.Cyan),
            )
        }
        Text(
            text = text,
            color = SinopeColors.TextMuted,
            fontSize = 11.sp,
        )
    }
}

/* ----------------------------------------------------------------------------------------------
 * Helpers
 * -------------------------------------------------------------------------------------------- */

private fun android.content.Context.hasCameraPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED

private fun android.content.Context.openAppSettings() {
    startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}

/**
 * Turns an `otpauth://totp/Issuer:user@host?issuer=Issuer&…` payload into a short human label.
 * Anything that isn't an otpauth URI is shown truncated, so an unrelated QR code is still
 * recognisable as "not what you wanted".
 */
private fun String.toAccountLabel(): String {
    if (!startsWith("otpauth://", ignoreCase = true)) return take(48)
    val uri = runCatching { toUri() }.getOrNull() ?: return take(48)
    val label = uri.path?.trimStart('/').orEmpty()
    val issuer = uri.getQueryParameter("issuer")?.takeIf { it.isNotBlank() }
        ?: label.substringBefore(':', missingDelimiterValue = "").takeIf { it.isNotBlank() }
    val account = label.substringAfter(':', missingDelimiterValue = label)
    return listOfNotNull(issuer, account.takeIf { it.isNotBlank() })
        .distinct()
        .joinToString(" · ")
        .ifBlank { take(48) }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0F, widthDp = 320)
@Composable
private fun ScanQrTabPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SinopeButton(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.scan_again),
            icon = Icons.Outlined.Refresh,
            onClick = { },
            backgroundColor = SolidColor(SinopeColors.InputBg),
            borderWidth = 1.dp,
            borderColor = SinopeColors.Border,
            textColor = SinopeColors.TextSecondary,
            tintColor = SinopeColors.TextSecondary,
            iconSize = 14.dp,
            iconSpacing = 6.dp,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            contentPadVer = 13.dp,
        )
        SinopeButton(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.use_this_code),
            onClick = { },
            backgroundColor = Brush.linearGradient(
                listOf(SinopeColors.Cyan, SinopeColors.VioletDeep),
            ),
            textColor = Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            contentPadVer = 13.dp,
        )
    }
}
