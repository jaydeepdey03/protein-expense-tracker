package com.jaydeep.trackingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.jaydeep.trackingapp.core.auth.AuthState
import com.jaydeep.trackingapp.core.auth.AuthViewModel
import com.jaydeep.trackingapp.core.ui.AppNavGraph
import com.jaydeep.trackingapp.ui.theme.ThemeViewModel
import com.jaydeep.trackingapp.ui.theme.TrackingAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by themeViewModel.themeMode.collectAsState()
            val authState by authViewModel.authState.collectAsState()

            TrackingAppTheme(themeMode = themeMode, dynamicColor = false) {
                when (val state = authState) {
                    is AuthState.Loading -> {
                        // Optional: Show a splash screen or loader
                    }
                    is AuthState.Authenticated -> {
                        AppNavGraph(startDestination = state.startDestination)
                    }
                    is AuthState.Unauthenticated -> {
                        AppNavGraph(startDestination = state.startDestination)
                    }
                }
            }
        }
    }
}
