package com.threewin.tictactoe.features.game.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.threewin.tictactoe.theme.TicTacToeTheme
import com.threewin.tictactoe.domain.model.*

@Preview(showBackground = true)
@Composable
fun TicTacToeGridPreview() {
    val mockBoard = listOf(
        Player.X, Player.O, null,
        null, Player.X, null,
        null, null, Player.O
    )
    val mockState = GameState(
        boardSize = 3,
        board = mockBoard,
        currentTurn = Player.X
    )
    TicTacToeTheme(themeMode = ThemeMode.LIGHT) {
        TicTacToeGrid(gameState = mockState, onCellClick = {})
    }
}
