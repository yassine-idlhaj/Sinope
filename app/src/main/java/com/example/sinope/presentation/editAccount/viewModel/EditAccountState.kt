package com.example.sinope.presentation.editAccount.viewModel

import androidx.compose.ui.graphics.Color
import com.example.sinope.core.utils.SinopeColors
import com.example.sinope.domain.model.Account
import com.example.sinope.R
import androidx.annotation.StringRes

/**
 * Form state for the Edit Account screen. [original] is the row as it currently sits in the
 * database — the editable fields start as a copy of it and [hasChanges] compares the two so the
 * Save button can tell "nothing to save" from a real edit.
 */
data class EditAccountState(
    val original: Account? = null,
    val issuer: String = "",
    val accountName: String = "",
    val emoji: String = "🔐",
    val color: Color = SinopeColors.Cyan,
    val favorite: Boolean = false,
    val digits: Int = 6,
    val period: Int = 30,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val showDeleteDialog: Boolean = false,
) {
    @get:StringRes
    val issuerError: Int? =
        if (!isLoading && issuer.isBlank()) R.string.issuer_cant_be_empty else null

    val hasChanges: Boolean = original?.let {
        it.issuer != issuer ||
            it.accountName != accountName ||
            it.emoji != emoji ||
            it.color != color.value.toLong() ||
            it.favorite != favorite ||
            it.digits != digits ||
            it.period != period
    } == true

    val canSave: Boolean = !isLoading && !isSaving && issuerError == null && hasChanges

    /** The edited form folded back onto [original], ready to hand to the update use case. */
    fun toAccount(): Account? = original?.copy(
        issuer = issuer.trim(),
        accountName = accountName.trim(),
        emoji = emoji,
        color = color.value.toLong(),
        favorite = favorite,
        digits = digits,
        period = period,
    )
}
