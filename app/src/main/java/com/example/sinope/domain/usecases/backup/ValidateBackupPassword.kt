package com.example.sinope.domain.usecases.backup

enum class BackupPasswordValidation { Valid, TooShort, Mismatch }

class ValidateBackupPassword {

    operator fun invoke(password: CharArray, confirmation: CharArray): BackupPasswordValidation =
        when {
            password.size < MIN_LENGTH -> BackupPasswordValidation.TooShort
            !password.contentEquals(confirmation) -> BackupPasswordValidation.Mismatch
            else -> BackupPasswordValidation.Valid
        }

    companion object {
        const val MIN_LENGTH = 8
    }
}
