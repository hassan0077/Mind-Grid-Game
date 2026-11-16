package com.hassanaj.mindgrid

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import com.hassanaj.mindgrid.ui.theme.RasheedTheme

enum class Screen {
    SPLASH,
    MENU,
    GAME
}

private const val PREFS_NAME = "memory_grid_prefs"
private const val KEY_SELECTED_DIFFICULTY = "selected_difficulty"
private const val KEY_IS_DARK_THEME = "is_dark_theme"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindGridApp()
        }
    }
}

@Composable
fun MindGridApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    // Restore saved settings or fallback to defaults
    val savedDiffName = prefs.getString(KEY_SELECTED_DIFFICULTY, Difficulty.NORMAL.name) ?: Difficulty.NORMAL.name
    val initialDiff = runCatching { Difficulty.valueOf(savedDiffName) }.getOrDefault(Difficulty.NORMAL)
    val initialDarkTheme = prefs.getBoolean(KEY_IS_DARK_THEME, true)

    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    var selectedDifficulty by remember { mutableStateOf(initialDiff) }
    var isDarkTheme by remember { mutableStateOf(initialDarkTheme) }

    fun setDifficultyAndSave(difficulty: Difficulty) {
        selectedDifficulty = difficulty
        prefs.edit { putString(KEY_SELECTED_DIFFICULTY, difficulty.name) }
    }

    fun setThemeAndSave(dark: Boolean) {
        isDarkTheme = dark
        prefs.edit { putBoolean(KEY_IS_DARK_THEME, dark) }
    }

    RasheedTheme(darkTheme = isDarkTheme) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                when (screen) {
                    Screen.SPLASH -> SplashScreen(
                        modifier = Modifier.padding(innerPadding),
                        isDarkTheme = isDarkTheme,
                        onSplashFinished = { currentScreen = Screen.MENU }
                    )

                    Screen.MENU -> MainMenuScreen(
                        modifier = Modifier.padding(innerPadding),
                        selectedDifficulty = selectedDifficulty,
                        onDifficultySelected = { setDifficultyAndSave(it) },
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { setThemeAndSave(it) },
                        onStartGame = { currentScreen = Screen.GAME }
                    )

                    Screen.GAME -> MemoryGridGameScreen(
                        modifier = Modifier.padding(innerPadding),
                        difficulty = selectedDifficulty,
                        isDarkTheme = isDarkTheme,
                        onBackToMenu = { currentScreen = Screen.MENU }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashPreview() {
    RasheedTheme {
        SplashScreen(isDarkTheme = true)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainMenuPreview() {
    RasheedTheme {
        MainMenuScreen(
            selectedDifficulty = Difficulty.NORMAL,
            onDifficultySelected = {},
            isDarkTheme = true,
            onThemeToggle = {},
            onStartGame = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MemoryGridGamePreview() {
    RasheedTheme {
        MemoryGridGameScreen(
            difficulty = Difficulty.NORMAL,
            isDarkTheme = true,
            onBackToMenu = {}
        )
    }
}
