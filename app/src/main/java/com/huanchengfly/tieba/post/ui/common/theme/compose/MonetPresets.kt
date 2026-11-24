package com.huanchengfly.tieba.post.ui.common.theme.compose

import androidx.compose.ui.graphics.Color

const val KEY_MONET_PRESET = "monetColorPreset"

enum class MonetPreset(
    val id: String,
    val label: String,
    val swatch1: Color,
    val swatch2: Color,
    val swatch3: Color,
    val darkPrimary: Color,
    val darkAccent: Color,
    val darkCanvas: Color,
    val darkCard: Color,
    val lightPrimary: Color,
    val lightAccent: Color,
    val lightCanvas: Color,
) {
    NONE(
        id = "",
        label = "原色",
        swatch1 = Color(0xFF4A6FA5),
        swatch2 = Color(0xFFB7CBE6),
        swatch3 = Color(0xFFE1E8F2),
        darkPrimary = Color.Unspecified,
        darkAccent = Color.Unspecified,
        darkCanvas = Color.Unspecified,
        darkCard = Color.Unspecified,
        lightPrimary = Color.Unspecified,
        lightAccent = Color.Unspecified,
        lightCanvas = Color.Unspecified,
    ),
    GRAPHITE(
        id = "graphite",
        label = "石墨",
        swatch1 = Color(0xFF3E5872),
        swatch2 = Color(0xFFB9D4EB),
        swatch3 = Color(0xFFDCD8EB),
        darkPrimary = Color(0xFF8CA9C4),
        darkAccent = Color(0xFF9DB4C9),
        darkCanvas = Color(0xFF0E1116),
        darkCard = Color(0xFF161B22),
        lightPrimary = Color(0xFF47627D),
        lightAccent = Color(0xFF5B7894),
        lightCanvas = Color(0xFFF5F6F8),
    ),
    PINE(
        id = "pine",
        label = "雾松",
        swatch1 = Color(0xFF226654),
        swatch2 = Color(0xFF9EE0C5),
        swatch3 = Color(0xFFC6ECF5),
        darkPrimary = Color(0xFF7FC3AE),
        darkAccent = Color(0xFF94CFB9),
        darkCanvas = Color(0xFF0C1311),
        darkCard = Color(0xFF141E1B),
        lightPrimary = Color(0xFF2E6F5E),
        lightAccent = Color(0xFF3F8572),
        lightCanvas = Color(0xFFF4F7F5),
    ),
    SEA(
        id = "sea",
        label = "浅海",
        swatch1 = Color(0xFF24607E),
        swatch2 = Color(0xFFB3E2F7),
        swatch3 = Color(0xFFDCD6FA),
        darkPrimary = Color(0xFF7FB8D6),
        darkAccent = Color(0xFF97C7E0),
        darkCanvas = Color(0xFF0C1217),
        darkCard = Color(0xFF141D25),
        lightPrimary = Color(0xFF2D6A88),
        lightAccent = Color(0xFF3F809F),
        lightCanvas = Color(0xFFF4F7F9),
    ),
    IRIS(
        id = "iris",
        label = "鸢尾",
        swatch1 = Color(0xFF665B8A),
        swatch2 = Color(0xFFDDD3FA),
        swatch3 = Color(0xFFF7D1DA),
        darkPrimary = Color(0xFFA79BC9),
        darkAccent = Color(0xFFB9AED8),
        darkCanvas = Color(0xFF100F16),
        darkCard = Color(0xFF191722),
        lightPrimary = Color(0xFF6B5F8F),
        lightAccent = Color(0xFF7F72A6),
        lightCanvas = Color(0xFFF7F5FA),
    );

    fun applyTo(base: ExtendedColors): ExtendedColors {
        if (this == NONE) return base

        return if (base.isNightMode) {
            val isAmoled = base.background == Color.Black
            val resolvedBg = if (isAmoled) Color.Black else darkCanvas
            base.copy(
                primary = darkPrimary,
                accent = darkAccent,
                background = resolvedBg,
                windowBackground = resolvedBg,
                card = if (isAmoled) Color(0xFF121418) else darkCard,
                floorCard = if (isAmoled) Color(0xFF171A20) else darkCard,
                topBar = resolvedBg,
                bottomBar = resolvedBg,
            )
        } else {
            base.copy(
                primary = lightPrimary,
                accent = lightAccent,
                background = lightCanvas,
                windowBackground = lightCanvas,
                topBar = if (base.topBar == base.primary) lightPrimary else lightCanvas,
                bottomBar = lightCanvas,
            )
        }
    }

    companion object {
        val selectablePresets: List<MonetPreset> = listOf(GRAPHITE, PINE, SEA, IRIS)

        fun fromId(id: String?): MonetPreset = entries.firstOrNull { it.id == id } ?: GRAPHITE
    }
}
