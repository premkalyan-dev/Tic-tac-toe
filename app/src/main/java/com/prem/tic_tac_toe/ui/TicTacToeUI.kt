package com.prem.tic_tac_toe.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prem.tic_tac_toe.R
import com.prem.tic_tac_toe.logic.GameState
import com.prem.tic_tac_toe.logic.Player
import com.prem.tic_tac_toe.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val gameMode by viewModel.gameMode.collectAsState()
    val gridSize by viewModel.gridSize.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val aiPlayer by viewModel.aiPlayer.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }

    // Show result dialog when game ends
    LaunchedEffect(gameState.winner, gameState.isDraw) {
        if (gameState.winner != null || gameState.isDraw) {
            showResultDialog = true
        }
    }

    val gridLabel = "${gridSize}×${gridSize}"
    val modeLabel = when (gameMode) {
        GameMode.VS_FRIEND -> "vs Friend"
        GameMode.VS_COMPUTER -> "vs Computer"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Three Win",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "$gridLabel  •  $modeLabel",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Stats Display
            StatsHeader(stats = stats, onResetClick = { showResetDialog = true })

            Spacer(modifier = Modifier.height(12.dp))

            // Status Indicator
            StatusIndicator(gameState)

            Spacer(modifier = Modifier.height(12.dp))

            val haptic = LocalHapticFeedback.current
            val isVibrationEnabled by viewModel.isVibrationEnabled.collectAsState()

            // Dynamic NxN Grid
            TicTacToeGrid(
                gameState = gameState,
                gridSize = gridSize,
                onCellClick = {
                    if (isVibrationEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    viewModel.onCellClick(it)
                }
            )
        }
    }

    // Reset Stats Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Stats") },
            text = { Text("Are you sure you want to clear all your wins, losses, and draws?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetStats()
                    showResetDialog = false
                }) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Game Result Dialog
    if (showResultDialog) {
        GameResultDialog(
            gameState = gameState,
            gameMode = gameMode,
            aiPlayer = aiPlayer,
            onDismiss = { showResultDialog = false },
            onPlayAgain = {
                showResultDialog = false
                viewModel.resetGame()
            },
            onUndo = {
                showResultDialog = false
                viewModel.undoMove()
            }
        )
    }
}

@Composable
fun GameResultDialog(
    gameState: GameState,
    gameMode: GameMode,
    aiPlayer: Player,
    onDismiss: () -> Unit,
    onPlayAgain: () -> Unit,
    onUndo: () -> Unit
) {
    val resultTitle = when {
        gameState.winner != null -> {
            if (gameMode == GameMode.VS_COMPUTER) {
                if (gameState.winner == aiPlayer) "You Lost!" else "You Won!"
            } else {
                "Player ${gameState.winner} Wins!"
            }
        }
        gameState.isDraw -> "It's a Draw!"
        else -> ""
    }

    val resultIcon = when {
        gameState.winner != null -> {
            if (gameMode == GameMode.VS_COMPUTER && gameState.winner == aiPlayer) "😞" else "🎉"
        }
        gameState.isDraw -> "🤝"
        else -> ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = resultIcon,
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = resultTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        gameState.winner != null -> {
                            if (gameMode == GameMode.VS_COMPUTER && gameState.winner == aiPlayer) LossRed else WinGreen
                        }
                        else -> DrawAmber
                    }
                )
            }
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                val message = when {
                    gameState.isDraw -> "Good game! Both played well."
                    gameMode == GameMode.VS_COMPUTER && gameState.winner == aiPlayer -> "Better luck next time!"
                    else -> "Excellent move! Congratulations!"
                }
                Text(
                    text = message,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CoralOrange)
                ) {
                    Text("Play Again", color = Color.White)
                }

                OutlinedButton(
                    onClick = onUndo,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text("Undo Last Move", color = MaterialTheme.colorScheme.primary)
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    )
}

