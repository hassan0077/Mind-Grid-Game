package com.hassanaj.mindgrid

enum class Difficulty(
    val title: String,
    val gridDimension: Int, // 2 for 2x2, 3 for 3x3, 4 for 4x4
    val flashDelayMs: Long,
    val description: String
) {
    EASY("EASY", 2, 600L, "2x2 Grid • Relaxed Pace"),
    NORMAL("NORMAL", 3, 450L, "3x3 Grid • Standard Pace"),
    HARD("HARD", 4, 300L, "4x4 Grid • Fast & Intense");

    val totalTiles: Int
        get() = gridDimension * gridDimension
}
