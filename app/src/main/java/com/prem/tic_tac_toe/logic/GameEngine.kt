package com.prem.tic_tac_toe.logic

/**
 * Represents the players in the game.
 */
enum class Player {
    X, O
}

/**
 * Represents the state of the Tic-Tac-Toe game.
 * @property board A list representing the NxN grid. Null means empty.
 * @property gridSize The size of the grid (3, 4, or 5).
 * @property currentTurn The player whose turn it is to move.
 * @property winner The winner of the game, or null if no winner yet.
 * @property isDraw True if the board is full and there is no winner.
 * @property winningLine The indices of the winning cells, for UI highlighting.
 */
data class GameState(
    val board: List<Player?> = List(9) { null },
    val gridSize: Int = 3,
    val currentTurn: Player = Player.X,
    val winner: Player? = null,
    val isDraw: Boolean = false,
    val winningLine: List<Int>? = null
) {
    companion object {
        fun create(gridSize: Int): GameState {
            return GameState(
                board = List(gridSize * gridSize) { null },
                gridSize = gridSize
            )
        }
    }
}

object GameEngine {

    /**
     * Result of checking the board for a winner.
     */
    data class WinResult(val winner: Player?, val winningLine: List<Int>?)

    /**
     * Single-pass check: returns both the winner and the winning line indices.
     */
    fun checkResult(board: List<Player?>, gridSize: Int): WinResult {
        val patterns = GameConstants.getWinningPatterns(gridSize)
        for (pattern in patterns) {
            val first = board[pattern[0]]
            if (first != null && pattern.all { board[it] == first }) {
                return WinResult(first, pattern)
            }
        }
        return WinResult(null, null)
    }

    /**
     * Checks all win patterns for the given grid size and returns the winner if any.
     */
    fun checkWinner(board: List<Player?>, gridSize: Int): Player? =
        checkResult(board, gridSize).winner

    /**
     * Returns the indices of the winning line if there is a winner.
     */
    fun checkWinningLine(board: List<Player?>, gridSize: Int): List<Int>? =
        checkResult(board, gridSize).winningLine

    /**
     * Returns true if all cells on the board are filled.
     */
    fun isBoardFull(board: List<Player?>): Boolean {
        return board.all { it != null }
    }

    /**
     * Validates and processes a move, returning the updated GameState.
     */
    fun makeMove(state: GameState, position: Int): GameState {
        val totalCells = state.gridSize * state.gridSize
        // Validation: position in range, cell empty, game not over
        if (position !in 0 until totalCells || state.board[position] != null ||
            state.winner != null || state.isDraw) {
            return state
        }

        // Place the mark
        val newBoard = state.board.toMutableList()
        newBoard[position] = state.currentTurn

        // Single-pass check for winner and winning line
        val (winner, winningLine) = checkResult(newBoard, state.gridSize)
        val isDraw = winner == null && isBoardFull(newBoard)

        // Switch turn
        val nextTurn = if (state.currentTurn == Player.X) Player.O else Player.X

        return state.copy(
            board = newBoard,
            currentTurn = nextTurn,
            winner = winner,
            isDraw = isDraw,
            winningLine = winningLine
        )
    }
}
