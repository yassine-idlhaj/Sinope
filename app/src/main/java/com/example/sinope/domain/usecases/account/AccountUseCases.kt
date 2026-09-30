package com.example.sinope.domain.usecases.account

data class AccountUseCases(
    val getAccounts: GetAccounts,
    val getAccount: GetAccount,
    val insertAccount: InsertAccount,
    val updateAccount: UpdateAccount,
    val deleteAccount: DeleteAccount,
    val deleteAllAccounts: DeleteAllAccounts,
    val parseQrCode: ParseQrCode,
    val generateTotpCode: GenerateTotpCode,
    val toggleFavorite: ToggleFavorite,
    val isAccountExists: IsAccountExists,
    val validateManualEntry: ValidateManualEntry
)
