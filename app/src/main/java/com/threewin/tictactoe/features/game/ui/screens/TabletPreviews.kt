package com.threewin.tictactoe.features.game.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.threewin.tictactoe.theme.TicTacToeTheme
import com.threewin.tictactoe.domain.model.*

@Composable
fun GameplayMock(gameState: GameState) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        StatusIndicator(gameState)
        Spacer(modifier = Modifier.height(32.dp))
        TicTacToeGrid(gameState = gameState, onCellClick = {})
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
            Text("Undo Move")
        }
    }
}

@Composable
fun SettingsContentMock() {
    Column(modifier = Modifier.padding(32.dp)) {
        Text("Settings", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Statistics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Wins: 10", style = MaterialTheme.typography.bodyLarge)
                Text("Losses: 5", style = MaterialTheme.typography.bodyLarge)
                Text("Draws: 2", style = MaterialTheme.typography.bodyLarge)
            }
        }
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Preferences", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Sound Effects", style = MaterialTheme.typography.bodyLarge)
                    Switch(checked = true, onCheckedChange = {})
                }
            }
        }
    }
}

// 7-inch Tablet Previews
@Preview(device = "spec:width=600dp,height=960dp,dpi=320", showBackground = true)
@Composable
fun Home_7inch() {
    TicTacToeTheme(themeMode = ThemeMode.LIGHT) {
        Surface(modifier = Modifier.fillMaxSize()) { StartScreen(onModeSelected = {}) }
    }
}

@Preview(device = "spec:width=600dp,height=960dp,dpi=320", showBackground = true)
@Composable
fun BoardSelection_7inch() {
    TicTacToeTheme(themeMode = ThemeMode.LIGHT) {
        Surface(modifier = Modifier.fillMaxSize()) { SelectBoardSizeScreen(onSizeSelected = {}, onBack = {}, onSettingsClick = {}) }
    }
}

@Preview(device = "spec:width=600dp,height=960dp,dpi=320", showBackground = true)
@Composable
fun Gameplay_7inch() {
    val mockState = GameState(
        boardSize = 3,
        board = listOf(Player.X, Player.O, null, null, Player.X, null, null, null, null),
        currentTurn = Player.O,
        moveHistory = listOf(0, 1, 4)
    )
    TicTacToeTheme(themeMode = ThemeMode.LIGHT) {
        Surface(modifier = Modifier.fillMaxSize()) { GameplayMock(mockState) }
    }
}

// 10-inch Tablet Previews
@Preview(device = "spec:width=800dp,height=1280dp,dpi=320", showBackground = true)
@Composable
fun Home_10inch() {
    TicTacToeTheme(themeMode = ThemeMode.LIGHT) {
        Surface(modifier = Modifier.fillMaxSize()) { StartScreen(onModeSelected = {}) }
    }
}

@Preview(device = "spec:width=800dp,height=1280dp,dpi=320", showBackground = true)
@Composable
fun BoardSelection_10inch() {
    TicTacToeTheme(themeMode = ThemeMode.LIGHT) {
        Surface(modifier = Modifier.fillMaxSize()) { SelectBoardSizeScreen(onSizeSelected = {}, onBack = {}, onSettingsClick = {}) }
    }
}

@Preview(device = "spec:width=800dp,height=1280dp,dpi=320", showBackground = true)
@Composable
fun Gameplay_10inch() {
    val mockState = GameState(
        boardSize = 5,
        board = List<Player?>(25) { null }.toMutableList().apply { 
            this[12] = Player.X
            this[0] = Player.O
            this[6] = Player.X
        }.toList(),
        currentTurn = Player.O,
        moveHistory = listOf(12, 0, 6)
    )
    TicTacToeTheme(themeMode = ThemeMode.LIGHT) {
        Surface(modifier = Modifier.fillMaxSize()) { GameplayMock(mockState) }
    }
}

@Preview(device = "spec:width=800dp,height=1280dp,dpi=320", showBackground = true)
@Composable
fun Settings_10inch() {
    TicTacToeTheme(themeMode = ThemeMode.LIGHT) {
        Surface(modifier = Modifier.fillMaxSize()) { SettingsContentMock() }
    }
}
