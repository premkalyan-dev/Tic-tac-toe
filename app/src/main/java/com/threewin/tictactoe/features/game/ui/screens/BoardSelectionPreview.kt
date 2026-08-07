package com.threewin.tictactoe.features.game.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.threewin.tictactoe.theme.TicTacToeTheme
import com.threewin.tictactoe.domain.model.ThemeMode

@Preview(showBackground = true)
@Composable
fun BoardSelectionPreview() {
    TicTacToeTheme(themeMode = ThemeMode.LIGHT) {
        SelectBoardSizeScreen(onSizeSelected = {}, onBack = {}, onSettingsClick = {})
    }
}
