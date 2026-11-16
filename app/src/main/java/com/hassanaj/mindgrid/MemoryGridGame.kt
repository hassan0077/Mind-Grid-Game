package com.hassanaj.mindgrid

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameState {
    START,
    SHOWING_PATTERN,
    USER_INPUT,
    GAME_OVER
}

// 16 vibrant neon colors for active tiles (up to 4x4 grid)
val tileColors = listOf(
    Color(0xFFFF2A85), // Electric Magenta
    Color(0xFF00F5FF), // Cyan Glow
    Color(0xFF00FF87), // Neon Emerald
    Color(0xFFFFD700), // Cyber Yellow
    Color(0xFFA855F7), // Bright Violet
    Color(0xFFFF6B00), // Neon Orange
    Color(0xFFFF1493), // Hot Pink
    Color(0xFF3B82F6), // Electric Blue
    Color(0xFF10B981), // Spring Mint
    Color(0xFFEC4899), // Deep Pink
    Color(0xFF8B5CF6), // Purple
    Color(0xFF06B6D4), // Cyan
    Color(0xFFF59E0B), // Amber
    Color(0xFFEF4444), // Coral Red
    Color(0xFF14B8A6), // Teal
    Color(0xFF6366F1)  // Indigo
)

private const val PREFS_NAME = "memory_grid_prefs"

