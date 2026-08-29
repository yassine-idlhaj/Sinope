package com.example.sinope

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point for Hilt: generates the root component every `@AndroidEntryPoint` in the
 * app resolves its dependencies from. Registered as `android:name` in the manifest.
 */
@HiltAndroidApp
class SinopeApplication : Application()
