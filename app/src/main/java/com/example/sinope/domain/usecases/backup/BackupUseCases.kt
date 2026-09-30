package com.example.sinope.domain.usecases.backup

data class BackupUseCases(
    val hasAccountsToExport: HasAccountsToExport,
    val validateBackupPassword: ValidateBackupPassword,
    val exportBackup: ExportBackup,
    val readBackupFile: ReadBackupFile,
    val findImportCandidates: FindImportCandidates,
    val importAccounts: ImportAccounts,
)
