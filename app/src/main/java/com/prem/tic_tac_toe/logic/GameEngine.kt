package com.prem.tic_tac_toe.logic

/**
 * Represents the players in the game.
 */
enum class Player {
    X, O
}

/**
 * Represents the state of the Tic-Tac-Toe game.
 * @property board A 9-element list representing the 3x3 grid. Null means empty.
 * @property currentTurn The player whose turn it is to move.
 * @property winner The winner of the game, or null if no winner yet.
 * @property isDraw True if the board is full and there is no winner.
 * @property winningLine The indices of the winning cells, for UI highlighting.
 */
data class GameState(
    val board: List<Player?> = List(9) { null },
    val currentTurn: Player = Player.X,
    val winner: Player? = null,
    val isDraw: Boolean = false,
    val winningLine: List<Int>? = null
)

object GameEngine {

    /**
     * Checks all 8 win patterns and returns the winner if any.
     */
    fun checkWinner(board: List<Player?>): Player? {
        for (pattern in GameConstants.WINNING_PATTERNS) {
            val a = board[pattern[0]]
            val b = board[pattern[1]]
            val c = board[pattern[2]]
            if (a != null && a == b && a == c) {
                return a
            }
        }
        return null
    }

    /**
     * Returns the indices of the winning line if there is a winner.
     */
    fun checkWinningLine(board: List<Player?>): List<Int>? {
        for (pattern in GameConstants.WINNING_PATTERNS) {
            val a = board[pattern[0]]
            val b = board[pattern[1]]
            val c = board[pattern[2]]
            if (a != null && a == b && a == c) {
                return pattern
            }
        }
        return null
    }

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
        // Validation: position in range, cell empty, game not over
        if (position !in 0..8 || state.board[position] != null || state.winner != null || state.isDraw) {
            return state
        }

        // Place the mark
        val newBoard = state.board.toMutableList()
        newBoard[position] = state.currentTurn

        // Check for winner or draw
        val winner = checkWinner(newBoard)
        val winningLine = if (winner != null) checkWinningLine(newBoard) else null
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
