package com.prem.tic_tac_toe.logic

object GameConstants {

    /**
     * Generates all winning patterns for an NxN grid where N-in-a-row wins.
     * Includes rows, columns, and both diagonals.
     *
     * @param gridSize The size of the grid (3, 4, or 5)
     * @return List of winning patterns, each being a list of cell indices
     */
    fun generateWinningPatterns(gridSize: Int): List<List<Int>> {
        val patterns = mutableListOf<List<Int>>()

        // Rows
        for (row in 0 until gridSize) {
            val pattern = (0 until gridSize).map { col -> row * gridSize + col }
            patterns.add(pattern)
        }

        // Columns
        for (col in 0 until gridSize) {
            val pattern = (0 until gridSize).map { row -> row * gridSize + col }
            patterns.add(pattern)
        }

        // Main diagonal (top-left to bottom-right)
        val mainDiag = (0 until gridSize).map { i -> i * gridSize + i }
        patterns.add(mainDiag)

        // Anti-diagonal (top-right to bottom-left)
        val antiDiag = (0 until gridSize).map { i -> i * gridSize + (gridSize - 1 - i) }
        patterns.add(antiDiag)

        return patterns
    }

    /**
     * Returns center cell indices for an NxN grid.
     * For odd grids (3x3, 5x5) there is one true center.
     * For even grids (4x4) there are 4 center cells.
     */
    fun getCenterCells(gridSize: Int): List<Int> {
        return if (gridSize % 2 == 1) {
            // Odd grid: single center
            listOf(gridSize * gridSize / 2)
        } else {
            // Even grid: 4 center cells
            val mid = gridSize / 2
            listOf(
                (mid - 1) * gridSize + (mid - 1),
                (mid - 1) * gridSize + mid,
                mid * gridSize + (mid - 1),
                mid * gridSize + mid
            )
        }
    }

    /**
     * Returns corner cell indices for an NxN grid.
     */
    fun getCornerCells(gridSize: Int): List<Int> {
        val last = gridSize - 1
        return listOf(
            0,                          // top-left
            last,                       // top-right
            last * gridSize,            // bottom-left
            last * gridSize + last      // bottom-right
        )
    }
}
