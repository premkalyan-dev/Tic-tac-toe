package com.threewin.tictactoe.domain.ai

import com.threewin.tictactoe.domain.engine.GameConstants
import com.threewin.tictactoe.domain.engine.GameEngine
import com.threewin.tictactoe.domain.model.DifficultyLevel
import com.threewin.tictactoe.domain.model.Player
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow

object AIPlayer {

    fun getAIMove(
        board: List<Player?>,
        boardSize: Int,
        aiPlayer: Player,
        difficulty: DifficultyLevel,
        coroutineContext: CoroutineContext
    ): Int {
        val opponent = if (aiPlayer == Player.X) Player.O else Player.X
        val freeCells = board.indices.filter { board[it] == null }
        if (freeCells.isEmpty()) return -1

        return when (difficulty) {
            DifficultyLevel.EASY -> {
                freeCells.shuffled().first()
            }
            DifficultyLevel.MEDIUM -> {
                if (Math.random() < 0.5) {
                    findWinningMove(board, boardSize, aiPlayer) ?: findWinningMove(board, boardSize, opponent) ?: freeCells.shuffled().first()
                } else {
                    freeCells.shuffled().first()
                }
            }
            DifficultyLevel.HARD -> {
                findWinningMove(board, boardSize, aiPlayer) ?: findWinningMove(board, boardSize, opponent) ?: 
                run {
                    val center = (boardSize * boardSize) / 2
                    if (board[center] == null) center else freeCells.shuffled().first()
                }
            }
            DifficultyLevel.UNBEATABLE -> {
                val maxDepth = when (boardSize) {
                    3 -> 9
                    4 -> 6
                    else -> 5
                }
                minimax(board, boardSize, aiPlayer, aiPlayer, 0, maxDepth, Int.MIN_VALUE, Int.MAX_VALUE, coroutineContext).index
            }
        }
    }

    private fun findWinningMove(board: List<Player?>, boardSize: Int, player: Player): Int? {
        val patterns = GameConstants.getWinningPatterns(boardSize)
        for (pattern in patterns) {
            val cells = pattern.map { board[it] }
            if (cells.count { it == player } == boardSize - 1 && cells.count { it == null } == 1) {
                return pattern[cells.indexOf(null)]
            }
        }
        return null
    }

    private const val WIN_SCORE = 1_000_000
    private const val HEURISTIC_MAX = 100_000

    private data class Move(val index: Int, val score: Int)

    private fun minimax(
        board: List<Player?>, 
        boardSize: Int, 
        currentPlayer: Player, 
        aiPlayer: Player, 
        depth: Int,
        maxDepth: Int,
        alpha: Int = Int.MIN_VALUE,
        beta: Int = Int.MAX_VALUE,
        coroutineContext: CoroutineContext
    ): Move {
        coroutineContext.ensureActive()
        val opponent = if (aiPlayer == Player.X) Player.O else Player.X
        val availableMoves = board.indices.filter { board[it] == null }

        val winner = GameEngine.checkWinner(board, boardSize)
        if (winner == aiPlayer) return Move(-1, WIN_SCORE - depth)
        if (winner == opponent) return Move(-1, depth - WIN_SCORE)
        if (availableMoves.isEmpty() || depth == maxDepth) {
            val score = evaluateBoard(board, boardSize, aiPlayer)
            val clampedScore = score.coerceIn(-HEURISTIC_MAX, HEURISTIC_MAX)
            return Move(-1, clampedScore)
        }

        var currentAlpha = alpha
        var currentBeta = beta
        
        if (currentPlayer == aiPlayer) {
            var bestScore = Int.MIN_VALUE
            var bestIndex = -1
            val rootTies = if (depth == 0) mutableListOf<Int>() else null
            
            val scoredMoves = availableMoves.map { index ->
                val newBoard = board.toMutableList()
                newBoard[index] = currentPlayer
                index to evaluateBoard(newBoard, boardSize, aiPlayer)
            }.sortedByDescending { it.second }
            
            for ((index, _) in scoredMoves) {
                val newBoard = board.toMutableList()
                newBoard[index] = currentPlayer
                
                val result = minimax(newBoard, boardSize, opponent, aiPlayer, depth + 1, maxDepth, currentAlpha, currentBeta, coroutineContext)
                
                if (result.score > bestScore) {
                    bestScore = result.score
                    bestIndex = index
                    rootTies?.clear()
                    rootTies?.add(index)
                } else if (result.score == bestScore) {
                    rootTies?.add(index)
                }
                currentAlpha = maxOf(currentAlpha, bestScore)
                if (currentBeta <= currentAlpha) break
            }
            return Move(rootTies?.random() ?: bestIndex, bestScore)
        } else {
            var bestScore = Int.MAX_VALUE
            var bestIndex = -1
            val rootTies = if (depth == 0) mutableListOf<Int>() else null

            val scoredMoves = availableMoves.map { index ->
                val newBoard = board.toMutableList()
                newBoard[index] = currentPlayer
                index to evaluateBoard(newBoard, boardSize, aiPlayer)
            }.sortedBy { it.second }
            
            for ((index, _) in scoredMoves) {
                val newBoard = board.toMutableList()
                newBoard[index] = currentPlayer
                
                val result = minimax(newBoard, boardSize, aiPlayer, aiPlayer, depth + 1, maxDepth, currentAlpha, currentBeta, coroutineContext)
                
                if (result.score < bestScore) {
                    bestScore = result.score
                    bestIndex = index
                    rootTies?.clear()
                    rootTies?.add(index)
                } else if (result.score == bestScore) {
                    rootTies?.add(index)
                }
                currentBeta = minOf(currentBeta, bestScore)
                if (currentBeta <= currentAlpha) break
            }
            return Move(rootTies?.random() ?: bestIndex, bestScore)
        }
    }
    
    private fun evaluateBoard(board: List<Player?>, boardSize: Int, aiPlayer: Player): Int {
        val opponent = if (aiPlayer == Player.X) Player.O else Player.X
        val patterns = GameConstants.getWinningPatterns(boardSize)
        var totalScore = 0
        
        for (pattern in patterns) {
            var aiCount = 0
            var oppCount = 0
            for (index in pattern) {
                when (board[index]) {
                    aiPlayer -> aiCount++
                    opponent -> oppCount++
                    else -> {}
                }
            }
            
            if (aiCount > 0 && oppCount > 0) continue
            
            if (aiCount > 0) {
                totalScore += 10.0.pow((aiCount - 1).toDouble()).toInt()
            } else if (oppCount > 0) {
                totalScore -= 10.0.pow((oppCount - 1).toDouble()).toInt()
            }
        }
        return totalScore
    }
}
