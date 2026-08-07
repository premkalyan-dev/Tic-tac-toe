package com.threewin.tictactoe.features.game.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.threewin.tictactoe.R
import com.threewin.tictactoe.domain.model.GameMode

@Composable
fun StartScreen(onModeSelected: (GameMode) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Image(painter = painterResource(id = R.drawable.app_logo_new), contentDescription = null, modifier = Modifier.size(200.dp).padding(bottom = 32.dp), contentScale = ContentScale.Fit)
        Text(text = stringResource(id = R.string.app_name), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = { onModeSelected(GameMode.VS_COMPUTER) }, modifier = Modifier.fillMaxWidth().height(64.dp), shape = RoundedCornerShape(16.dp)) {
            Icon(Icons.Default.SmartToy, contentDescription = null); Spacer(modifier = Modifier.width(12.dp))
            Text(stringResource(id = R.string.mode_vs_computer), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = { onModeSelected(GameMode.VS_FRIEND) }, modifier = Modifier.fillMaxWidth().height(64.dp), shape = RoundedCornerShape(16.dp)) {
            Icon(Icons.Default.Group, contentDescription = null); Spacer(modifier = Modifier.width(12.dp))
            Text(stringResource(id = R.string.mode_vs_friend), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}
