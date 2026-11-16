# Keep Jetpack Compose models & views
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Keep SharedPreferences key strings and GameSettings enums
-keepclassmembers enum com.hassanaj.mindgrid.** { *; }
