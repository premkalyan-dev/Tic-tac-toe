package com.threewin.tictactoe.domain.engine

object GameConstants {
    private val patternsCache = mutableMapOf<Int, List<List<Int>>>()

    fun getWinningPatterns(size: Int): List<List<Int>> {
        return patternsCache.getOrPut(size) {
            val patterns = mutableListOf<List<Int>>()

            // Rows
            for (i in 0 until size) {
                patterns.add((0 until size).map { i * size + it })
            }

            // Columns
            for (i in 0 until size) {
                patterns.add((0 until size).map { i + it * size })
            }

            // Diagonals
            patterns.add((0 until size).map { it * size + it })
            patterns.add((0 until size).map { it * size + (size - 1 - it) })

            patterns
        }
    }
}
