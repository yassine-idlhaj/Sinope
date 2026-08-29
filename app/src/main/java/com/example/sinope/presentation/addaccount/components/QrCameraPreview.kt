package com.example.sinope.presentation.addaccount.components

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.sinope.core.camera.QrCodeAnalyzer
import java.util.concurrent.Executors

/**
 * Live back-camera feed bound to the composition's lifecycle, with every frame handed to
 * [QrCodeAnalyzer].
 *
 * @param torchEnabled turns the flash on/off without rebinding the camera.
 * @param paused when it flips back to `false` the analyser is re-armed, so the caller can freeze
 *   scanning while a result is on screen and resume with a single state flag.
 * @param onQrCode fired on the main thread the first time a QR code is decoded.
 * @param onCameraReady reports whether the bound camera actually has a flash unit, so the caller
 *   can hide a torch button that would do nothing.
 * @param onError surfaces a binding failure (no camera, camera in use by another app, …).
 */
@Composable
fun QrCameraPreview(
    torchEnabled: Boolean,
    paused: Boolean,
    onQrCode: (String) -> Unit,
    modifier: Modifier = Modifier,
    onCameraReady: (hasFlash: Boolean) -> Unit = {},
    onError: (Throwable) -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnQrCode by rememberUpdatedState(onQrCode)
    val currentOnCameraReady by rememberUpdatedState(onCameraReady)
    val currentOnError by rememberUpdatedState(onError)

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            // COMPATIBLE renders into a TextureView, which — unlike the SurfaceView of
            // PERFORMANCE — honours the rounded-corner clip of the viewfinder card.
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    val analyzer = remember { QrCodeAnalyzer { code -> currentOnQrCode(code) } }
    var camera by remember { mutableStateOf<Camera?>(null) }

    DisposableEffect(lifecycleOwner, previewView) {
        val analysisExecutor = Executors.newSingleThreadExecutor()
        val providerFuture = ProcessCameraProvider.getInstance(context)

        providerFuture.addListener({
            runCatching {
                val provider = providerFuture.get()
                val preview = Preview.Builder().build().apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }
                val analysis = ImageAnalysis.Builder()
                    // Only the newest frame matters; older ones are stale by the time we decode.
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .apply { setAnalyzer(analysisExecutor, analyzer) }

                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis,
                )

            }.onSuccess {
                camera = it
                currentOnCameraReady(it.cameraInfo.hasFlashUnit())
            }.onFailure(currentOnError)
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            camera = null
            runCatching { if (providerFuture.isDone) providerFuture.get().unbindAll() }
            analysisExecutor.shutdown()
            analyzer.close()
        }
    }

    LaunchedEffect(torchEnabled, camera) {
        camera?.cameraControl?.enableTorch(torchEnabled)
    }

    // Re-arm the one-shot analyser whenever the caller leaves the frozen result state.
    LaunchedEffect(paused) {
        if (!paused) analyzer.reset()
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}
