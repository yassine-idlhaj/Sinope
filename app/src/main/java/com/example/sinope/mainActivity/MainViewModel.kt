package com.example.sinope.mainActivity

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sinope.domain.usecases.app_entry.AppEntryUseCase
import com.example.sinope.domain.usecases.settings.security.biometricLock.BiometricLockUseCases
import com.example.sinope.domain.usecases.settings.security.screenshotProtection.ScreenshotProtectionUseCases
import com.example.sinope.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds


@HiltViewModel
class MainViewModel @Inject constructor(
    private val appEntryUseCase: AppEntryUseCase,
    private val biometricLockUseCases: BiometricLockUseCases,
    private val screenshotProtectionUseCases: ScreenshotProtectionUseCases
): ViewModel(){
    private val _splashCondition = mutableStateOf(true)
    val splashCondition: State<Boolean> = _splashCondition;

    private val _biometricLockEnabled = mutableStateOf<Boolean?>(null)
    val biometricLockEnabled: State<Boolean?> = _biometricLockEnabled;

    private val _screenshotProtectionEnabled = mutableStateOf<Boolean?>(null)
    val screenshotProtectionEnabled: State<Boolean?> = _screenshotProtectionEnabled;


    private val _startDestination = mutableStateOf(Route.AppStartNavigation.route)
    val startDestination: State<String> = _startDestination;

    init {
        readingAppEntry()
        readingBiometricLockValue()
        readScreenshotProtectionValue()
    }




    private fun readingBiometricLockValue(){
        biometricLockUseCases.readBiometricLock().onEach { enable ->
            _biometricLockEnabled.value = enable

        }.launchIn(viewModelScope)
    }


    private fun readingAppEntry(){
        appEntryUseCase.readAppEntry().onEach { shouldStartFromHomeScreen ->
            if(shouldStartFromHomeScreen){
                _startDestination.value = Route.MainNavigation.route
            }else{
                _startDestination.value = Route.AppStartNavigation.route
            }
            delay(200.milliseconds) //Without this delay, the onBoarding screen will show for a momentum.
            _splashCondition.value = false
        }.launchIn(viewModelScope)
    }

    private fun readScreenshotProtectionValue(){
        screenshotProtectionUseCases.readScreenshotProtection().onEach { enable ->
            _screenshotProtectionEnabled.value = enable
        }.launchIn(viewModelScope)
    }



}