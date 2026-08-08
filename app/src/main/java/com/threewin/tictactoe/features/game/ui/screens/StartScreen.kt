package com.threewin.tictactoe.features.game.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.threewin.tictactoe.R
import com.threewin.tictactoe.data.local.StatsManager
import com.threewin.tictactoe.domain.model.GameMode

@Composable
fun StartScreen(
    onModeSelected: (GameMode) -> Unit,
    stats: StatsManager.GameStats = StatsManager.GameStats(0, 0, 0),
    onResetStats: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = null
                        )
                    },
                    label = { Text("Home", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                            contentDescription = null
                        )
                    },
                    label = { Text("Achievements", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedTab,
            modifier = Modifier.padding(innerPadding),
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { it } + fadeIn())
                        .togetherWith(slideOutHorizontally { -it } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn())
                        .togetherWith(slideOutHorizontally { it } + fadeOut())
                }
            },
            label = "TabTransition"
        ) { tab ->
            when (tab) {
                0 -> HomeContent(onModeSelected = onModeSelected)
                1 -> AchievementsScreen(stats = stats, onResetStats = onResetStats)
            }
        }
    }
}

@Composable
private fun HomeContent(onModeSelected: (GameMode) -> Unit) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Column(
        modifier = Modifier.fillMaxSize().padding(if (isTablet) 64.dp else 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(
                id = if (LocalInspectionMode.current) R.drawable.ic_launcher_foreground else R.drawable.app_logo_new
            ),
            contentDescription = null,
            modifier = Modifier.size(if (isTablet) 320.dp else 200.dp).padding(bottom = 32.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = stringResource(id = R.string.app_name),
            style = if (isTablet) MaterialTheme.typography.displayLarge else MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(if (isTablet) 80.dp else 48.dp))

        val buttonModifier = Modifier
            .widthIn(max = 480.dp)
            .fillMaxWidth()
            .height(if (isTablet) 80.dp else 64.dp)

        Button(
            onClick = { onModeSelected(GameMode.VS_COMPUTER) },
            modifier = buttonModifier,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.SmartToy, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(stringResource(id = R.string.mode_vs_computer), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { onModeSelected(GameMode.VS_FRIEND) },
            modifier = buttonModifier,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Group, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(stringResource(id = R.string.mode_vs_friend), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

