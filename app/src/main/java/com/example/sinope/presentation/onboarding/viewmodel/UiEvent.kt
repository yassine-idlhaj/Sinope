package com.example.sinope.presentation.onboarding.viewmodel

sealed interface UiEvent {
    data object NavigateToHome: UiEvent;
}