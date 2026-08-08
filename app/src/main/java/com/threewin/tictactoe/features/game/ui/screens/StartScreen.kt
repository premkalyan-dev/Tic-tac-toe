package com.threewin.tictactoe.features.game.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                0 -> HomeContent(onModeSelected = onModeSelected, stats = stats)
                1 -> AchievementsScreen(stats = stats, onResetStats = onResetStats)
            }
        }
    }
}

@Composable
private fun HomeContent(onModeSelected: (GameMode) -> Unit, stats: StatsManager.GameStats) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val totalGames = stats.wins + stats.losses + stats.draws

    // Entrance animations
    val logoScale = remember { Animatable(0.8f) }
    val contentAlpha = remember { Animatable(0f) }
    val buttonsOffset = remember { Animatable(40f) }

    LaunchedEffect(Unit) {
        launch {
            logoScale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            contentAlpha.animateTo(1f, animationSpec = tween(600))
        }
        launch {
            kotlinx.coroutines.delay(200)
            buttonsOffset.animateTo(0f, animationSpec = spring(stiffness = Spring.StiffnessLow))
        }
    }

    // Subtle floating animation for logo
    val infiniteTransition = rememberInfiniteTransition(label = "LogoFloat")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatY"
    )

    // Background gradient colors
    val gradientColors = listOf(
        MaterialTheme.colorScheme.background,
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
        MaterialTheme.colorScheme.background
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(0f, 2000f)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = if (isTablet) 64.dp else 24.dp)
                .padding(top = if (isTablet) 48.dp else 32.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            // Logo with glow container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = logoScale.value,
                        scaleY = logoScale.value,
                        alpha = contentAlpha.value,
                        translationY = floatOffset
                    )
            ) {
                // Glow shadow behind logo
                Box(
                    modifier = Modifier
                        .size(if (isTablet) 200.dp else 140.dp)
                        .shadow(
                            elevation = 24.dp,
                            shape = CircleShape,
                            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                )

                Image(
                    painter = painterResource(
                        id = if (LocalInspectionMode.current) R.drawable.ic_launcher_foreground else R.drawable.app_logo_new
                    ),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(if (isTablet) 180.dp else 120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Name
            Text(
                text = stringResource(id = R.string.app_name),
                style = if (isTablet) MaterialTheme.typography.displayMedium else MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.graphicsLayer(alpha = contentAlpha.value)
            )

            // Tagline
            Text(
                text = "Challenge Your Mind",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .graphicsLayer(alpha = contentAlpha.value),
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Stats Banner
            if (totalGames > 0) {
                Card(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxWidth()
                        .graphicsLayer(alpha = contentAlpha.value),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QuickStatItem(emoji = "🏆", value = stats.wins, label = "Wins")
                        QuickStatDivider()
                        QuickStatItem(emoji = "😞", value = stats.losses, label = "Losses")
                        QuickStatDivider()
                        QuickStatItem(emoji = "🤝", value = stats.draws, label = "Draws")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Mode Selection Cards
            val cardModifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .graphicsLayer(
                    alpha = contentAlpha.value,
                    translationY = buttonsOffset.value
                )

            ModeCard(
                icon = Icons.Default.SmartToy,
                title = stringResource(id = R.string.mode_vs_computer),
                subtitle = "Test your skills against AI",
                isPrimary = true,
                modifier = cardModifier,
                onClick = { onModeSelected(GameMode.VS_COMPUTER) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            ModeCard(
                icon = Icons.Default.Group,
                title = stringResource(id = R.string.mode_vs_friend),
                subtitle = "Play with a friend on same device",
                isPrimary = false,
                modifier = cardModifier,
                onClick = { onModeSelected(GameMode.VS_FRIEND) }
            )

            Spacer(modifier = Modifier.weight(0.5f))
        }
    }
}

@Composable
private fun QuickStatItem(emoji: String, value: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 18.sp)
        Text(
            text = "$value",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun QuickStatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(36.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}

@Composable
private fun ModeCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isPrimary: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimary)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPrimary) 6.dp else 2.dp
        ),
        border = if (!isPrimary)
            androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            )
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = if (isTablet) 24.dp else 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isPrimary)
                            Color.White.copy(alpha = 0.2f)
                        else
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = if (isPrimary)
                        Color.White
                    else
                        MaterialTheme.colorScheme.primary
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPrimary)
                        Color.White
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isPrimary)
                        Color.White.copy(alpha = 0.75f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer(alpha = 0f) // invisible spacer to keep row balanced
            )
        }
    }
}

