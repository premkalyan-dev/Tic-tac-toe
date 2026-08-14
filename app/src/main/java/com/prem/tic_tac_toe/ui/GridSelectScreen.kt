package com.prem.tic_tac_toe.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prem.tic_tac_toe.R
import com.prem.tic_tac_toe.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridSelectScreen(
    gameMode: GameMode,
    onGridSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    val modeTitle = when (gameMode) {
        GameMode.VS_FRIEND -> "Playing with Friend"
        GameMode.VS_COMPUTER -> "Playing with Computer"
    }

    val modeEmoji = when (gameMode) {
        GameMode.VS_FRIEND -> "👫"
        GameMode.VS_COMPUTER -> "🤖"
    }

    // Entrance animation
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = EaseOutBack)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "$modeEmoji  $modeTitle",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            // Logo at top
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .shadow(8.dp, RoundedCornerShape(18.dp))
                    .graphicsLayer {
                        scaleX = animatedProgress.value
                        scaleY = animatedProgress.value
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Choose Your Grid",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.graphicsLayer { alpha = animatedProgress.value }
            )

            Text(
                text = "Select a grid size to start playing",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 28.dp)
                    .graphicsLayer { alpha = animatedProgress.value }
            )

            Spacer(modifier = Modifier.weight(0.2f))

            // 3x3 Classic
            GridLevelCard(
                gridSize = 3,
                title = "3 × 3  Classic",
                subtitle = "The Original • Quick Fun",
                starCount = 1,
                maxStars = 3,
                gradientColors = listOf(Color(0xFF66BB6A), Color(0xFF2E7D32)),
                accentColor = Level3x3Color,
                onClick = { onGridSelected(3) },
                animatedProgress = animatedProgress.value,
                delayFactor = 0f
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4x4 Challenge
            GridLevelCard(
                gridSize = 4,
                title = "4 × 4  Challenge",
                subtitle = "Step It Up • More Strategy",
                starCount = 2,
                maxStars = 3,
                gradientColors = listOf(Color(0xFFFFA726), Color(0xFFE65100)),
                accentColor = Level4x4Color,
                onClick = { onGridSelected(4) },
                animatedProgress = animatedProgress.value,
                delayFactor = 0.1f
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5x5 Master
            GridLevelCard(
                gridSize = 5,
                title = "5 × 5  Master",
                subtitle = "Ultimate Battle • Brain Teaser",
                starCount = 3,
                maxStars = 3,
                gradientColors = listOf(Color(0xFFAB47BC), Color(0xFF6A1B9A)),
                accentColor = Level5x5Color,
                onClick = { onGridSelected(5) },
                animatedProgress = animatedProgress.value,
                delayFactor = 0.2f
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun GridLevelCard(
    gridSize: Int,
    title: String,
    subtitle: String,
    starCount: Int,
    maxStars: Int,
    gradientColors: List<Color>,
    accentColor: Color,
    onClick: () -> Unit,
    animatedProgress: Float,
    delayFactor: Float
) {
    val effectiveProgress = ((animatedProgress - delayFactor) / (1f - delayFactor)).coerceIn(0f, 1f)

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
            .height(110.dp)
            .scale(cardScale)
            .graphicsLayer {
                alpha = effectiveProgress
                translationY = (1f - effectiveProgress) * 60f
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
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini grid preview
            MiniGridPreview(
                gridSize = gridSize,
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Difficulty stars
                Row {
                    for (i in 1..maxStars) {
                        Icon(
                            imageVector = if (i <= starCount) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = if (i <= starCount) Color(0xFFFFD700) else Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                        if (i < maxStars) Spacer(modifier = Modifier.width(2.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (starCount) {
                            1 -> "Easy"
                            2 -> "Medium"
                            else -> "Hard"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFD700)
                    )
                }
            }

            // Grid size badge
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "${gridSize}×${gridSize}",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun MiniGridPreview(gridSize: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            for (row in 0 until gridSize) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0 until gridSize) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(1.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        }
    }
}
