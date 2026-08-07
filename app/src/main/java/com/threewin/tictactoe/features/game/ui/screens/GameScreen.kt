package com.threewin.tictactoe.features.game.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.threewin.tictactoe.R
import com.threewin.tictactoe.domain.model.GameMode
import com.threewin.tictactoe.domain.model.GameState
import com.threewin.tictactoe.domain.model.Player
import com.threewin.tictactoe.features.game.viewmodel.GameViewModel

@Composable
fun GameContent(
    gameState: GameState,
    gameMode: GameMode,
    vibrationEnabled: Boolean,
    haptic: HapticFeedback,
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp).height(56.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back_to_selection_description))
            }
            Text(text = if (gameMode == GameMode.VS_FRIEND) stringResource(id = R.string.label_vs_friend) else stringResource(id = R.string.label_vs_computer), style = if (isTablet) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
        }

        Column(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = if (isTablet) 32.dp else 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (gameState.winner == null && !gameState.isDraw) {
                Box(contentAlignment = Alignment.Center) {
                    StatusIndicator(gameState)
                    if (isAiThinking) {
                        CircularProgressIndicator(modifier = Modifier.padding(start = 130.dp).size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            } else { Spacer(modifier = Modifier.height(48.dp)) }

            Spacer(modifier = Modifier.height(if (isTablet) 48.dp else 32.dp))
            
            Box(modifier = Modifier.widthIn(max = 600.dp)) {
                TicTacToeGrid(gameState = gameState, onCellClick = { if (vibrationEnabled) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); viewModel.onCellClick(it) })
            }
            
            Spacer(modifier = Modifier.height(if (isTablet) 32.dp else 24.dp))
            
            // Undo Button
            Button(
                onClick = { viewModel.undoMove() },
                modifier = Modifier.padding(8.dp).height(if (isTablet) 56.dp else 48.dp).widthIn(min = 120.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(id = R.string.action_undo), style = if (isTablet) MaterialTheme.typography.titleMedium else MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(if (isTablet) 120.dp else 88.dp))
        }
    }
}

@Composable
fun TicTacToeGrid(gameState: GameState, onCellClick: (Int) -> Unit) {
    val size = gameState.boardSize
    Column(modifier = Modifier.aspectRatio(1f).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)).padding(12.dp)) {
        for (row in 0 until size) {
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                for (col in 0 until size) {
                    val index = row * size + col
                    val isWinningCell = gameState.winningLine?.contains(index) == true
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(4.dp).background(color = if (isWinningCell) Color(0xFFC8E6C9) else MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(if (size > 3) 4.dp else 8.dp)).clickable { onCellClick(index) }, contentAlignment = Alignment.Center) {
                        AnimatedMark(player = gameState.board[index], fontSize = if (size == 3) 48.sp else if (size == 4) 36.sp else 28.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedMark(player: Player?, fontSize: androidx.compose.ui.unit.TextUnit = 48.sp) {
    if (player == null) return
    val scale = remember { Animatable(0f) }
    LaunchedEffect(player) { scale.animateTo(targetValue = 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }
    Text(text = player.name, fontSize = fontSize, fontWeight = FontWeight.ExtraBold, color = if (player == Player.X) Color(0xFFE91E63) else Color(0xFF2196F3), modifier = Modifier.scale(scale.value))
}

@Composable
fun StatusIndicator(state: GameState) {
    val statusText = if (state.moveHistory.isEmpty()) stringResource(id = R.string.first_move_label, state.currentTurn.toString()) else stringResource(id = R.string.game_turn_label, state.currentTurn.toString())
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        AnimatedContent(targetState = statusText, transitionSpec = { (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut()) }, label = "TurnTransition") { text ->
            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp).widthIn(min = 120.dp), contentAlignment = Alignment.Center) {
                Text(text = text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}
