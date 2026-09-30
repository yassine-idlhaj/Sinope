package com.example.sinope.presentation.importaccounts.viewModel

import androidx.annotation.StringRes

/** Where the import flow currently is. The screen renders one of these. */
enum class ImportStage { PickingFile, Password, Reading, Preview, Importing, Finished, Error }

data class ImportState(
    val stage: ImportStage = ImportStage.PickingFile,
    val candidates: List<ImportCandidateUi> = emptyList(),
    val wrongPassword: Boolean = false,
    val importedCount: Int = 0,
    @param:StringRes val errorRes: Int? = null,
)

/** One row of the preview list. [index] points back to the parsed account in the ViewModel. */
data class ImportCandidateUi(
    val index: Int,
    val issuer: String,
    val accountName: String,
    val emoji: String,
    val isDuplicate: Boolean,
    val selected: Boolean,
)
