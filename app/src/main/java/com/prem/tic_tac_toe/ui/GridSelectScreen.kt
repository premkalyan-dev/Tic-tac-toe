package com.prem.tic_tac_toe.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
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
                title = { },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Header
            Text(
                text = "Choose Board Size",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.graphicsLayer {
                    alpha = animatedProgress.value
                    translationY = (1f - animatedProgress.value) * 30f
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Pick your challenge level",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.graphicsLayer {
                    alpha = animatedProgress.value
                    translationY = (1f - animatedProgress.value) * 20f
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3×3 Classic
            BoardCard(
                gridSize = 3,
                badgeLabel = "Classic",
                badgeColor = Level3x3Color,
                starCount = 1,
                maxStars = 3,
                sizeLabel = "3 × 3",
                description = "The original game —\nperfect for quick matches",
                boardColors = BoardCardColors(
                    bgGradient = listOf(Color(0xFF1A237E), Color(0xFF0D47A1)),
                    xColor = XColor,
                    oColor = OColor,
                    gridLineColor = Color(0xFF00E5FF)
                ),
                onClick = { onGridSelected(3) },
                animatedProgress = animatedProgress.value,
                delayFactor = 0f
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4×4 Advanced
            BoardCard(
                gridSize = 4,
                badgeLabel = "Advanced",
                badgeColor = Level4x4Color,
                starCount = 2,
                maxStars = 3,
                sizeLabel = "4 × 4",
                description = "More cells, more strategy\n— think ahead",
                boardColors = BoardCardColors(
                    bgGradient = listOf(Color(0xFF1A237E), Color(0xFF283593)),
                    xColor = Color(0xFFFF5252),
                    oColor = Color(0xFF69F0AE),
                    gridLineColor = Color(0xFFFFD740)
                ),
                onClick = { onGridSelected(4) },
                animatedProgress = animatedProgress.value,
                delayFactor = 0.1f
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5×5 Expert
            BoardCard(
                gridSize = 5,
                badgeLabel = "Expert",
                badgeColor = Color(0xFFF44336),
                starCount = 3,
                maxStars = 3,
                sizeLabel = "5 × 5",
                description = "Maximum complexity —\nfor true masters only",
                boardColors = BoardCardColors(
                    bgGradient = listOf(Color(0xFF4A148C), Color(0xFF7B1FA2)),
                    xColor = Color(0xFFFF80AB),
                    oColor = Color(0xFFFFD740),
                    gridLineColor = Color(0xFFE040FB)
                ),
                onClick = { onGridSelected(5) },
                animatedProgress = animatedProgress.value,
                delayFactor = 0.2f
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─── Data class for board icon colors ───
private data class BoardCardColors(
    val bgGradient: List<Color>,
    val xColor: Color,
    val oColor: Color,
    val gridLineColor: Color
)

// ─── Board Selection Card (matching reference design) ───
@Composable
private fun BoardCard(
    gridSize: Int,
    badgeLabel: String,
    badgeColor: Color,
    starCount: Int,
    maxStars: Int,
    sizeLabel: String,
    description: String,
    boardColors: BoardCardColors,
    onClick: () -> Unit,
    animatedProgress: Float,
    delayFactor: Float
) {
    val effectiveProgress = ((animatedProgress - delayFactor) / (1f - delayFactor)).coerceIn(0f, 1f)

    var isPressed by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
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
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Board icon
            BoardIcon(
                gridSize = gridSize,
                colors = boardColors,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                // Badge + Stars row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = badgeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = badgeLabel,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Stars
                    Row {
                        for (i in 1..maxStars) {
                            Icon(
                                imageVector = if (i <= starCount) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = null,
                                tint = if (i <= starCount) Color(0xFFFFB300) else Color(0xFFE0E0E0),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Grid size label
                Text(
                    text = sizeLabel,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Description
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }

            // Chevron arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Select",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// ─── Canvas-drawn board icon ───
@Composable
private fun BoardIcon(
    gridSize: Int,
    colors: BoardCardColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(brush = Brush.linearGradient(colors.bgGradient))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            val cellSize = size.width / gridSize
            val lineWidth = 1.5f

            // Draw grid lines
            for (i in 1 until gridSize) {
                // Vertical lines
                drawLine(
                    color = colors.gridLineColor.copy(alpha = 0.6f),
                    start = Offset(i * cellSize, 0f),
                    end = Offset(i * cellSize, size.height),
                    strokeWidth = lineWidth
                )
                // Horizontal lines
                drawLine(
                    color = colors.gridLineColor.copy(alpha = 0.6f),
                    start = Offset(0f, i * cellSize),
                    end = Offset(size.width, i * cellSize),
                    strokeWidth = lineWidth
                )
            }

            // Draw some X and O marks to make it look like a game board
            val markPadding = cellSize * 0.25f
            val markStroke = 2.5f

            // Predefined mark positions based on grid size
            val xPositions: List<Pair<Int, Int>>
            val oPositions: List<Pair<Int, Int>>

            when (gridSize) {
                3 -> {
                    xPositions = listOf(0 to 0, 1 to 1, 2 to 0)
                    oPositions = listOf(0 to 1, 1 to 0, 2 to 2)
                }
                4 -> {
                    xPositions = listOf(0 to 0, 1 to 2, 2 to 1, 3 to 3)
                    oPositions = listOf(0 to 2, 1 to 1, 2 to 3, 3 to 0)
                }
                5 -> {
                    xPositions = listOf(0 to 0, 1 to 3, 2 to 2, 3 to 1, 4 to 4)
                    oPositions = listOf(0 to 4, 1 to 1, 2 to 0, 3 to 3, 4 to 2)
                }
                else -> {
                    xPositions = emptyList()
                    oPositions = emptyList()
                }
            }

            // Draw X marks
            for ((row, col) in xPositions) {
                val cx = col * cellSize + cellSize / 2
                val cy = row * cellSize + cellSize / 2
                val half = cellSize / 2 - markPadding
                drawLine(
                    color = colors.xColor,
                    start = Offset(cx - half, cy - half),
                    end = Offset(cx + half, cy + half),
                    strokeWidth = markStroke,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = colors.xColor,
                    start = Offset(cx + half, cy - half),
                    end = Offset(cx - half, cy + half),
                    strokeWidth = markStroke,
                    cap = StrokeCap.Round
                )
            }

            // Draw O marks
            for ((row, col) in oPositions) {
                val cx = col * cellSize + cellSize / 2
                val cy = row * cellSize + cellSize / 2
                val radius = cellSize / 2 - markPadding
                drawCircle(
                    color = colors.oColor,
                    radius = radius,
                    center = Offset(cx, cy),
                    style = Stroke(width = markStroke)
                )
            }
        }
    }
}
