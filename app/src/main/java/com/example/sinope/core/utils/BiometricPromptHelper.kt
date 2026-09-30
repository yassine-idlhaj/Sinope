package com.example.sinope.core.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.sinope.R


fun showBiometricPrompt(
    activity: FragmentActivity,
    onSuccess:() -> Unit,
    onError:(String) -> Unit,
){


    val biometricManager = BiometricManager.from(activity)
    when(
        biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
    ){
        BiometricManager.BIOMETRIC_SUCCESS -> {
            // Continue with biometric prompt
        }

        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            onError(activity.getString(R.string.biometric_error_no_hardware))
            return
        }

        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            onError(activity.getString(R.string.biometric_error_unavailable))
            return
        }

        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            onError(activity.getString(R.string.biometric_error_none_enrolled))
            return
        }

        else -> {
            onError(activity.getString(R.string.biometric_error_generic))
            return
        }
    }


    val executor = ContextCompat.getMainExecutor(activity)

    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                onSuccess()
            }

            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                when (errorCode) {
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                    BiometricPrompt.ERROR_USER_CANCELED -> {
                        onError(activity.getString(R.string.biometric_error_cancelled))
                    }

                    else -> {
                        onError(errString.toString())
                    }
                }
            }
            override fun onAuthenticationFailed() {

            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(activity.getString(R.string.biometric_prompt_title))
        .setSubtitle(activity.getString(R.string.biometric_prompt_subtitle))
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        .build()

    biometricPrompt.authenticate(promptInfo)

}

/** Walks the [ContextWrapper] chain for the hosting activity, which the prompt needs to attach to. */
tailrec fun Context.findFragmentActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findFragmentActivity()
    else -> null
}
