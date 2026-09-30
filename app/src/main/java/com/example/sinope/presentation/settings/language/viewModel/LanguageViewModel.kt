package com.example.sinope.presentation.settings.language.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sinope.core.utils.applyLanguages
import com.example.sinope.domain.model.AppLanguage
import com.example.sinope.domain.usecases.settings.language.LanguageUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val languageUseCases: LanguageUseCases
): ViewModel() {

    private val _language = MutableStateFlow(AppLanguage.SYSTEM)
    val language = _language.asStateFlow()

    init {
        observeLanguage()
    }

    private fun observeLanguage(){
        viewModelScope.launch {
            languageUseCases.readLanguage().collect { language ->
                _language.value = language
            }
        }
    }

    fun selectLanguage(context: Context, language: AppLanguage){
        viewModelScope.launch {
            languageUseCases.saveLanguage(language)
            applyLanguages(context, language)
        }
    }


}