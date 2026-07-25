package com.prem.tic_tac_toe.logic

object GameConstants {
    /**
     * The 8 winning patterns in a 3x3 Tic Tac Toe grid.
     * Each sub-list contains the indices of a row, column, or diagonal.
     */
    val WINNING_PATTERNS = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Columns
        listOf(0, 4, 8), listOf(2, 4, 6)             // Diagonals
    )
}
