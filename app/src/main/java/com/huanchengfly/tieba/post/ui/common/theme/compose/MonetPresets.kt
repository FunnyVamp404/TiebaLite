package com.huanchengfly.tieba.post.ui.common.theme.compose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.huanchengfly.tieba.post.App
import com.huanchengfly.tieba.post.dataStore
import com.huanchengfly.tieba.post.getString
import com.huanchengfly.tieba.post.putString

const val KEY_MONET_PRESET = "monetColorPreset"

object MonetPresetState {
    val presetState: MutableState<MonetPreset> by lazy {
        mutableStateOf(
            MonetPreset.fromId(App.INSTANCE.dataStore.getString(KEY_MONET_PRESET))
        )
    }

    fun setPreset(preset: MonetPreset) {
        presetState.value = preset
        App.INSTANCE.dataStore.putString(KEY_MONET_PRESET, preset.id)
    }
}

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
        darkPrimary = Color(0xFF92B0CC),
        darkAccent = Color(0xFFA4BDD4),
        darkCanvas = Color(0xFF0F1217),
        darkCard = Color(0xFF171C24),
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
        darkPrimary = Color(0xFF7AC7AE),
        darkAccent = Color(0xFF91D4BD),
        darkCanvas = Color(0xFF0D1614),
        darkCard = Color(0xFF15231F),
        lightPrimary = Color(0xFF2E6F5E),
        lightAccent = Color(0xFF3F8572),
        lightCanvas = Color(0xFFF3F7F5),
    ),
    SEA(
        id = "sea",
        label = "浅海",
        swatch1 = Color(0xFF24607E),
        swatch2 = Color(0xFFB3E2F7),
        swatch3 = Color(0xFFDCD6FA),
        darkPrimary = Color(0xFF78B9DC),
        darkAccent = Color(0xFF92C9E6),
        darkCanvas = Color(0xFF0C151D),
        darkCard = Color(0xFF14222E),
        lightPrimary = Color(0xFF2D6A88),
        lightAccent = Color(0xFF3F809F),
        lightCanvas = Color(0xFFF3F7FA),
    ),
    IRIS(
        id = "iris",
        label = "鸢尾",
        swatch1 = Color(0xFF665B8A),
        swatch2 = Color(0xFFDDD3FA),
        swatch3 = Color(0xFFF7D1DA),
        darkPrimary = Color(0xFFB0A0D8),
        darkAccent = Color(0xFFC2B4E3),
        darkCanvas = Color(0xFF13101B),
        darkCard = Color(0xFF1E192B),
        lightPrimary = Color(0xFF6B5F8F),
        lightAccent = Color(0xFF7F72A6),
        lightCanvas = Color(0xFFF7F5FB),
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
