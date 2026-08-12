package com.jaydeep.trackingapp.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaydeep.trackingapp.core.di.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ThemeMode { LIGHT, DARK, SYSTEM }

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val tokenStore: TokenStore
) : ViewModel() {

    val themeMode = tokenStore.themeMode
        .map { 
            try { 
                ThemeMode.valueOf(it) 
            } catch (e: Exception) { 
                ThemeMode.SYSTEM 
            } 
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ThemeMode.SYSTEM
        )

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            tokenStore.saveThemeMode(mode.name)
        }
    }
}
