package com.prem.tic_tac_toe.logic

object AIPlayer {

    /**
     * Returns the best move for the AI player on any NxN grid.
     * Strategy:
     * 1. Win if possible
     * 2. Block opponent from winning
     * 3. Take center cell(s)
     * 4. Take corner
     * 5. Take any free cell
     *
     * @param board The current board state
     * @param aiPlayer The AI's mark (X or O)
     * @param gridSize The grid size (3, 4, or 5)
     */
    fun getAIMove(board: List<Player?>, aiPlayer: Player, gridSize: Int): Int {
        val opponent = if (aiPlayer == Player.X) Player.O else Player.X
        val patterns = GameConstants.getWinningPatterns(gridSize)

        // 1. Try to win
        findWinningMove(board, aiPlayer, patterns)?.let { return it }

        // 2. Block opponent
        findWinningMove(board, opponent, patterns)?.let { return it }

        // 3. Take center cell(s)
        val centers = GameConstants.getCenterCells(gridSize).shuffled()
        for (center in centers) {
            if (board[center] == null) return center
        }

        // 4. Take corner
        val corners = GameConstants.getCornerCells(gridSize).shuffled()
        for (corner in corners) {
            if (board[corner] == null) return corner
        }

        // 5. Try to build towards a winning line (strategic positioning)
        findStrategicMove(board, aiPlayer, patterns)?.let { return it }

        // 6. Take any free cell
        val freeCells = board.indices.filter { board[it] == null }.shuffled()
        return if (freeCells.isNotEmpty()) freeCells.first() else -1
    }

    /**
     * Finds a move that completes a winning line: (N-1) marks + 1 empty cell.
     */
    private fun findWinningMove(
        board: List<Player?>,
        player: Player,
        patterns: List<List<Int>>
    ): Int? {
        for (pattern in patterns) {
            val cells = pattern.map { board[it] }
            val playerCount = cells.count { it == player }
            val emptyCount = cells.count { it == null }
            // If all cells minus one are filled by the player and one is empty
            if (playerCount == pattern.size - 1 && emptyCount == 1) {
                return pattern[cells.indexOf(null)]
            }
        }
        return null
    }

    /**
     * Finds a strategic position: a line where the AI already has marks and
     * the rest are empty (no opponent blocking).
     * Prioritizes lines with more existing marks.
     */
    private fun findStrategicMove(
        board: List<Player?>,
        player: Player,
        patterns: List<List<Int>>
    ): Int? {
        // Sort patterns by how many marks the AI already has (descending)
        val scoredMoves = mutableListOf<Pair<Int, Int>>() // (position, score)

        for (pattern in patterns) {
            val cells = pattern.map { board[it] }
            val playerCount = cells.count { it == player }
            val opponentCount = cells.count { it != null && it != player }

            // Only consider lines that are not blocked by the opponent
            if (opponentCount == 0 && playerCount > 0) {
                for (i in pattern.indices) {
                    if (board[pattern[i]] == null) {
                        scoredMoves.add(pattern[i] to playerCount)
                    }
                }
            }
        }

        return scoredMoves.maxByOrNull { it.second }?.first
    }
}
