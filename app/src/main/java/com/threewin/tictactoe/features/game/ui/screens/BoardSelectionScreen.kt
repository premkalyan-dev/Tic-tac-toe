package com.threewin.tictactoe.features.game.ui.screens

import androidx.compose.animation.core.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.threewin.tictactoe.R

@Composable
fun SelectBoardSizeScreen(onSizeSelected: (Int) -> Unit, onBack: () -> Unit, onSettingsClick: () -> Unit) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    // Entrance animations
    val headerAlpha = remember { Animatable(0f) }
    val card1Offset = remember { Animatable(60f) }
    val card2Offset = remember { Animatable(60f) }
    val card3Offset = remember { Animatable(60f) }
    val cardsAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            headerAlpha.animateTo(1f, animationSpec = tween(400))
        }
        launch {
            cardsAlpha.animateTo(1f, animationSpec = tween(500))
        }
        launch {
            delay(100)
            card1Offset.animateTo(0f, animationSpec = spring(stiffness = Spring.StiffnessLow))
        }
        launch {
            delay(220)
            card2Offset.animateTo(0f, animationSpec = spring(stiffness = Spring.StiffnessLow))
        }
        launch {
            delay(340)
            card3Offset.animateTo(0f, animationSpec = spring(stiffness = Spring.StiffnessLow))
        }
    }

    // Background gradient
    val gradientColors = listOf(
        MaterialTheme.colorScheme.background,
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f),
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
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Box(modifier = Modifier.fillMaxWidth().padding(8.dp).height(56.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                IconButton(onClick = onSettingsClick, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = if (isTablet) 64.dp else 24.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(0.2f))

                // Header
                Text(
                    text = stringResource(id = R.string.select_board_size),
                    style = if (isTablet) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.graphicsLayer(alpha = headerAlpha.value)
                )
                Text(
                    text = "Pick your challenge level",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(top = 6.dp, bottom = if (isTablet) 48.dp else 32.dp)
                        .graphicsLayer(alpha = headerAlpha.value)
                )

                val cardModifier = Modifier.widthIn(max = 440.dp).fillMaxWidth()

                // Classic 3x3
                BoardSizeCard(
                    label = "3 × 3",
                    subtitle = "Classic",
                    description = "The original game — perfect for quick matches",
                    logoResId = R.drawable.classic_logo,
                    difficultyStars = 1,
                    accentColor = Color(0xFF4CAF50),
                    modifier = cardModifier.graphicsLayer(
                        alpha = cardsAlpha.value,
                        translationY = card1Offset.value
                    ),
                    onClick = { onSizeSelected(3) }
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Advanced 4x4
                BoardSizeCard(
                    label = "4 × 4",
                    subtitle = "Advanced",
                    description = "More cells, more strategy — think ahead",
                    logoResId = R.drawable.advance_level_logo,
                    difficultyStars = 2,
                    accentColor = Color(0xFFFF9800),
                    modifier = cardModifier.graphicsLayer(
                        alpha = cardsAlpha.value,
                        translationY = card2Offset.value
                    ),
                    onClick = { onSizeSelected(4) }
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Expert 5x5
                BoardSizeCard(
                    label = "5 × 5",
                    subtitle = "Expert",
                    description = "Maximum complexity — for true masters only",
                    logoResId = R.drawable.expert_logo,
                    difficultyStars = 3,
                    accentColor = Color(0xFFF44336),
                    modifier = cardModifier.graphicsLayer(
                        alpha = cardsAlpha.value,
                        translationY = card3Offset.value
                    ),
                    onClick = { onSizeSelected(5) }
                )

                Spacer(modifier = Modifier.weight(0.3f))
            }
        }
    }
}

@Composable
fun BoardSizeCard(
    label: String,
    subtitle: String,
    description: String,
    logoResId: Int,
    difficultyStars: Int,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val imageSize = if (isTablet) 76.dp else 60.dp

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = if (isTablet) 20.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Logo with accent ring
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(imageSize + 8.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(accentColor.copy(alpha = 0.12f))
                )
                Image(
                    painter = painterResource(id = logoResId),
                    contentDescription = "$subtitle logo",
                    modifier = Modifier
                        .size(imageSize)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                // Subtitle badge + stars row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Difficulty badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accentColor.copy(alpha = 0.15f),
                        modifier = Modifier
                    ) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Stars
                    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                        repeat(3) { index ->
                            Icon(
                                imageVector = if (index < difficultyStars) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (index < difficultyStars) accentColor else MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Board size label
                Text(
                    text = label,
                    style = if (isTablet) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Description
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                    lineHeight = 16.sp
                )
            }

            // Chevron
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