@Composable
fun MemoryGridGameScreen(
    modifier: Modifier = Modifier,
    difficulty: Difficulty = Difficulty.NORMAL,
    isDarkTheme: Boolean = true,
    onBackToMenu: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val highScoreKey = "high_score_${difficulty.name}"
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    var highScore by remember(difficulty) { mutableIntStateOf(prefs.getInt(highScoreKey, 0)) }

    var gameState by remember { mutableStateOf(GameState.START) }
    val sequence = remember { mutableStateListOf<Int>() }
    var userStepIndex by remember { mutableIntStateOf(0) }
    var litTileIndex by remember { mutableStateOf<Int?>(null) }
    var wrongTileIndex by remember { mutableStateOf<Int?>(null) }
    var currentScore by remember { mutableIntStateOf(0) }

    fun updateHighScore(newScore: Int) {
        if (newScore > highScore) {
            highScore = newScore
            prefs.edit { putInt(highScoreKey, newScore) }
        }
    }

    fun startNewGame() {
        sequence.clear()
        currentScore = 0
        userStepIndex = 0
        wrongTileIndex = null
        // Initial 2 steps
        sequence.add(Random.nextInt(difficulty.totalTiles))
        sequence.add(Random.nextInt(difficulty.totalTiles))
        gameState = GameState.SHOWING_PATTERN
    }

    fun advanceToNextLevel() {
        currentScore++
        updateHighScore(currentScore)
        userStepIndex = 0
        sequence.add(Random.nextInt(difficulty.totalTiles))
        gameState = GameState.SHOWING_PATTERN
    }

    // Play pattern sequence
    LaunchedEffect(gameState, sequence.size) {
        if (gameState == GameState.SHOWING_PATTERN) {
            delay(400L)
            for (tileIndex in sequence) {
                litTileIndex = tileIndex
                SoundManager.playTileSound(tileIndex)
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                delay(difficulty.flashDelayMs)
                litTileIndex = null
                delay(180L)
            }
            gameState = GameState.USER_INPUT
            userStepIndex = 0
        }
    }

    fun onTileClick(index: Int) {
        if (gameState != GameState.USER_INPUT) return

        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

        if (index == sequence[userStepIndex]) {
            // Correct tap
            SoundManager.playTileSound(index)
            scope.launch {
                litTileIndex = index
                delay(180L)
                if (litTileIndex == index) litTileIndex = null
            }

            userStepIndex++
            if (userStepIndex == sequence.size) {
                SoundManager.playSuccessSound()
                scope.launch {
                    delay(250L)
                    advanceToNextLevel()
                }
            }
        } else {
            // Wrong tap
            SoundManager.playErrorSound()
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            wrongTileIndex = index
            gameState = GameState.GAME_OVER
            updateHighScore(currentScore)
        }
    }

    // Theme Brushes & Colors
    val backgroundBrush = if (isDarkTheme) {
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

    Surface(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Bar
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Back to Menu Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(cardBg)
                                .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                                .clickable { onBackToMenu() }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "◀ MENU",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryTextColor
                            )
                        }

                        // App Title
                        Text(
                            text = "MINDGRID",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp,
                            color = primaryTextColor
                        )

                        // Difficulty Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(primaryTextColor.copy(alpha = 0.2f))
                                .border(1.dp, primaryTextColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = difficulty.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = primaryTextColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Score Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NeonScoreCard(
                            label = "SCORE",
                            value = currentScore.toString(),
                            accentColor = Color(0xFF00FF87),
                            cardBg = cardBg,
                            isDarkTheme = isDarkTheme
                        )
                        NeonScoreCard(
                            label = "BEST (${difficulty.title})",
                            value = highScore.toString(),
                            accentColor = Color(0xFFFFD700),
                            cardBg = cardBg,
                            isDarkTheme = isDarkTheme
                        )
                    }
                }

                // Middle Game Grid Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatusPill(
                        gameState = gameState,
                        sequenceLength = sequence.size,
                        userStep = userStepIndex,
                        cardBg = cardBg
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val dimension = difficulty.gridDimension
                    val tilePadding = if (dimension == 4) 4.dp else 6.dp

                    // Dynamic Grid Frame (2x2, 3x3, or 4x4)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(28.dp))
                            .background(cardBg)
                            .border(
                                width = 1.5.dp,
                                brush = Brush.linearGradient(
                                    listOf(cardBorder, primaryTextColor.copy(alpha = 0.2f))
                                ),
                                shape = RoundedCornerShape(28.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (row in 0 until dimension) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    for (col in 0 until dimension) {
                                        val index = (row * dimension) + col
                                        val isLit = litTileIndex == index
                                        val isWrong = wrongTileIndex == index

                                        NeonGridTile(
                                            index = index,
                                            isLit = isLit,
                                            isWrong = isWrong,
                                            isEnabled = gameState == GameState.USER_INPUT,
                                            isDarkTheme = isDarkTheme,
                                            onClick = { onTileClick(index) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(tilePadding)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Action Buttons Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (gameState) {
                        GameState.START -> {
                            GradientActionButton(
                                text = "▶ START GAME",
                                gradient = Brush.horizontalGradient(
                                    listOf(Color(0xFF8B5CF6), Color(0xFF06B6D4))
                                ),
                                onClick = { startNewGame() }
                            )
                        }

                        GameState.GAME_OVER -> {
                            GradientActionButton(
                                text = "🔄 PLAY AGAIN",
                                gradient = Brush.horizontalGradient(
                                    listOf(Color(0xFFF43F5E), Color(0xFFFB923C))
                                ),
                                onClick = { startNewGame() }
                            )
                        }

                        GameState.SHOWING_PATTERN, GameState.USER_INPUT -> {
                            Button(
                                onClick = { startNewGame() },
                                modifier = Modifier
                                    .fillMaxWidth(0.55f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.dp, primaryTextColor.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = cardBg,
                                    contentColor = primaryTextColor
                                )
                            ) {
                                Text(
                                    text = "🔄 RESTART",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NeonScoreCard(
    label: String,
    value: String,
    accentColor: Color,
    cardBg: Color,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        modifier = modifier.padding(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = if (isDarkTheme) Color.White else Color(0xFF0F172A)
            )
        }
    }
}

@Composable
fun StatusPill(
    gameState: GameState,
    sequenceLength: Int,
    userStep: Int,
    cardBg: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val (statusText, accentColor, showPulse) = when (gameState) {
        GameState.START -> Triple("TAP START TO BEGIN", Color(0xFF38BDF8), false)
        GameState.SHOWING_PATTERN -> Triple("WATCH PATTERN", Color(0xFF00F5FF), true)
        GameState.USER_INPUT -> Triple("YOUR TURN ($userStep / $sequenceLength)", Color(0xFF00FF87), false)
        GameState.GAME_OVER -> Triple("GAME OVER", Color(0xFFFF2A85), false)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(cardBg)
            .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(30.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (showPulse) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .alpha(pulseAlpha)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = statusText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = accentColor
            )
        }
    }
}

@Composable
fun NeonGridTile(
    index: Int,
    isLit: Boolean,
    isWrong: Boolean,
    isEnabled: Boolean,
    isDarkTheme: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tileAccent = tileColors[index % tileColors.size]
    val darkBaseColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)

    val targetColor = when {
        isWrong -> Color(0xFFFF2A85)
        isLit -> tileAccent
        else -> darkBaseColor
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "tileColorAnimation"
    )

    val scaleFactor by animateFloatAsState(
        targetValue = if (isLit) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tileScaleAnimation"
    )

    val borderStroke = when {
        isWrong -> BorderStroke(2.dp, Color(0xFFFF2A85))
        isLit -> BorderStroke(2.dp, Color.White)
        else -> BorderStroke(1.dp, if (isDarkTheme) Color(0xFF334155) else Color(0xFFCBD5E1))
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = animatedColor),
        border = borderStroke,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isLit) 16.dp else 2.dp
        ),
        modifier = modifier
            .scale(scaleFactor)
            .clickable(enabled = isEnabled && !isLit) { onClick() }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLit) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                )
            }
        }
    }
}

@Composable
fun GradientActionButton(
    text: String,
    gradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(0.82f)
            .height(54.dp)
            .clip(RoundedCornerShape(27.dp))
            .background(gradient)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            color = Color.White
        )
    }
}
