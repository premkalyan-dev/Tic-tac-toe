package com.prem.tic_tac_toe.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages game statistics using SharedPreferences.
 */
class StatsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("game_stats", Context.MODE_PRIVATE)

    data class GameStats(val wins: Int, val losses: Int, val draws: Int)

    fun getStats(): GameStats {
        return GameStats(
            wins = prefs.getInt(KEY_WINS, 0),
            losses = prefs.getInt(KEY_LOSSES, 0),
            draws = prefs.getInt(KEY_DRAWS, 0)
        )
    }

    fun incrementWins() {
        prefs.edit().putInt(KEY_WINS, prefs.getInt(KEY_WINS, 0) + 1).apply()
    }

    fun incrementLosses() {
        prefs.edit().putInt(KEY_LOSSES, prefs.getInt(KEY_LOSSES, 0) + 1).apply()
    }

    fun incrementDraws() {
        prefs.edit().putInt(KEY_DRAWS, prefs.getInt(KEY_DRAWS, 0) + 1).apply()
    }

    fun decrementWins() {
        val current = prefs.getInt(KEY_WINS, 0)
        if (current > 0) prefs.edit().putInt(KEY_WINS, current - 1).apply()
    }

    fun decrementLosses() {
        val current = prefs.getInt(KEY_LOSSES, 0)
        if (current > 0) prefs.edit().putInt(KEY_LOSSES, current - 1).apply()
    }

    fun decrementDraws() {
        val current = prefs.getInt(KEY_DRAWS, 0)
        if (current > 0) prefs.edit().putInt(KEY_DRAWS, current - 1).apply()
    }

    fun resetStats() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_WINS = "wins"
        private const val KEY_LOSSES = "losses"
        private const val KEY_DRAWS = "draws"
    }
}
