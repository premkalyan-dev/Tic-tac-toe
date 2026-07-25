package com.prem.tic_tac_toe.data

import android.content.Context
import android.content.SharedPreferences
import com.prem.tic_tac_toe.logic.Player

/**
 * Manages game settings using SharedPreferences.
 */
class SettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("game_settings", Context.MODE_PRIVATE)

    fun isSoundEnabled(): Boolean {
        return prefs.getBoolean(KEY_SOUND, true)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
    }

    fun getPreferredPlayerMark(): Player {
        val markStr = prefs.getString(KEY_PLAYER_MARK, Player.X.name) ?: Player.X.name
        return Player.valueOf(markStr)
    }

    fun setPreferredPlayerMark(player: Player) {
        prefs.edit().putString(KEY_PLAYER_MARK, player.name).apply()
    }

    companion object {
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_PLAYER_MARK = "player_mark"
    }
}
