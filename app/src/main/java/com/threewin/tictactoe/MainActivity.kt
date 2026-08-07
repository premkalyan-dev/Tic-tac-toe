package com.threewin.tictactoe

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
import com.threewin.tictactoe.features.game.viewmodel.GameViewModel
import com.threewin.tictactoe.navigation.TicTacToeScreen
import com.threewin.tictactoe.theme.TicTacToeTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels { GameViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            
            TicTacToeTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TicTacToeScreen(viewModel)
                }
            }
        }
    }
}
