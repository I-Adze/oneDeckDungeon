package com.example.onedeckdungeon

import androidx.compose.ui.graphics.Color

enum class DieColor(val displayName: String, val displayColor: Color) {
    MAGENTA("Agility", Color.Magenta),
    YELLOW("Strength", Color.Yellow),
    BLUE("Magic", Color.Blue),
    BLACK("Heroic", Color.Black);
}