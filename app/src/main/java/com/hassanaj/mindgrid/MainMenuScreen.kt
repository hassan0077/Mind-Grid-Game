package com.hassanaj.mindgrid

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val PREFS_NAME = "memory_grid_prefs"

@Composable
fun MainMenuScreen(
    selectedDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    var showHowToPlayDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }

    val bgBrush = if (isDarkTheme) {
        Brush.radialGradient(
            colors = listOf(
                Color(0xFF1E1B4B),
                Color(0xFF0F172A),
                Color(0xFF020617)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF1F5F9),
                Color(0xFFE2E8F0),
                Color(0xFFCBD5E1)
            )
        )
    }

    val primaryTextColor = if (isDarkTheme) Color(0xFF38BDF8) else Color(0xFF0284C7)
    val cardBg = if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.7f) else Color.White.copy(alpha = 0.85f)
    val cardBorder = if (isDarkTheme) Color(0xFF38BDF8).copy(alpha = 0.3f) else Color(0xFF0284C7).copy(alpha = 0.3f)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val subtitleColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)

    Surface(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgBrush)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = "MINDGRID",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 8.sp,
                        color = primaryTextColor
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "MEMORIZE • REPEAT • CONQUER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = subtitleColor
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Play Button
                GradientActionButton(
                    text = "▶ PLAY GAME",
                    gradient = Brush.horizontalGradient(
                        listOf(Color(0xFF8B5CF6), Color(0xFF06B6D4))
                    ),
                    onClick = onStartGame
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Options Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Option 1: Difficulty Modes
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, cardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "1. DIFFICULTY MODE",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = primaryTextColor
                                )

                                val bestForDiff = prefs.getInt("high_score_${selectedDifficulty.name}", 0)
                                Text(
                                    text = "BEST: $bestForDiff",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFFD700)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Difficulty.entries.forEach { diff ->
                                    val isSelected = diff == selectedDifficulty
                                    val chipBg = if (isSelected) {
                                        if (isDarkTheme) Color(0xFF38BDF8) else Color(0xFF0284C7)
                                    } else {
                                        if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF1F5F9)
                                    }
                                    val chipText = if (isSelected) Color.White else textColor

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(chipBg)
                                            .clickable { onDifficultySelected(diff) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = diff.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = chipText
                                            )
                                            Text(
                                                text = "${diff.gridDimension}x${diff.gridDimension}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = chipText.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Option 2: Theme Option (Light / Dark)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, cardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "2. THEME OPTION",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = primaryTextColor
                                )
                                Text(
                                    text = if (isDarkTheme) "Cyber Dark Mode" else "Modern Light Mode",
                                    fontSize = 11.sp,
                                    color = subtitleColor
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFE2E8F0))
                                    .padding(4.dp)
                            ) {
                                ThemeChip(
                                    title = "🌙 DARK",
                                    isSelected = isDarkTheme,
                                    onClick = { onThemeToggle(true) }
                                )
                                ThemeChip(
                                    title = "☀️ LIGHT",
                                    isSelected = !isDarkTheme,
                                    onClick = { onThemeToggle(false) }
                                )
                            }
                        }
                    }

                    // Option 3 & 4: How to Play & Privacy Policy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MenuOptionCard(
                            title = "3. HOW TO PLAY",
                            subtitle = "Learn Rules",
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            textColor = textColor,
                            modifier = Modifier.weight(1f),
                            onClick = { showHowToPlayDialog = true }
                        )

                        MenuOptionCard(
                            title = "4. PRIVACY POLICY",
                            subtitle = "Data Info",
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            textColor = textColor,
                            modifier = Modifier.weight(1f),
                            onClick = { showPrivacyPolicyDialog = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Footer
                Text(
                    text = "v1.0 • MindGrid Studio",
                    fontSize = 11.sp,
                    color = subtitleColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // How to Play Dialog
    if (showHowToPlayDialog) {
        AlertDialog(
            onDismissRequest = { showHowToPlayDialog = false },
            title = {
                Text(
                    text = "🎮 How to Play",
                    fontWeight = FontWeight.Bold,
                    color = primaryTextColor
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("1. Watch the flashing tiles carefully in sequence.")
                    Text("2. Once 'YOUR TURN' appears, tap the exact same tiles in order.")
                    Text("3. Each completed level adds +1 new tile to the sequence.")
                    Text("4. One wrong tap ends the game. Challenge yourself to beat your High Score!")
                }
            },
            confirmButton = {
                TextButton(onClick = { showHowToPlayDialog = false }) {
                    Text("GOT IT!", fontWeight = FontWeight.Bold, color = primaryTextColor)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyPolicyDialog) {
        val privacyPolicyUrl = "https://sites.google.com/view/rasheeddevelopers-privacy"

        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            title = {
                Text(
                    text = "🔒 Privacy Policy",
                    fontWeight = FontWeight.Bold,
                    color = primaryTextColor
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("• MindGrid is a 100% offline puzzle memory game.")
                    Text("• We DO NOT collect, store, or share any personal user data.")
                    Text("• High scores and settings are stored locally on your device only.")
                    Text("• No internet connection or account creation is required to play.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyPolicyDialog = false }) {
                    Text("CLOSE", fontWeight = FontWeight.Bold, color = primaryTextColor)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                        context.startActivity(intent)
                    }
                ) {
                    Text("🌐 READ ONLINE", fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
        )
    }
}

@Composable
fun ThemeChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFF0284C7) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Gray
        )
    }
}

@Composable
fun MenuOptionCard(
    title: String,
    subtitle: String,
    cardBg: Color,
    cardBorder: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
