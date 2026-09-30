package com.example.sinope.domain.usecases.settings.security.screenshotProtection

data class ScreenshotProtectionUseCases(
    val saveScreenshotProtection: SaveScreenshotProtection,
    val readScreenshotProtection: ReadScreenshotProtection,
) {
}