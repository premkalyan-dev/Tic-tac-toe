package com.prem.tic_tac_toe.data

import android.content.Context
import android.content.SharedPreferences
import com.prem.tic_tac_toe.logic.Player

/**
 * Manages game settings using SharedPreferences.
 */
class SettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("game_settings", Context.MODE_PRIVATE)


    fun isVibrationEnabled(): Boolean {
        return prefs.getBoolean(KEY_VIBRATION, true)
    }

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()
    }

    fun getAppTheme(): String {
        return prefs.getString(KEY_THEME, THEME_DEFAULT) ?: THEME_DEFAULT
    }

    fun setAppTheme(theme: String) {
        prefs.edit().putString(KEY_THEME, theme).apply()
    }

    fun getPreferredPlayerMark(): Player {
        val markStr = prefs.getString(KEY_PLAYER_MARK, Player.X.name) ?: Player.X.name
        return Player.valueOf(markStr)
    }

    fun setPreferredPlayerMark(player: Player) {
        prefs.edit().putString(KEY_PLAYER_MARK, player.name).apply()
    }

    companion object {
        private const val KEY_VIBRATION = "vibration_enabled"
        private const val KEY_PLAYER_MARK = "player_mark"
        private const val KEY_THEME = "app_theme"

        const val THEME_DEFAULT = "default"
        const val THEME_OCEAN = "ocean"
        const val THEME_FOREST = "forest"
        const val THEME_SUNSET = "sunset"
        const val THEME_DARK = "dark"
    }
}
