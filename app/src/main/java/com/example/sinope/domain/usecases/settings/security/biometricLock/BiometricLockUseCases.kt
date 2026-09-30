package com.example.sinope.domain.usecases.settings.security.biometricLock

data class BiometricLockUseCases(
    val saveBiometricLock: SaveBiometricLock,
    val readBiometricLock: ReadBiometricLock,
)
