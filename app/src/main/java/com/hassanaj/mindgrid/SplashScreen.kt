package com.hassanaj.mindgrid

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true,
    onSplashFinished: () -> Unit = {}
) {
    val scale = remember { Animatable(0.2f) }
    val alpha = remember { Animatable(0f) }

    // Pulsing background glow transition
    val infiniteTransition = rememberInfiniteTransition(label = "splashPulse")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    LaunchedEffect(key1 = true) {
        // Animate scale and alpha together
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        alpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 800)
        )

        // Hold splash screen briefly before transitioning
        delay(1800L)
        onSplashFinished()
    }

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
    val subtitleColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)

    Surface(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Background Glowing Circle behind logo
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .scale(glowScale)
                            .clip(CircleShape)
                            .background(primaryTextColor.copy(alpha = 0.15f))
                    )

                    // App Logo with Scale & Fade Animations
                    Image(
                        painter = painterResource(id = R.drawable.applogo),
                        contentDescription = "MindGrid App Logo",
                        modifier = Modifier
                            .size(160.dp)
                            .scale(scale.value)
                            .alpha(alpha.value)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Title & Subtitle with Fade Animation
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.alpha(alpha.value)
                ) {
                    Text(
                        text = "MINDGRID",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 8.sp,
                        color = primaryTextColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "MEMORIZE • REPEAT • CONQUER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = subtitleColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
