package com.jaydeep.trackingapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.jaydeep.trackingapp.core.di.TokenStore
import com.jaydeep.trackingapp.core.ui.AppNavGraph
import com.jaydeep.trackingapp.core.ui.Screens
import com.jaydeep.trackingapp.ui.theme.TrackingAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenStore: TokenStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.e(
            "TrackingApp",
            "========== MAIN ACTIVITY STARTED =========="
        )

        lifecycleScope.launch {
            val startDestination = if (tokenStore.hasValidToken()) {
                Screens.Dashboard.route
            } else {
                Screens.Login.route
            }

            setContent {
                TrackingAppTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AppNavGraph(
                            startDestination = startDestination
                        )
                    }
                }
            }
        }
    }
}
