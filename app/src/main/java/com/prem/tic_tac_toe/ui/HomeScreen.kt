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
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prem.tic_tac_toe.data.StatsManager
import com.prem.tic_tac_toe.ui.theme.*


@Composable
fun HomeScreen(
    stats: StatsManager.GameStats,
    onPlayFriend: () -> Unit,
    onPlayComputer: () -> Unit,
    onSettingsClick: () -> Unit,
    bannerAd: @Composable () -> Unit = {}
) {
    // Entrance animation
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = EaseOutBack)
        )
    }

    // Get greeting based on time of day
    val (greeting, greetingIcon) = remember {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good Morning!" to Icons.Filled.WbSunny
            hour < 17 -> "Good Afternoon!" to Icons.Filled.WbTwilight
            else -> "Good Evening!" to Icons.Filled.NightsStay
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
      Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Welcome Greeting
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.graphicsLayer {
                    alpha = animatedProgress.value
                    translationY = (1f - animatedProgress.value) * 30f
                }
            ) {
                Icon(
                    imageVector = greetingIcon,
                    contentDescription = null,
                    tint = CoralOrange,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = greeting,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // App Title
            Text(
                text = "Three Win",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CoralOrange,
                modifier = Modifier.graphicsLayer {
                    alpha = animatedProgress.value
                    translationY = (1f - animatedProgress.value) * 20f
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.graphicsLayer {
                    alpha = animatedProgress.value
                    translationY = (1f - animatedProgress.value) * 15f
                }
            ) {
                Text(
                    text = "Ready to play? Let's go!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.SportsEsports,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ─── Achievements / Stats Section ───
            AchievementsSection(
                stats = stats,
                animatedProgress = animatedProgress.value
            )


            Spacer(modifier = Modifier.height(20.dp))

            // ─── Play Mode Section ───
            Text(
                text = "Choose how to play",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(bottom = 14.dp)
                    .graphicsLayer { alpha = animatedProgress.value }
            )

            ModeCard(
                title = "Play with Friend",
                subtitle = "Challenge a local player",
                icon = Icons.Filled.People,
                gradientColors = listOf(Color(0xFF43A047), Color(0xFF1B5E20)),
                onClick = onPlayFriend,
                animatedProgress = animatedProgress.value
            )

            Spacer(modifier = Modifier.height(14.dp))

            ModeCard(
                title = "Play with Computer",
                subtitle = "Test your skills against AI",
                icon = Icons.Filled.SmartToy,
                gradientColors = listOf(Color(0xFF1E88E5), Color(0xFF0D47A1)),
                onClick = onPlayComputer,
                animatedProgress = animatedProgress.value
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Settings icon
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

        // Banner Ad Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            bannerAd()
        }
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
                    contentDescription = null,
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
