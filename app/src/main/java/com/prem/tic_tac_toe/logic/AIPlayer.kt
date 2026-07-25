package com.prem.tic_tac_toe.logic

object AIPlayer {

    /**
     * Returns the best move for the AI player based on a simple heuristic:
     * 1. Win if possible
     * 2. Block opponent from winning
     * 3. Take center
     * 4. Take corner
     * 5. Take any free cell
     */
    fun getAIMove(board: List<Player?>, aiPlayer: Player): Int {
        val opponent = if (aiPlayer == Player.X) Player.O else Player.X

        // 1. Try to win
        findWinningMove(board, aiPlayer)?.let { return it }

        // 2. Block opponent
        findWinningMove(board, opponent)?.let { return it }

        // 3. Take center
        if (board[4] == null) return 4

        // 4. Take corner
        val corners = listOf(0, 2, 6, 8).shuffled()
        for (corner in corners) {
            if (board[corner] == null) return corner
        }

        // 5. Take any free cell
        val freeCells = board.indices.filter { board[it] == null }.shuffled()
        return if (freeCells.isNotEmpty()) freeCells.first() else -1
    }

    private fun findWinningMove(board: List<Player?>, player: Player): Int? {
        for (pattern in GameConstants.WINNING_PATTERNS) {
            val cells = pattern.map { board[it] }
            // If two cells are marked by the player and one is empty, that's a winning move
            if (cells.count { it == player } == 2 && cells.count { it == null } == 1) {
                return pattern[cells.indexOf(null)]
            }
        }
        return null
    }
}
