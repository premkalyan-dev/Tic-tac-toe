package com.prem.tic_tac_toe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.prem.tic_tac_toe.data.SettingsManager
import com.prem.tic_tac_toe.data.StatsManager
import com.prem.tic_tac_toe.logic.AIPlayer
import com.prem.tic_tac_toe.logic.GameEngine
import com.prem.tic_tac_toe.logic.GameState
import com.prem.tic_tac_toe.logic.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Modes of the game.
 */
enum class GameMode {
    VS_FRIEND, VS_COMPUTER
}

class GameViewModel(
    private val statsManager: StatsManager,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _gameMode = MutableStateFlow(GameMode.VS_COMPUTER)
    val gameMode: StateFlow<GameMode> = _gameMode.asStateFlow()

    private val _gridSize = MutableStateFlow(3)
    val gridSize: StateFlow<Int> = _gridSize.asStateFlow()

    private val _aiPlayer = MutableStateFlow(
        if (settingsManager.getPreferredPlayerMark() == Player.X) Player.O else Player.X
    )
    val aiPlayer: StateFlow<Player> = _aiPlayer.asStateFlow()

    private val _stats = MutableStateFlow(statsManager.getStats())
    val stats: StateFlow<StatsManager.GameStats> = _stats.asStateFlow()


    private val _isVibrationEnabled = MutableStateFlow(settingsManager.isVibrationEnabled())
    val isVibrationEnabled: StateFlow<Boolean> = _isVibrationEnabled.asStateFlow()

    private val _appTheme = MutableStateFlow(settingsManager.getAppTheme())
    val appTheme: StateFlow<String> = _appTheme.asStateFlow()

    private val history = mutableListOf<GameState>()

    private var isProcessingMove = false

    fun onCellClick(position: Int) {
        if (isProcessingMove) return

        val currentState = _gameState.value
        
        // Only allow move if it's the human player's turn or in vs friend mode
        val isHumanTurn = when (_gameMode.value) {
            GameMode.VS_FRIEND -> true
            GameMode.VS_COMPUTER -> currentState.currentTurn != _aiPlayer.value
        }

        if (isHumanTurn && currentState.board[position] == null && currentState.winner == null && !currentState.isDraw) {
            history.add(currentState)
            val newState = GameEngine.makeMove(currentState, position)
            _gameState.value = newState
            checkGameEnd(newState)

            // If it's VS Computer and game is not over, trigger AI move
            if (_gameMode.value == GameMode.VS_COMPUTER && newState.winner == null && !newState.isDraw && newState.currentTurn == _aiPlayer.value) {
                triggerAIMove()
            }
        }
    }

    private fun checkGameEnd(state: GameState) {
        if (state.winner != null) {
            val humanMark = getHumanPlayerMark()
            if (state.winner == humanMark) {
                statsManager.incrementWins()
            } else {
                statsManager.incrementLosses()
            }
            updateStats()
        } else if (state.isDraw) {
            statsManager.incrementDraws()
            updateStats()
        }
    }

    private fun updateStats() {
        _stats.value = statsManager.getStats()
    }

    fun resetStats() {
        statsManager.resetStats()
        updateStats()
    }

    private fun triggerAIMove() {
        viewModelScope.launch {
            isProcessingMove = true
            delay(500) // Delay to make AI feel more natural
            val currentState = _gameState.value
            val aiMove = withContext(Dispatchers.Default) {
                AIPlayer.getAIMove(currentState.board, _aiPlayer.value, currentState.gridSize)
            }
            if (aiMove != -1) {
                history.add(currentState)
                val newState = GameEngine.makeMove(currentState, aiMove)
                _gameState.value = newState
                checkGameEnd(newState)
            }
            isProcessingMove = false
        }
    }

    fun resetGame() {
        _gameState.value = GameState.create(_gridSize.value)
        history.clear()
        isProcessingMove = false
        
        // If AI is X and it's VS Computer, AI makes first move
        if (_gameMode.value == GameMode.VS_COMPUTER && _aiPlayer.value == Player.X) {
            triggerAIMove()
        }
    }

    fun setGameMode(mode: GameMode) {
        _gameMode.value = mode
        resetGame()
    }

    fun setGridSize(size: Int) {
        _gridSize.value = size
        resetGame()
    }

    /**
     * Configure game from navigation parameters and start.
     */
    fun startGame(mode: GameMode, gridSize: Int) {
        _gameMode.value = mode
        _gridSize.value = gridSize
        resetGame()
    }

    fun undoMove() {
        if (history.isEmpty()) return

        val currentState = _gameState.value

        // Decrement stats if the game had ended
        if (currentState.winner != null) {
            val humanMark = getHumanPlayerMark()
            if (currentState.winner == humanMark) {
                statsManager.decrementWins()
            } else {
                statsManager.decrementLosses()
            }
        } else if (currentState.isDraw) {
            statsManager.decrementDraws()
        }

        if (_gameMode.value == GameMode.VS_COMPUTER) {
            // Determine who made the last move by checking who just played.
            // currentState.currentTurn is the NEXT player to move,
            // so the last move was made by the OTHER player.
            val lastMoveBy = if (currentState.currentTurn == Player.X) Player.O else Player.X

            if (lastMoveBy == _aiPlayer.value && history.size >= 2) {
                // Last move was by AI (e.g., AI won or draw after AI move).
                // Undo both the AI move and the preceding human move.
                history.removeAt(history.size - 1) // Remove state before AI move
                _gameState.value = history.removeAt(history.size - 1) // Restore state before human move
            } else {
                // Last move was by human (e.g., human won).
                // Undo only the human's move.
                _gameState.value = history.removeAt(history.size - 1)
                // Now it's human's turn again, no need to trigger AI.
            }
        } else {
            // VS Friend: just undo one move
            _gameState.value = history.removeAt(history.size - 1)
        }
        
        updateStats()
    }


    fun toggleVibration() {
        val newState = !_isVibrationEnabled.value
        settingsManager.setVibrationEnabled(newState)
        _isVibrationEnabled.value = newState
    }

    fun setAppTheme(theme: String) {
        settingsManager.setAppTheme(theme)
        _appTheme.value = theme
    }

    fun setHumanPlayerMark(player: Player) {
        settingsManager.setPreferredPlayerMark(player)
        _aiPlayer.value = if (player == Player.X) Player.O else Player.X
        resetGame()
    }

    fun getHumanPlayerMark(): Player {
        return if (_aiPlayer.value == Player.X) Player.O else Player.X
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as android.app.Application)
                GameViewModel(
                    StatsManager(context),
                    SettingsManager(context)
                )
            }
        }
    }
}
