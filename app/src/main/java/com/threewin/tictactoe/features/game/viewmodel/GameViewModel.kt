package com.threewin.tictactoe.features.game.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.threewin.tictactoe.data.local.SettingsManager
import com.threewin.tictactoe.data.local.StatsManager
import com.threewin.tictactoe.domain.ai.AIPlayer
import com.threewin.tictactoe.domain.engine.GameEngine
import com.threewin.tictactoe.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel(
    private val statsManager: StatsManager,
    private val settingsManager: SettingsManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _gameState = MutableStateFlow(savedStateHandle.get<GameState>(KEY_GAME_STATE) ?: GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _gameMode = MutableStateFlow(savedStateHandle.get<GameMode>(KEY_GAME_MODE) ?: GameMode.VS_COMPUTER)
    val gameMode: StateFlow<GameMode> = _gameMode.asStateFlow()

    private val _boardSize = MutableStateFlow(savedStateHandle.get<Int>(KEY_BOARD_SIZE) ?: 3)
    val boardSize: StateFlow<Int> = _boardSize.asStateFlow()

    private val _aiPlayer = MutableStateFlow(savedStateHandle.get<Player>(KEY_AI_PLAYER) ?: 
        (if (settingsManager.getPreferredPlayerMark() == Player.X) Player.O else Player.X))
    val aiPlayer: StateFlow<Player> = _aiPlayer.asStateFlow()

    private val _stats = MutableStateFlow(statsManager.getStats())
    val stats: StateFlow<StatsManager.GameStats> = _stats.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(settingsManager.isSoundEnabled())
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _themeMode = MutableStateFlow(settingsManager.getThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(settingsManager.isVibrationEnabled())
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _difficultyLevel = MutableStateFlow(settingsManager.getDifficultyLevel())
    val difficultyLevel: StateFlow<DifficultyLevel> = _difficultyLevel.asStateFlow()

    private val _firstPlayerRule = MutableStateFlow(settingsManager.getFirstPlayerRule())
    val firstPlayerRule: StateFlow<FirstPlayerRule> = _firstPlayerRule.asStateFlow()

    private val _showResultDialog = MutableStateFlow(savedStateHandle.get<Boolean>(KEY_SHOW_RESULT_DIALOG) ?: false)
    val showResultDialog: StateFlow<Boolean> = _showResultDialog.asStateFlow()

    private val _gameResult = MutableStateFlow<GameResult?>(savedStateHandle.get<GameResult>(KEY_GAME_RESULT))
    val gameResult: StateFlow<GameResult?> = _gameResult.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private var gameStartTime: Long = 0
    private var isProcessingMove = false
    private var aiMoveJob: Job? = null
    private var lastWinner: Player? = savedStateHandle.get<Player>(KEY_LAST_WINNER)
    private var lastStarter: Player = savedStateHandle.get<Player>(KEY_LAST_STARTER) ?: Player.X

    init {
        val currentState = _gameState.value
        if (_gameMode.value == GameMode.VS_COMPUTER && 
            currentState.winner == null && 
            !currentState.isDraw && 
            currentState.currentTurn == _aiPlayer.value &&
            currentState.moveHistory.isNotEmpty()) {
            triggerAIMove(currentState)
        }
    }

    fun onCellClick(position: Int) {
        if (isProcessingMove) return
        val currentState = _gameState.value
        val isHumanTurn = when (_gameMode.value) {
            GameMode.VS_FRIEND -> true
            GameMode.VS_COMPUTER -> currentState.currentTurn != _aiPlayer.value
        }
        if (isHumanTurn && currentState.board[position] == null && currentState.winner == null && !currentState.isDraw) {
            if (currentState.moveHistory.isEmpty()) gameStartTime = System.currentTimeMillis()
            val newState = GameEngine.makeMove(currentState, position)
            updateGameState(newState)
            checkGameEnd(newState)
            if (_gameMode.value == GameMode.VS_COMPUTER && newState.winner == null && !newState.isDraw && newState.currentTurn == _aiPlayer.value) {
                triggerAIMove(newState)
            }
        }
    }

    private fun checkGameEnd(state: GameState) {
        if (state.winner != null || state.isDraw) {
            val endTime = System.currentTimeMillis()
            val timeTaken = (endTime - gameStartTime) / 1000
            val result = GameResult(state.winner, state.isDraw, state.moveHistory.size, timeTaken, 
                if (_gameMode.value == GameMode.VS_COMPUTER) _difficultyLevel.value else null, _gameMode.value)
            _gameResult.value = result
            savedStateHandle[KEY_GAME_RESULT] = result
            if (state.winner != null) {
                lastWinner = state.winner
                savedStateHandle[KEY_LAST_WINNER] = lastWinner
                if (_gameMode.value == GameMode.VS_FRIEND) statsManager.incrementWins()
                else if (state.winner == _aiPlayer.value) statsManager.incrementLosses()
                else statsManager.incrementWins()
            } else if (state.isDraw) {
                lastWinner = null
                savedStateHandle[KEY_LAST_WINNER] = null
                statsManager.incrementDraws()
            }
            updateStats()
            _showResultDialog.value = true
            savedStateHandle[KEY_SHOW_RESULT_DIALOG] = true
        }
    }

    fun undoMove() {
        aiMoveJob?.cancel()
        val currentHistory = _gameState.value.moveHistory
        val movesToDrop = if (_gameMode.value == GameMode.VS_FRIEND) 1 else 2
        if (currentHistory.size >= movesToDrop) {
            val newHistory = currentHistory.dropLast(movesToDrop)
            var tempState = GameState(boardSize = _boardSize.value, currentTurn = lastStarter)
            newHistory.forEach { pos -> tempState = GameEngine.makeMove(tempState, pos) }
            updateGameState(tempState)
            _showResultDialog.value = false
            savedStateHandle[KEY_SHOW_RESULT_DIALOG] = false
        }
    }

    fun dismissResultDialog() {
        _showResultDialog.value = false
        savedStateHandle[KEY_SHOW_RESULT_DIALOG] = false
    }

    private fun updateStats() { _stats.value = statsManager.getStats() }
    fun resetStats() { statsManager.resetStats(); updateStats() }

    private fun triggerAIMove(state: GameState) {
        aiMoveJob?.cancel()
        aiMoveJob = viewModelScope.launch {
            _isAiThinking.value = true
            isProcessingMove = true
            delay(500)
            val aiMove = withContext(Dispatchers.Default) {
                AIPlayer.getAIMove(state.board, state.boardSize, _aiPlayer.value, _difficultyLevel.value, coroutineContext)
            }
            if (aiMove != -1) {
                val newState = GameEngine.makeMove(_gameState.value, aiMove)
                updateGameState(newState)
                checkGameEnd(newState)
            }
            isProcessingMove = false
            _isAiThinking.value = false
        }
    }

    private fun updateGameState(newState: GameState) {
        _gameState.value = newState
        savedStateHandle[KEY_GAME_STATE] = newState
    }

    fun resetGame() {
        aiMoveJob?.cancel()
        val starter = when (_firstPlayerRule.value) {
            FirstPlayerRule.ALWAYS_X -> Player.X
            FirstPlayerRule.ALWAYS_O -> Player.O
            FirstPlayerRule.RANDOM -> if (Math.random() < 0.5) Player.X else Player.O
            FirstPlayerRule.WINNER_GOES_FIRST -> lastWinner ?: (if (lastStarter == Player.X) Player.O else Player.X)
        }
        lastStarter = starter
        savedStateHandle[KEY_LAST_STARTER] = lastStarter
        val finalState = GameState(boardSize = _boardSize.value, currentTurn = starter)
        updateGameState(finalState)
        isProcessingMove = false
        _showResultDialog.value = false
        savedStateHandle[KEY_SHOW_RESULT_DIALOG] = false
        gameStartTime = 0
        if (_gameMode.value == GameMode.VS_COMPUTER && _aiPlayer.value == starter) triggerAIMove(_gameState.value)
    }

    fun setGameMode(mode: GameMode) { _gameMode.value = mode; savedStateHandle[KEY_GAME_MODE] = mode }
    fun setBoardSize(size: Int) { _boardSize.value = size; savedStateHandle[KEY_BOARD_SIZE] = size; resetGame() }
    fun toggleSound() { val newState = !_isSoundEnabled.value; settingsManager.setSoundEnabled(newState); _isSoundEnabled.value = newState }
    fun setThemeMode(mode: ThemeMode) { settingsManager.setThemeMode(mode); _themeMode.value = mode }
    fun toggleVibration() { val newState = !_vibrationEnabled.value; settingsManager.setVibrationEnabled(newState); _vibrationEnabled.value = newState }
    fun setDifficultyLevel(level: DifficultyLevel) { settingsManager.setDifficultyLevel(level); _difficultyLevel.value = level }
    fun setFirstPlayerRule(rule: FirstPlayerRule) { settingsManager.setFirstPlayerRule(rule); _firstPlayerRule.value = rule }
    fun setHumanPlayerMark(player: Player) { settingsManager.setPreferredPlayerMark(player); val newAi = if (player == Player.X) Player.O else Player.X; _aiPlayer.value = newAi; savedStateHandle[KEY_AI_PLAYER] = newAi; resetGame() }
    fun getHumanPlayerMark(): Player = if (_aiPlayer.value == Player.X) Player.O else Player.X

    companion object {
        private const val KEY_GAME_STATE = "game_state"; private const val KEY_GAME_MODE = "game_mode"; private const val KEY_BOARD_SIZE = "board_size"
        private const val KEY_AI_PLAYER = "ai_player"; private const val KEY_SHOW_RESULT_DIALOG = "show_result_dialog"; private const val KEY_GAME_RESULT = "game_result"
        private const val KEY_LAST_WINNER = "last_winner"; private const val KEY_LAST_STARTER = "last_starter"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as android.app.Application)
                GameViewModel(StatsManager(context), SettingsManager(context), this.createSavedStateHandle())
            }
        }
    }
}
