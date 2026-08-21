package com.prem.tic_tac_toe.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prem.tic_tac_toe.data.StatsManager
import com.prem.tic_tac_toe.ui.theme.*


@Composable
fun HomeScreen(
    stats: StatsManager.GameStats,
    onPlayFriend: () -> Unit,
    onPlayComputer: () -> Unit,
    onSettingsClick: () -> Unit
) {
    // Entrance animation
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = EaseOutBack)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // ─── 1. Neon Glow "Tic Tac Toe" Title ───
            NeonTitle(
                animatedProgress = animatedProgress.value
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ─── 2. "Play with Friend" Button ───
            ModeCard(
                title = "Play with Friend",
                subtitle = "Challenge a local player",
                icon = Icons.Filled.People,
                gradientColors = listOf(Color(0xFF43A047), Color(0xFF1B5E20)),
                onClick = onPlayFriend,
                animatedProgress = animatedProgress.value
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ─── 3. "Play with Computer" Button ───
            ModeCard(
                title = "Play with Computer",
                subtitle = "Test your skills against AI",
                icon = Icons.Filled.SmartToy,
                gradientColors = listOf(Color(0xFF1E88E5), Color(0xFF0D47A1)),
                onClick = onPlayComputer,
                animatedProgress = animatedProgress.value
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ─── 4. Stats Card (Achievements & Win Rate) ───
            AchievementsSection(
                stats = stats,
                animatedProgress = animatedProgress.value
            )

            // Minimal bottom padding to prevent content touching edge
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Settings icon (top-right)
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─── Neon Title with Multi-layered Glow ───
@Composable
private fun NeonTitle(
    animatedProgress: Float,
    modifier: Modifier = Modifier
) {
    val titleText = "Tic Tac Toe"
    val fontSize = 38.sp
    val letterSpacing = 1.5.sp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.graphicsLayer {
            alpha = animatedProgress
            translationY = (1f - animatedProgress) * 25f
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Layer 1: Wide diffused ambient aura
            Text(
                text = titleText,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                letterSpacing = letterSpacing,
                style = TextStyle(
                    color = CoralOrange.copy(alpha = 0.35f),
                    shadow = Shadow(
                        color = CoralOrange,
                        blurRadius = 32f,
                        offset = Offset.Zero
                    )
                )
            )

            // Layer 2: Medium vibrant neon halo
            Text(
                text = titleText,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                letterSpacing = letterSpacing,
                style = TextStyle(
                    color = CoralOrange.copy(alpha = 0.7f),
                    shadow = Shadow(
                        color = Color(0xFFFF7A50),
                        blurRadius = 16f,
                        offset = Offset.Zero
                    )
                )
            )

            // Layer 3: Inner core tube glow
            Text(
                text = titleText,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                letterSpacing = letterSpacing,
                style = TextStyle(
                    color = Color(0xFFFFD1C1),
                    shadow = Shadow(
                        color = Color(0xFFFFE0D6),
                        blurRadius = 6f,
                        offset = Offset.Zero
                    )
                )
            )

            // Layer 4: Sharp foremost neon text
            Text(
                text = titleText,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                letterSpacing = letterSpacing,
                color = CoralOrange,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.White.copy(alpha = 0.85f),
                        blurRadius = 2f,
                        offset = Offset.Zero
                    )
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Classic Board Game",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
            letterSpacing = 1.sp
        )
    }
}

// ─── Achievements / Stats Section ───
@Composable
private fun AchievementsSection(
    stats: StatsManager.GameStats,
    animatedProgress: Float
) {
    val totalGames = stats.wins + stats.losses + stats.draws
    val decisiveGames = stats.wins + stats.losses
    val winRate = if (decisiveGames > 0) (stats.wins * 100f / decisiveGames) else 0f

    // Determine achievement title
    val (achievementTitle, achievementIconColor) = when {
        stats.wins >= 50 -> "Grand Master" to Color(0xFFFFD700)
        stats.wins >= 25 -> "Champion" to Color(0xFFFF9800)
        stats.wins >= 10 -> "Pro Player" to Color(0xFF4CAF50)
        stats.wins >= 5 -> "Rising Star" to Color(0xFF2196F3)
        totalGames > 0 -> "Beginner" to Color(0xFF9E9E9E)
        else -> "New Player" to Color(0xFF9E9E9E)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = animatedProgress
                translationY = (1f - animatedProgress) * 40f
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header: Achievement title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = "Achievement Trophy",
                    tint = achievementIconColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = achievementTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row with colorful circular badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBadge(
                    value = stats.wins,
                    label = "Wins",
                    color = WinGreen,
                    bgColor = Color(0xFFE8F5E9)
                )
                StatBadge(
                    value = stats.losses,
                    label = "Losses",
                    color = LossRed,
                    bgColor = Color(0xFFFFEBEE)
                )
                StatBadge(
                    value = stats.draws,
                    label = "Draws",
                    color = DrawAmber,
                    bgColor = Color(0xFFFFF3E0)
                )
                StatBadge(
                    value = totalGames,
                    label = "Total",
                    color = DeepIndigo,
                    bgColor = Color(0xFFE3F2FD)
                )
            }

            // Win rate progress bar
            if (totalGames > 0) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Win Rate",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${winRate.toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (winRate >= 50f) WinGreen else LossRed
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { winRate / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (winRate >= 50f) WinGreen else CoralOrange,
                    trackColor = LightGray
                )
            }
        }
    }
}

@Composable
private fun StatBadge(value: Int, label: String, color: Color, bgColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$value",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ─── Mode Selection Card ───
@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    animatedProgress: Float
) {
    var isPressed by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .scale(cardScale)
            .graphicsLayer {
                alpha = animatedProgress
                translationX = (1f - animatedProgress) * 100f
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.horizontalGradient(gradientColors))
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}
