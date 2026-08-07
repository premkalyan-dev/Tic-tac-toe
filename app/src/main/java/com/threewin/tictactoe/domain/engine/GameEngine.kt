package com.threewin.tictactoe.domain.engine

import com.threewin.tictactoe.domain.model.GameState
import com.threewin.tictactoe.domain.model.Player

object GameEngine {

    fun checkWinner(board: List<Player?>, boardSize: Int): Player? {
        val patterns = GameConstants.getWinningPatterns(boardSize)
        for (pattern in patterns) {
            val playersInPattern = pattern.map { board[it] }
            val first = playersInPattern[0]
            if (first != null && playersInPattern.all { it == first }) {
                return first
            }
        }
        return null
    }

    fun checkWinningLine(board: List<Player?>, boardSize: Int): List<Int>? {
        val patterns = GameConstants.getWinningPatterns(boardSize)
        for (pattern in patterns) {
            val playersInPattern = pattern.map { board[it] }
            val first = playersInPattern[0]
            if (first != null && playersInPattern.all { it == first }) {
                return pattern
            }
        }
        return null
    }

    fun isBoardFull(board: List<Player?>): Boolean {
        return board.all { it != null }
    }

    fun makeMove(state: GameState, position: Int): GameState {
        if (position !in 0 until (state.boardSize * state.boardSize) || 
            state.board[position] != null || state.winner != null || state.isDraw) {
            return state
        }

        val newBoard = state.board.toMutableList()
        newBoard[position] = state.currentTurn

        val winner = checkWinner(newBoard, state.boardSize)
        val winningLine = if (winner != null) checkWinningLine(newBoard, state.boardSize) else null
        val isDraw = winner == null && isBoardFull(newBoard)

        val nextTurn = if (state.currentTurn == Player.X) Player.O else Player.X

        return state.copy(
            board = newBoard,
            currentTurn = nextTurn,
            winner = winner,
            isDraw = isDraw,
            winningLine = winningLine,
            moveHistory = state.moveHistory + position
        )
    }
}
