package com.prem.tic_tac_toe.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                        text = modeTitle,
                        fontWeight = FontWeight.SemiBold
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            Text(
                text = "Choose Your Grid",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.graphicsLayer {
                    alpha = animatedProgress.value
                }
            )

            Text(
                text = "Select a grid size to start playing",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 32.dp)
                    .graphicsLayer {
                        alpha = animatedProgress.value
                    }
            )

            // 3x3 Classic
            GridLevelCard(
                gridSize = 3,
                title = "3 × 3  Classic",
                subtitle = "The Original",
                difficulty = "Easy",
                gradientColors = listOf(Level3x3Color, Color(0xFF2E7D32)),
                onClick = { onGridSelected(3) },
                animatedProgress = animatedProgress.value,
                delayFactor = 0f
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4x4 Challenge
            GridLevelCard(
                gridSize = 4,
                title = "4 × 4  Challenge",
                subtitle = "Step It Up",
                difficulty = "Medium",
                gradientColors = listOf(Level4x4Color, Color(0xFFE65100)),
                onClick = { onGridSelected(4) },
                animatedProgress = animatedProgress.value,
                delayFactor = 0.1f
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5x5 Master
            GridLevelCard(
                gridSize = 5,
                title = "5 × 5  Master",
                subtitle = "Ultimate Battle",
                difficulty = "Hard",
                gradientColors = listOf(Level5x5Color, Color(0xFF6A1B9A)),
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
    difficulty: String,
    gradientColors: List<Color>,
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
            .height(100.dp)
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
                .background(
                    brush = Brush.horizontalGradient(gradientColors)
                )
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini grid preview
            MiniGridPreview(
                gridSize = gridSize,
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            // Difficulty badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.25f)
            ) {
                Text(
                    text = difficulty,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
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
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(4.dp)
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
