package com.example.sinope.core.camera

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean

/**
 * CameraX analyser that decodes QR codes on-device with ML Kit.
 *
 * The first successful decode wins: [onQrCode] fires once and every later frame is dropped until
 * [reset] is called, so a code lingering in front of the lens can't re-trigger the callback while
 * the success state is showing.
 */
class QrCodeAnalyzer(private val onQrCode: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build(),
    )

    private val delivered = AtomicBoolean(false)

    /** Re-arms the analyser after a result was consumed (e.g. the user tapped "Scan again"). */
    fun reset() = delivered.set(false)

    /** Releases the ML Kit detector; call once the camera use cases are unbound. */
    fun close() = scanner.close()

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null || delivered.get()) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val value = barcodes.firstNotNullOfOrNull { it.rawValue?.takeIf(String::isNotBlank) }
                if (value != null && delivered.compareAndSet(false, true)) onQrCode(value)
            }
            // Always close, otherwise the analyser starves after `imageQueueDepth` frames.
            .addOnCompleteListener { imageProxy.close() }
    }
}
