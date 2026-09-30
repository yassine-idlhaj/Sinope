package com.example.sinope.mainActivity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.os.ConfigurationCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.text.layoutDirection
import com.example.sinope.core.utils.showBiometricPrompt
import com.example.sinope.core.utils.withPersistedAppLocale
import com.example.sinope.presentation.lock.LockedScreen
import com.example.sinope.presentation.navigation.NavGraph
import com.example.sinope.ui.theme.SinopeTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private var isUnlocked by mutableStateOf(false)

    /** Why the last unlock attempt failed, shown on [LockedScreen]. Null while none has. */
    private var lockError by mutableStateOf<String?>(null)

    // Below API 33 there is no per-app language support in the platform, so the saved locale has
    // to be folded into the activity's context here — before onCreate inflates anything.
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.withPersistedAppLocale())
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().apply {
            setKeepOnScreenCondition(condition = { viewModel.splashCondition.value })
        }
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContent {
            val biometricLockEnabled = viewModel.biometricLockEnabled.value
            val screenshotProtection = viewModel.screenshotProtectionEnabled.value

            LaunchedEffect(biometricLockEnabled) {
                when (biometricLockEnabled) {
                    // Still reading the preference — keep the splash, decide nothing yet.
                    null -> Unit

                    false -> {
                        isUnlocked = true
                        lockError = null
                    }

                    // Guarded on isUnlocked so turning the setting on from inside an open session
                    // doesn't immediately lock the user out of the screen they're standing on.
                    true -> if (!isUnlocked) authenticate()
                }
            }

            LaunchedEffect(screenshotProtection) {
                if(screenshotProtection == true){
                    window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }else{
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            // The window's own configuration still carries the device's layout direction — an
            // overridden Context changes resource lookup, not the window. Derive the direction
            // from the active locale so Arabic lays out right-to-left.

            val locale = ConfigurationCompat.getLocales(LocalConfiguration.current)[0]
            val layoutDirection = if (
                locale != null &&
                locale.layoutDirection == View.LAYOUT_DIRECTION_RTL
            ) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                SinopeTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            when {
                                isUnlocked -> NavGraph(
                                    startDestination = viewModel.startDestination.value
                                )

                                // A cancelled or failed prompt used to draw nothing at all,
                                // leaving no way back in short of force-stopping the app.
                                biometricLockEnabled == true -> LockedScreen(
                                    onUnlock = ::authenticate,
                                    error = lockError,
                                )
                            }

                        }
                    }
                }
            }
        }

    }

    /** Runs the biometric prompt. Retried from [LockedScreen] whenever an attempt doesn't land. */
    private fun authenticate() {
        lockError = null

        showBiometricPrompt(
            activity = this,
            onSuccess = {
                isUnlocked = true
                lockError = null
            },
            onError = { message ->
                isUnlocked = false
                lockError = message
            },
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SinopeTheme {
        Greeting("Android")
    }
}