package com.prem.tic_tac_toe.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prem.tic_tac_toe.R
import com.prem.tic_tac_toe.logic.GameState
import com.prem.tic_tac_toe.logic.Player

@Composable
fun TicTacToeScreen(viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsState()
    val gameMode by viewModel.gameMode.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
    
    var showResetDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tic Tac Toe",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Display
            StatsHeader(stats = stats, onResetClick = { showResetDialog = true })

            Spacer(modifier = Modifier.height(16.dp))

            // Game Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = gameMode == GameMode.VS_FRIEND,
                    onClick = { viewModel.setGameMode(GameMode.VS_FRIEND) },
                    label = { Text("vs Friend") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = gameMode == GameMode.VS_COMPUTER,
                    onClick = { viewModel.setGameMode(GameMode.VS_COMPUTER) },
                    label = { Text("vs Computer") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Indicator
            StatusIndicator(gameState)

            Spacer(modifier = Modifier.height(16.dp))

            // 3x3 Grid
            TicTacToeGrid(
                gameState = gameState,
                onCellClick = { viewModel.onCellClick(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Play Again Button
            if (gameState.winner != null || gameState.isDraw) {
                Button(
                    onClick = { viewModel.resetGame() },
                    modifier = Modifier.height(56.dp).fillMaxWidth(0.6f)
                ) {
                    Text("Play Again", fontSize = 18.sp)
                }
            }
        }

        // Settings Button
        TextButton(
            onClick = { showSettingsDialog = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("Settings")
        }
    }

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

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Settings") },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sound Effects")
                        Switch(
                            checked = isSoundEnabled,
                            onCheckedChange = { viewModel.toggleSound() }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Your Mark (vs Computer)", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        val currentMark = viewModel.getHumanPlayerMark()
                        FilterChip(
                            selected = currentMark == Player.X,
                            onClick = { viewModel.setHumanPlayerMark(Player.X) },
                            label = { Text("Play as X") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilterChip(
                            selected = currentMark == Player.O,
                            onClick = { viewModel.setHumanPlayerMark(Player.O) },
                            label = { Text("Play as O") }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun StatsHeader(stats: com.prem.tic_tac_toe.data.StatsManager.GameStats, onResetClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Wins: ${stats.wins}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Losses: ${stats.losses}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Draws: ${stats.draws}", style = MaterialTheme.typography.bodyLarge)
            }
            TextButton(onClick = onResetClick) {
                Text("Reset Stats", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun StatusIndicator(state: GameState) {
    val statusText = when {
        state.winner != null -> "Winner: ${state.winner}"
        state.isDraw -> "It's a Draw!"
        else -> "${state.currentTurn}'s Turn"
    }

    Text(
        text = statusText,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Medium,
        color = if (state.winner != null) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun TicTacToeGrid(
    gameState: GameState,
    onCellClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        for (row in 0..2) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                for (col in 0..2) {
                    val index = row * 3 + col
                    val isWinningCell = gameState.winningLine?.contains(index) == true
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp)
                            .background(
                                color = if (isWinningCell) Color(0xFFC8E6C9) else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onCellClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedMark(player = gameState.board[index])
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedMark(player: Player?) {
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

    Text(
        text = player.name,
        fontSize = 48.sp,
        fontWeight = FontWeight.ExtraBold,
        color = if (player == Player.X) Color(0xFFE91E63) else Color(0xFF2196F3),
        modifier = Modifier.scale(scale.value)
    )
}
