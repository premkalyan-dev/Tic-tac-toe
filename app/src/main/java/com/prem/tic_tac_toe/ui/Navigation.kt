package com.prem.tic_tac_toe.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prem.tic_tac_toe.logic.Player
import com.prem.tic_tac_toe.ui.theme.CoralOrange

/**
 * Navigation routes for the app.
 */
object Routes {
    const val HOME = "home"
    const val GRID_SELECT = "grid_select/{mode}"
    const val GAME = "game/{mode}/{gridSize}"
    const val PRIVACY_POLICY = "privacy_policy"

    fun gridSelect(mode: String) = "grid_select/$mode"
    fun game(mode: String, gridSize: Int) = "game/$mode/$gridSize"
}

@Composable
fun AppNavigation(viewModel: GameViewModel) {
    val navController = rememberNavController()
    val activity = LocalContext.current as? Activity

    // Settings dialog state (shared across screens)
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    val isVibrationEnabled by viewModel.isVibrationEnabled.collectAsState()
    val appTheme by viewModel.appTheme.collectAsState()
    val stats by viewModel.stats.collectAsState()

    // Track the current route to make back handling contextual
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Only show exit dialog when on the Home screen; other screens navigate back normally
    BackHandler(enabled = currentRoute == Routes.HOME) {
        showExitDialog = true
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        // ─── Home Screen ───
        composable(Routes.HOME) {
            HomeScreen(
                stats = stats,
                onPlayFriend = {
                    navController.navigate(Routes.gridSelect("friend"))
                },
                onPlayComputer = {
                    navController.navigate(Routes.gridSelect("computer"))
                },
                onSettingsClick = { showSettingsDialog = true }
            )
        }

        // ─── Grid Selection Screen ───
        composable(
            route = Routes.GRID_SELECT,
            arguments = listOf(navArgument("mode") { type = NavType.StringType })
        ) { backStackEntry ->
            val modeStr = backStackEntry.arguments?.getString("mode") ?: "computer"
            val gameMode = if (modeStr == "friend") GameMode.VS_FRIEND else GameMode.VS_COMPUTER

            GridSelectScreen(
                gameMode = gameMode,
                onGridSelected = { gridSize ->
                    navController.navigate(Routes.game(modeStr, gridSize))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ─── Game Screen ───
        composable(
            route = Routes.GAME,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("gridSize") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val modeStr = backStackEntry.arguments?.getString("mode") ?: "computer"
            val gridSize = backStackEntry.arguments?.getInt("gridSize") ?: 3
            val gameMode = if (modeStr == "friend") GameMode.VS_FRIEND else GameMode.VS_COMPUTER

            // Initialize the game with the selected mode and grid size
            LaunchedEffect(modeStr, gridSize) {
                viewModel.startGame(gameMode, gridSize)
            }

            GameScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
                onSettingsClick = { showSettingsDialog = true }
            )
        }

        // ─── Privacy Policy Screen ───
        composable(Routes.PRIVACY_POLICY) {
            PrivacyPolicyScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }

    // ─── Settings Dialog (shared) ───
    if (showSettingsDialog) {
        SettingsDialog(
            isVibrationEnabled = isVibrationEnabled,
            currentMark = viewModel.getHumanPlayerMark(),
            currentTheme = appTheme,
            onToggleVibration = { viewModel.toggleVibration() },
            onSetMark = { viewModel.setHumanPlayerMark(it) },
            onSetTheme = { viewModel.setAppTheme(it) },
            onResetStats = { viewModel.resetStats() },
            onPrivacyPolicyClick = { 
                showSettingsDialog = false
                navController.navigate(Routes.PRIVACY_POLICY)
            },
            onDismiss = { showSettingsDialog = false }
        )
    }

    // ─── Exit Confirmation Dialog ───
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = {},
            properties = DialogProperties(dismissOnClickOutside = false, dismissOnBackPress = false),
            title = {
                Text(
                    text = "Exit Game?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    text = "Do you want to exit the game?",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        activity?.finish()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Exit", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showExitDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Stay", color = CoralOrange, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}
