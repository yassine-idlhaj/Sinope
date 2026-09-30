package com.example.sinope.domain.model

/**
 * One account read from a backup, labelled with whether Sinope already has it.
 * Only a label: what to do with it is the user's choice in the import preview.
 */
data class ImportCandidate(
    val account: Account,
    val isDuplicate: Boolean,
)
