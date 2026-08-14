package com.prem.tic_tac_toe.ui

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prem.tic_tac_toe.logic.Player

/**
 * Navigation routes for the app.
 */
object Routes {
    const val HOME = "home"
    const val GRID_SELECT = "grid_select/{mode}"
    const val GAME = "game/{mode}/{gridSize}"

    fun gridSelect(mode: String) = "grid_select/$mode"
    fun game(mode: String, gridSize: Int) = "game/$mode/$gridSize"
}

@Composable
fun AppNavigation(viewModel: GameViewModel) {
    val navController = rememberNavController()

    // Settings dialog state (shared across screens)
    var showSettingsDialog by remember { mutableStateOf(false) }
    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
    val isVibrationEnabled by viewModel.isVibrationEnabled.collectAsState()
    val appTheme by viewModel.appTheme.collectAsState()
    val stats by viewModel.stats.collectAsState()

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
    }

    // ─── Settings Dialog (shared) ───
    if (showSettingsDialog) {
        SettingsDialog(
            isSoundEnabled = isSoundEnabled,
            isVibrationEnabled = isVibrationEnabled,
            currentMark = viewModel.getHumanPlayerMark(),
            currentTheme = appTheme,
            onToggleSound = { viewModel.toggleSound() },
            onToggleVibration = { viewModel.toggleVibration() },
            onSetMark = { viewModel.setHumanPlayerMark(it) },
            onSetTheme = { viewModel.setAppTheme(it) },
            onResetStats = { viewModel.resetStats() },
            onDismiss = { showSettingsDialog = false }
        )
    }
}
