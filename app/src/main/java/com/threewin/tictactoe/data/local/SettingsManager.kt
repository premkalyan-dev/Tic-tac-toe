package com.threewin.tictactoe.data.local

import android.content.Context
import android.content.SharedPreferences
import com.threewin.tictactoe.domain.model.DifficultyLevel
import com.threewin.tictactoe.domain.model.FirstPlayerRule
import com.threewin.tictactoe.domain.model.Player
import com.threewin.tictactoe.domain.model.ThemeMode

class SettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("game_settings", Context.MODE_PRIVATE)

    fun isSoundEnabled(): Boolean = prefs.getBoolean(KEY_SOUND, true)
    fun setSoundEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_SOUND, enabled).apply()

    fun getPreferredPlayerMark(): Player {
        val markStr = prefs.getString(KEY_PLAYER_MARK, Player.X.name) ?: Player.X.name
        return Player.valueOf(markStr)
    }
    fun setPreferredPlayerMark(player: Player) = prefs.edit().putString(KEY_PLAYER_MARK, player.name).apply()

    fun isVibrationEnabled(): Boolean = prefs.getBoolean(KEY_VIBRATION, true)
    fun setVibrationEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()

    fun getDifficultyLevel(): DifficultyLevel {
        val difficultyStr = prefs.getString(KEY_DIFFICULTY, DifficultyLevel.MEDIUM.name) ?: DifficultyLevel.MEDIUM.name
        return DifficultyLevel.valueOf(difficultyStr)
    }
    fun setDifficultyLevel(level: DifficultyLevel) = prefs.edit().putString(KEY_DIFFICULTY, level.name).apply()

    fun getFirstPlayerRule(): FirstPlayerRule {
        val ruleStr = prefs.getString(KEY_FIRST_PLAYER, FirstPlayerRule.ALWAYS_X.name) ?: FirstPlayerRule.ALWAYS_X.name
        return FirstPlayerRule.valueOf(ruleStr)
    }
    fun setFirstPlayerRule(rule: FirstPlayerRule) = prefs.edit().putString(KEY_FIRST_PLAYER, rule.name).apply()

    fun getThemeMode(): ThemeMode {
        val themeStr = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return ThemeMode.valueOf(themeStr)
    }
    fun setThemeMode(mode: ThemeMode) = prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()

    companion object {
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_PLAYER_MARK = "player_mark"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_VIBRATION = "vibration_enabled"
        private const val KEY_DIFFICULTY = "difficulty_level"
        private const val KEY_FIRST_PLAYER = "first_player_rule"
    }
}
