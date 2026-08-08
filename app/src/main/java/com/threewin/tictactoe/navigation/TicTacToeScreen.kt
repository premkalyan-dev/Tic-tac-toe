package com.threewin.tictactoe.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.content.res.Configuration
import com.threewin.tictactoe.R
import com.threewin.tictactoe.domain.model.*
import com.threewin.tictactoe.features.game.ui.screens.GameContent
import com.threewin.tictactoe.features.game.ui.screens.SelectBoardSizeScreen
import com.threewin.tictactoe.features.game.ui.screens.StartScreen
import com.threewin.tictactoe.features.game.viewmodel.GameViewModel
import kotlinx.coroutines.launch

enum class Screen {
    SPLASH, START, BOARD_SELECTION, GAME
}

@Composable
fun TicTacToeScreen(viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsState()
    val gameMode by viewModel.gameMode.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val difficultyLevel by viewModel.difficultyLevel.collectAsState()
    val firstPlayerRule by viewModel.firstPlayerRule.collectAsState()
    val showResultDialog by viewModel.showResultDialog.collectAsState()
    val gameResult by viewModel.gameResult.collectAsState()
    
    var showResetDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showLeaveGameDialog by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    
    val haptic = LocalHapticFeedback.current

    BackHandler(enabled = currentScreen != Screen.START) {
        when (currentScreen) {
            Screen.GAME -> {
                val isGameInProgress = gameState.moveHistory.isNotEmpty() && gameState.winner == null && !gameState.isDraw
                if (isGameInProgress) showLeaveGameDialog = true else currentScreen = Screen.BOARD_SELECTION
            }
            Screen.BOARD_SELECTION -> currentScreen = Screen.START
            else -> {}
        }
    }

    LaunchedEffect(gameState.winner, gameState.isDraw) {
        if (vibrationEnabled && (gameState.winner != null || gameState.isDraw)) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                if (targetState.ordinal > initialState.ordinal) {
                    (slideInHorizontally { it } + fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)))
                        .togetherWith(slideOutHorizontally { -it } + fadeOut(animationSpec = spring(stiffness = Spring.StiffnessLow)))
                } else {
                    (slideInHorizontally { -it } + fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)))
                        .togetherWith(slideOutHorizontally { it } + fadeOut(animationSpec = spring(stiffness = Spring.StiffnessLow)))
                }.using(SizeTransform(clip = false))
            },
            label = "ScreenTransition"
        ) { targetScreen ->
            when (targetScreen) {
                Screen.SPLASH -> SplashScreen(onSplashFinished = { currentScreen = Screen.START })
                Screen.START -> StartScreen(
                    onModeSelected = { mode -> viewModel.setGameMode(mode); currentScreen = Screen.BOARD_SELECTION },
                    stats = stats,
                    onResetStats = { viewModel.resetStats() }
                )
                Screen.BOARD_SELECTION -> SelectBoardSizeScreen(
                    onSizeSelected = { size -> viewModel.setBoardSize(size); currentScreen = Screen.GAME },
                    onBack = { currentScreen = Screen.START },
                    onSettingsClick = { showSettingsDialog = true }
                )
                Screen.GAME -> GameContent(
                    gameState = gameState,
                    gameMode = gameMode,
                    vibrationEnabled = vibrationEnabled,
                    haptic = haptic,
                    viewModel = viewModel,
                    onBack = { 
                        val isGameInProgress = gameState.moveHistory.isNotEmpty() && gameState.winner == null && !gameState.isDraw
                        if (isGameInProgress) showLeaveGameDialog = true else currentScreen = Screen.BOARD_SELECTION
                    },
                    onSettingsClick = { showSettingsDialog = true }
                )
            }
        }
    }

    if (showResultDialog && gameResult != null) {
        GameResultDialog(result = gameResult!!, onPlayAgain = { viewModel.resetGame() }, onHome = { viewModel.dismissResultDialog(); currentScreen = Screen.START })
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(id = R.string.reset_stats_title)) },
            text = { Text(stringResource(id = R.string.reset_stats_message)) },
            confirmButton = { TextButton(onClick = { viewModel.resetStats(); showResetDialog = false }) { Text(stringResource(id = R.string.reset_button_text), color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text(stringResource(id = R.string.cancel_button_text)) } }
        )
    }

    if (showLeaveGameDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveGameDialog = false },
            title = { Text(stringResource(id = R.string.dialog_leave_game_title)) },
            text = { Text(stringResource(id = R.string.dialog_leave_game_message)) },
            confirmButton = { TextButton(onClick = { showLeaveGameDialog = false; currentScreen = Screen.BOARD_SELECTION }) { Text(stringResource(id = R.string.action_leave), color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showLeaveGameDialog = false }) { Text(stringResource(id = R.string.action_cancel)) } }
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text(stringResource(id = R.string.settings_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth()) {
                    SettingsSectionCard(title = stringResource(id = R.string.preferences_title)) {
                        PreferenceRow(label = stringResource(id = R.string.pref_sound_label), contentDescription = stringResource(id = R.string.pref_sound_description, if (isSoundEnabled) "On" else "Off")) { Switch(checked = isSoundEnabled, onCheckedChange = { viewModel.toggleSound() }) }
                        PreferenceRow(label = stringResource(id = R.string.pref_vibration_label), contentDescription = stringResource(id = R.string.pref_vibration_description, if (vibrationEnabled) "On" else "Off")) { Switch(checked = vibrationEnabled, onCheckedChange = { viewModel.toggleVibration() }) }
                    }
                    SettingsSectionCard(title = stringResource(id = R.string.difficulty_title)) {
                        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DifficultyLevel.entries.forEach { level -> FilterChip(selected = difficultyLevel == level, onClick = { viewModel.setDifficultyLevel(level) }, label = { Text(level.name.lowercase().replaceFirstChar { it.uppercase() }) }) }
                        }
                    }
                    SettingsSectionCard(title = stringResource(id = R.string.first_player_title)) {
                        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FirstPlayerRule.entries.forEach { rule -> FilterChip(selected = firstPlayerRule == rule, onClick = { viewModel.setFirstPlayerRule(rule) }, label = { Text(rule.name.replace("_", " ").lowercase().split(" ").joinToString(" ") { word -> if (word.length == 1) word.uppercase() else word.replaceFirstChar { it.uppercase() } }) }) }
                        }
                    }
                    SettingsSectionCard(title = stringResource(id = R.string.app_theme_title)) {
                        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ThemeMode.entries.forEach { mode -> FilterChip(selected = themeMode == mode, onClick = { viewModel.setThemeMode(mode) }, label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) }) }
                        }
                    }
                    SettingsSectionCard(title = stringResource(id = R.string.preferred_mark_title)) {
                        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val currentMark = viewModel.getHumanPlayerMark()
                            FilterChip(selected = currentMark == Player.X, onClick = { viewModel.setHumanPlayerMark(Player.X) }, label = { Text("Play as X") })
                            FilterChip(selected = currentMark == Player.O, onClick = { viewModel.setHumanPlayerMark(Player.O) }, label = { Text("Play as O") })
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showSettingsDialog = false }) { Text(stringResource(id = R.string.close_button_text)) } }
        )
    }
}

