package com.example.sinope.mainActivity

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sinope.domain.usecases.app_entry.AppEntryUseCase
import com.example.sinope.presentation.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val appEntryUseCase: AppEntryUseCase
): ViewModel(){
    private val _splashCondition = mutableStateOf(true)
    val splashCondition: State<Boolean> = _splashCondition;


    private val _startDestination = mutableStateOf(Route.AppStartNavigation.route)
    val startDestination: State<String> = _startDestination;

    init {
        appEntryUseCase.readAppEntry().onEach { shouldStartFromHomeScreen ->
            Log.d("TEST",shouldStartFromHomeScreen.toString())
            if(shouldStartFromHomeScreen){
                _startDestination.value = Route.MainNavigation.route
            }else{
                _startDestination.value = Route.AppStartNavigation.route
            }
            delay(200) //Without this delay, the onBoarding screen will show for a momentum.
            _splashCondition.value = false
        }.launchIn(viewModelScope)
    }



}