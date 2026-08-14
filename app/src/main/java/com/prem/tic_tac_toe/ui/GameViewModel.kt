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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    private val _isSoundEnabled = MutableStateFlow(settingsManager.isSoundEnabled())
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

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
            val newState = GameEngine.makeMove(currentState, position)
            _gameState.value = newState
            checkGameEnd(newState)

            // If it's VS Computer and game is not over, trigger AI move
            if (_gameMode.value == GameMode.VS_COMPUTER && newState.winner == null && !newState.isDraw && newState.currentTurn == _aiPlayer.value) {
                triggerAIMove(newState)
            }
        }
    }

    private fun checkGameEnd(state: GameState) {
        if (state.winner != null) {
            if (_gameMode.value == GameMode.VS_FRIEND) {
                statsManager.incrementWins() // In vs friend, just count it as a win for someone
            } else {
                if (state.winner == _aiPlayer.value) {
                    statsManager.incrementLosses()
                } else {
                    statsManager.incrementWins()
                }
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

    private fun triggerAIMove(state: GameState) {
        viewModelScope.launch {
            isProcessingMove = true
            delay(500) // Delay to make AI feel more natural
            val aiMove = AIPlayer.getAIMove(state.board, _aiPlayer.value, state.gridSize)
            if (aiMove != -1) {
                val newState = GameEngine.makeMove(_gameState.value, aiMove)
                _gameState.value = newState
                checkGameEnd(newState)
            }
            isProcessingMove = false
        }
    }

    fun resetGame() {
        _gameState.value = GameState.create(_gridSize.value)
        isProcessingMove = false
        
        // If AI is X and it's VS Computer, AI makes first move
        if (_gameMode.value == GameMode.VS_COMPUTER && _aiPlayer.value == Player.X) {
            triggerAIMove(_gameState.value)
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

    fun toggleSound() {
        val newState = !_isSoundEnabled.value
        settingsManager.setSoundEnabled(newState)
        _isSoundEnabled.value = newState
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
