package com.threewin.tictactoe.features.game.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.threewin.tictactoe.R

@Composable
fun SelectBoardSizeScreen(onSizeSelected: (Int) -> Unit, onBack: () -> Unit, onSettingsClick: () -> Unit) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().padding(8.dp).height(56.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
            IconButton(onClick = onSettingsClick, modifier = Modifier.align(Alignment.CenterEnd)) { Icon(Icons.Default.Settings, contentDescription = null) }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(if (isTablet) 64.dp else 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.select_board_size),
                style = if (isTablet) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(if (isTablet) 48.dp else 32.dp))

            val cardModifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()

            BoardSizeCard(
                label = "3 x 3",
                subtitle = "Classic",
                logoResId = R.drawable.classic_logo,
                modifier = cardModifier,
                onClick = { onSizeSelected(3) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            BoardSizeCard(
                label = "4 x 4",
                subtitle = "Advanced",
                logoResId = R.drawable.advance_level_logo,
                modifier = cardModifier,
                onClick = { onSizeSelected(4) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            BoardSizeCard(
                label = "5 x 5",
                subtitle = "Expert",
                logoResId = R.drawable.expert_logo,
                modifier = cardModifier,
                onClick = { onSizeSelected(5) }
            )
        }
    }
}

@Composable
fun BoardSizeCard(
    label: String,
    subtitle: String,
    logoResId: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val imageSize = if (isTablet) 80.dp else 60.dp

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = if (isTablet) 20.dp else 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = logoResId),
                contentDescription = "$subtitle logo",
                modifier = Modifier
                    .size(imageSize)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = if (isTablet) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