@Composable
fun PreferenceRow(label: String, contentDescription: String, content: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).semantics(mergeDescendants = true) { this.contentDescription = contentDescription }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge); content()
    }
}

@Composable
fun StatsHeader(stats: com.threewin.tictactoe.data.local.StatsManager.GameStats, onResetClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            StatRow(label = stringResource(id = R.string.stat_wins), value = stats.wins)
            StatRow(label = stringResource(id = R.string.stat_losses), value = stats.losses)
            StatRow(label = stringResource(id = R.string.stat_draws), value = stats.draws)
        }
        VerticalDivider(modifier = Modifier.height(70.dp).padding(horizontal = 16.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
        TextButton(onClick = onResetClick, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp)); Text(stringResource(id = R.string.reset_button_text), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun SettingsSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(modifier = Modifier.padding(16.dp)) { Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary); Spacer(modifier = Modifier.height(8.dp)); content() }
    }
}

@Composable
fun StatRow(label: String, value: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        AnimatedContent(targetState = value, transitionSpec = { (slideInVertically { it } + fadeIn()).togetherWith(slideOutVertically { -it } + fadeOut()) }, label = "StatAnimation") { targetValue ->
            Text(text = "$targetValue", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 12.dp))
        }
    }
}

@Composable
fun GameResultDialog(result: GameResult, onPlayAgain: () -> Unit, onHome: () -> Unit) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    Dialog(onDismissRequest = { }, properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = !isLandscape)) {
        val scope = rememberCoroutineScope(); val scale = remember { Animatable(0.9f) }; val alpha = remember { Animatable(0f) }
        LaunchedEffect(Unit) { scope.launch { scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) }; scope.launch { alpha.animateTo(1f, animationSpec = spring(stiffness = Spring.StiffnessLow)) } }
        Surface(modifier = Modifier.padding(24.dp).wrapContentHeight().fillMaxWidth(if (isLandscape) 0.6f else 1f).scale(scale.value).graphicsLayer(alpha = alpha.value), shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 6.dp, shadowElevation = 8.dp) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                val (icon, color, title, message) = when {
                    result.winner == Player.X -> arrayOf(Icons.Filled.EmojiEvents, Color(0xFFFFD700), stringResource(id = R.string.result_x_wins), stringResource(id = R.string.result_x_wins_subtitle))
                    result.winner == Player.O -> arrayOf(Icons.Filled.EmojiEvents, Color(0xFFC0C0C0), stringResource(id = R.string.result_o_wins), stringResource(id = R.string.result_o_wins_subtitle))
                    else -> arrayOf(Icons.Filled.Handshake, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), stringResource(id = R.string.result_draw_title), stringResource(id = R.string.result_draw_subtitle))
                }
                Icon(imageVector = icon as androidx.compose.ui.graphics.vector.ImageVector, contentDescription = title as String, modifier = Modifier.size(80.dp).padding(bottom = 16.dp), tint = color as Color)
                Text(text = title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp)); Text(text = message as String, style = MaterialTheme.typography.bodyMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(modifier = Modifier.height(24.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatDetailRow(label = stringResource(id = R.string.stat_game_mode), value = if (result.gameMode == GameMode.VS_FRIEND) stringResource(id = R.string.mode_vs_friend) else stringResource(id = R.string.mode_vs_computer))
                        if (result.difficulty != null) StatDetailRow(label = stringResource(id = R.string.stat_difficulty), value = result.difficulty.name.lowercase().replaceFirstChar { it.uppercase() })
                        if (result.winner != null) StatDetailRow(label = stringResource(id = R.string.stat_winner), value = result.winner.name)
                        StatDetailRow(label = stringResource(id = R.string.stat_time), value = formatTime(result.timeTakenSeconds))
                        StatDetailRow(label = stringResource(id = R.string.stat_moves), value = result.totalMoves.toString())
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onPlayAgain, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) { Text(text = stringResource(id = R.string.action_play_again), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                Spacer(modifier = Modifier.height(12.dp)); OutlinedButton(onClick = onHome, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))) { Text(text = stringResource(id = R.string.action_home), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    // Staggered entrance animations
    val logoScale = remember { Animatable(0.7f) }
    val logoAlpha = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleOffset = remember { Animatable(20f) }
    val subtitleAlpha = remember { Animatable(0f) }

    // Exit animation
    val exitScale = remember { Animatable(1f) }
    val exitAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Phase 1: Logo entrance (0ms)
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        }
        launch {
            logoScale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }

        // Phase 2: Title entrance (300ms)
        kotlinx.coroutines.delay(300)
        launch {
            titleAlpha.animateTo(1f, animationSpec = tween(400))
        }
        launch {
            titleOffset.animateTo(0f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }

        // Phase 3: Subtitle entrance (500ms)
        kotlinx.coroutines.delay(200)
        launch {
            subtitleAlpha.animateTo(1f, animationSpec = tween(350))
        }

        // Phase 4: Hold briefly, then exit (1200ms after subtitle)
        kotlinx.coroutines.delay(800)

        // Smooth exit
        launch {
            exitScale.animateTo(1.1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        }
        launch {
            exitAlpha.animateTo(0f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        }
        kotlinx.coroutines.delay(300)
        onSplashFinished()
    }

    // Elegant 2-color gradient
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1A1A2E),  // Deep navy
            Color(0xFF16213E),  // Dark blue
            Color(0xFF0F3460)   // Rich blue
        ),
        start = Offset(0f, 0f),
        end = Offset(500f, 1800f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .graphicsLayer(
                scaleX = exitScale.value,
                scaleY = exitScale.value,
                alpha = exitAlpha.value
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo with elegant container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = logoScale.value,
                        scaleY = logoScale.value,
                        alpha = logoAlpha.value
                    )
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            ),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )

                // Logo container
                val isPreview = LocalInspectionMode.current
                Image(
                    painter = painterResource(
                        id = if (isPreview) R.drawable.ic_launcher_foreground else R.drawable.app_logo_new
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .padding(4.dp)
                        .graphicsLayer(
                            clip = true,
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name — staggered entrance
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp,
                modifier = Modifier.graphicsLayer(
                    alpha = titleAlpha.value,
                    translationY = titleOffset.value
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline — last to appear
            Text(
                text = "Challenge Your Mind",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 2.sp,
                modifier = Modifier.graphicsLayer(alpha = subtitleAlpha.value)
            )
        }
    }
}

@Composable
fun StatDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun formatTime(seconds: Long): String {
    val mins = seconds / 60; val secs = seconds % 60
    return java.util.Locale.getDefault().let { locale -> String.format(locale, "%02d:%02d", mins, secs) }
}
