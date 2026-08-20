package com.prem.tic_tac_toe.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val onlinePolicyUrl = "https://splendorous-daffodil-fb1cdf.netlify.app/"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            Text(
                text = "Privacy Policy for Three Win (Tic-Tac-Toe)",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Effective Date: August 17, 2026",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(onlinePolicyUrl))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Official Policy Page")
            }

            Spacer(modifier = Modifier.height(16.dp))

            PolicySection(
                title = "1. Information Collection and Use",
                body = "Three Win is a fully offline game. We respect your privacy and do not collect, store, or share any personal user data.\n\nThe app does not require an internet connection, does not contain user accounts, and does not transmit any information to external servers."
            )
            
            PolicySection(
                title = "2. Local Data Storage",
                body = "Game statistics (such as the number of wins, losses, and draws) are saved locally on your device using Android's SharedPreferences. This data is stored solely for the purpose of tracking your progress and is never transmitted off your device. You can clear this data at any time by using the \"Reset Stats\" button in the app's settings."
            )
            
            PolicySection(
                title = "3. Device Permissions",
                body = "The app requests the VIBRATE permission. This is used strictly to provide haptic feedback (vibrations) during gameplay when you tap on the game board."
            )
            
            PolicySection(
                title = "4. Third-Party Services and Advertising",
                body = "Three Win does not contain any advertisements and does not use third-party analytics or tracking SDKs."
            )
            
            PolicySection(
                title = "5. Changes to This Privacy Policy",
                body = "We may update this Privacy Policy from time to time. If any changes are made, we will update the \"Effective Date\" at the top of this policy."
            )
            
            PolicySection(
                title = "6. Contact Us",
                body = "If you have any questions or suggestions about our Privacy Policy, do not hesitate to contact us at:\npremkalyan2727@gmail.com"
            )
        }
    }
}

@Composable
private fun PolicySection(title: String, body: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = body,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(16.dp))
}