// ─── Settings Dialog (extracted for sharing) ───
@Composable
fun SettingsDialog(
    isSoundEnabled: Boolean,
    isVibrationEnabled: Boolean,
    currentMark: Player,
    currentTheme: String,
    onToggleSound: () -> Unit,
    onToggleVibration: () -> Unit,
    onSetMark: (Player) -> Unit,
    onSetTheme: (String) -> Unit,
    onResetStats: () -> Unit,
    onDismiss: () -> Unit
) {
    var showResetConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // ─── Sound Toggle ───
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sound Effects")
                    Switch(
                        checked = isSoundEnabled,
                        onCheckedChange = { onToggleSound() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ─── Vibration Toggle ───
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Vibration")
                    Switch(
                        checked = isVibrationEnabled,
                        onCheckedChange = { onToggleVibration() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                // ─── Player Mark ───
                Text(
                    "Your Mark (vs Computer)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    FilterChip(
                        selected = currentMark == Player.X,
                        onClick = { onSetMark(Player.X) },
                        label = {
                            Text(
                                "Play as X",
                                fontWeight = if (currentMark == Player.X) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = XColor.copy(alpha = 0.15f),
                            selectedLabelColor = XColor
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = currentMark == Player.O,
                        onClick = { onSetMark(Player.O) },
                        label = {
                            Text(
                                "Play as O",
                                fontWeight = if (currentMark == Player.O) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OColor.copy(alpha = 0.15f),
                            selectedLabelColor = OColor
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                // ─── App Theme ───
                Text(
                    "App Theme",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))

                val themes = listOf(
                    Triple("default", "Default", Color(0xFFFF6B35)),
                    Triple("ocean", "Ocean", Color(0xFF0288D1)),
                    Triple("forest", "Forest", Color(0xFF2E7D32)),
                    Triple("sunset", "Sunset", Color(0xFFE65100)),
                    Triple("dark", "Dark", Color(0xFF424242))
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // First row: 3 themes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        themes.take(3).forEach { (key, label, color) ->
                            val isSelected = currentTheme == key
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSetTheme(key) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    // Second row: 2 themes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        themes.drop(3).forEach { (key, label, color) ->
                            val isSelected = currentTheme == key
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSetTheme(key) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else null
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        // Spacer to balance the row
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )

                // ─── Reset Stats ───
                OutlinedButton(
                    onClick = { showResetConfirm = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LossRed),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LossRed)
                ) {
                    Text("Reset Stats", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = MaterialTheme.colorScheme.primary)
            }
        }
    )

    // Reset Stats confirmation
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset Stats") },
            text = { Text("Are you sure you want to clear all your wins, losses, and draws?") },
            confirmButton = {
                TextButton(onClick = {
                    onResetStats()
                    showResetConfirm = false
                }) {
                    Text("Reset", color = LossRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ─── Stats Header ───
@Composable
fun StatsHeader(
    stats: com.prem.tic_tac_toe.data.StatsManager.GameStats,
    onResetClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                StatItem(label = "Wins", value = stats.wins, color = WinGreen)
                StatItem(label = "Losses", value = stats.losses, color = LossRed)
                StatItem(label = "Draws", value = stats.draws, color = DrawAmber)
            }
            TextButton(onClick = onResetClick) {
                Text("Reset", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$value",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─── Status Indicator ───
@Composable
fun StatusIndicator(state: GameState) {
    val statusText = when {
        state.winner != null -> "🎉 ${state.winner} Wins!"
        state.isDraw -> "🤝 It's a Draw!"
        else -> "${state.currentTurn}'s Turn"
    }

    val statusColor = when {
        state.winner != null -> WinGreen
        state.isDraw -> DrawAmber
        state.currentTurn == Player.X -> XColor
        else -> OColor
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = statusColor.copy(alpha = 0.1f)
        )
    ) {
        Text(
            text = statusText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = statusColor,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// ─── Dynamic NxN Grid ───
@Composable
fun TicTacToeGrid(
    gameState: GameState,
    gridSize: Int,
    onCellClick: (Int) -> Unit
) {
    val maxGridWidth = 360.dp

    Column(
        modifier = Modifier
            .widthIn(max = maxGridWidth)
            .aspectRatio(1f)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .background(CardWhite, RoundedCornerShape(20.dp))
            .padding(10.dp)
    ) {
        for (row in 0 until gridSize) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                for (col in 0 until gridSize) {
                    val index = row * gridSize + col
                    val isWinningCell = gameState.winningLine?.contains(index) == true

                    val cellColor = when {
                        isWinningCell -> WinGreenLight
                        else -> LightGray.copy(alpha = 0.5f)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(3.dp)
                            .clip(RoundedCornerShape(if (gridSize <= 3) 12.dp else 8.dp))
                            .background(cellColor)
                            .clickable { onCellClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedMark(
                            player = gameState.board[index],
                            gridSize = gridSize
                        )
                    }
                }
            }
        }
    }
}

// ─── Animated Mark ───
@Composable
fun AnimatedMark(player: Player?, gridSize: Int) {
    if (player == null) return

    val scale = remember { Animatable(0f) }

    LaunchedEffect(player) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // Scale font size based on grid size
    val fontSize = when (gridSize) {
        3 -> 48.sp
        4 -> 36.sp
        5 -> 28.sp
        else -> 48.sp
    }

    Text(
        text = player.name,
        fontSize = fontSize,
        fontWeight = FontWeight.ExtraBold,
        color = if (player == Player.X) XColor else OColor,
        modifier = Modifier.scale(scale.value)
    )
}
