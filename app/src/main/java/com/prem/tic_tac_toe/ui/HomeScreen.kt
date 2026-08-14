package com.prem.tic_tac_toe.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SmartToy
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prem.tic_tac_toe.R
import com.prem.tic_tac_toe.ui.theme.*

@Composable
fun HomeScreen(
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

    // Subtle floating animation for logo
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.8f))

            // Animated Logo
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Three Win Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .graphicsLayer {
                        translationY = floatOffset
                        scaleX = animatedProgress.value
                        scaleY = animatedProgress.value
                    }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // App Title with gradient
            Text(
                text = "Three Win",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CoralOrange,
                modifier = Modifier.graphicsLayer {
                    alpha = animatedProgress.value
                    translationY = (1f - animatedProgress.value) * 30f
                }
            )

            Text(
                text = "Tic Tac Toe",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = DeepIndigo,
                modifier = Modifier.graphicsLayer {
                    alpha = animatedProgress.value
                    translationY = (1f - animatedProgress.value) * 20f
                }
            )

            Spacer(modifier = Modifier.weight(0.6f))

            // Choose how to play label
            Text(
                text = "Choose how to play",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .graphicsLayer {
                        alpha = animatedProgress.value
                    }
            )

            // Mode Selection Cards
            ModeCard(
                title = "Play with Friend",
                subtitle = "Challenge a local player",
                icon = Icons.Filled.People,
                gradientColors = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32)),
                onClick = onPlayFriend,
                animationDelay = 0,
                animatedProgress = animatedProgress.value
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModeCard(
                title = "Play with Computer",
                subtitle = "Test your skills against AI",
                icon = Icons.Filled.SmartToy,
                gradientColors = listOf(Color(0xFF2196F3), Color(0xFF1565C0)),
                onClick = onPlayComputer,
                animationDelay = 100,
                animatedProgress = animatedProgress.value
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        // Settings icon
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    animationDelay: Int,
    animatedProgress: Float
) {
    // Press animation
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
            .height(88.dp)
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
                .background(
                    brush = Brush.horizontalGradient(gradientColors)
                )
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}
