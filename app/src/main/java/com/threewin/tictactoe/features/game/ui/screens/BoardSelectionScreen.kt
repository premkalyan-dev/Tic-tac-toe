package com.threewin.tictactoe.features.game.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            modifier = Modifier.fillMaxWidth().weight(1f).padding(if (isTablet) 64.dp else 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.select_board_size),
                style = if (isTablet) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(if (isTablet) 64.dp else 48.dp))
            
            val buttonModifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
            
            BoardSizeButton(label = "3 x 3", modifier = buttonModifier, onClick = { onSizeSelected(3) })
            Spacer(modifier = Modifier.height(16.dp))
            BoardSizeButton(label = "4 x 4", modifier = buttonModifier, onClick = { onSizeSelected(4) })
            Spacer(modifier = Modifier.height(16.dp))
            BoardSizeButton(label = "5 x 5", modifier = buttonModifier, onClick = { onSizeSelected(5) })
        }
    }
}

@Composable
fun BoardSizeButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    
    Button(
        onClick = onClick,
        modifier = modifier.height(if (isTablet) 88.dp else 72.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(text = label, style = if (isTablet) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    }
}
