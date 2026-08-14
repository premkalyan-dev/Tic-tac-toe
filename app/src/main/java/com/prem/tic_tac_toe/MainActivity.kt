package com.prem.tic_tac_toe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.prem.tic_tac_toe.ui.AppNavigation
import com.prem.tic_tac_toe.ui.GameViewModel
import com.prem.tic_tac_toe.ui.theme.TicTacToeTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels { GameViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // TODO: Initialize Firebase Crashlytics here:
        // Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)

        setContent {
            val appTheme by viewModel.appTheme.collectAsState()

            TicTacToeTheme(themeName = appTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel)
                }
            }
        }
    }
}
