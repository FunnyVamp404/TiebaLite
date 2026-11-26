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
    val darkChip: Color,
    val lightPrimary: Color,
    val lightAccent: Color,
    val lightCanvas: Color,
    val lightCard: Color,
    val lightChip: Color,
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
        darkChip = Color.Unspecified,
        lightPrimary = Color.Unspecified,
        lightAccent = Color.Unspecified,
        lightCanvas = Color.Unspecified,
        lightCard = Color.Unspecified,
        lightChip = Color.Unspecified,
    ),
    PINE(
        id = "pine",
        label = "雾松",
        swatch1 = Color(0xFF1F6652),
        swatch2 = Color(0xFF9BE0C4),
        swatch3 = Color(0xFFC4ECF6),
        darkPrimary = Color(0xFF6DD4B0),
        darkAccent = Color(0xFF8CE0C3),
        darkCanvas = Color(0xFF0D1B17),
        darkCard = Color(0xFF162C25),
        darkChip = Color(0xFF1F3D34),
        lightPrimary = Color(0xFF236B58),
        lightAccent = Color(0xFF35826D),
        lightCanvas = Color(0xFFEFF6F3),
        lightCard = Color(0xFFF7FBF9),
        lightChip = Color(0xFFDCECE5),
    ),
    SEA(
        id = "sea",
        label = "浅海",
        swatch1 = Color(0xFF225E7D),
        swatch2 = Color(0xFFB0E0F7),
        swatch3 = Color(0xFFDAD4FA),
        darkPrimary = Color(0xFF6EC0EB),
        darkAccent = Color(0xFF90D1F2),
        darkCanvas = Color(0xFF0C1924),
        darkCard = Color(0xFF14293B),
        darkChip = Color(0xFF1C3952),
        lightPrimary = Color(0xFF246282),
        lightAccent = Color(0xFF387A9C),
        lightCanvas = Color(0xFFEEF5FA),
        lightCard = Color(0xFFF7FBFE),
        lightChip = Color(0xFFDBEAF5),
    ),
    IRIS(
        id = "iris",
        label = "鸢尾",
        swatch1 = Color(0xFF65598A),
        swatch2 = Color(0xFFDDD3FA),
        swatch3 = Color(0xFFF8D0DA),
        darkPrimary = Color(0xFFB8A4EC),
        darkAccent = Color(0xFFCEBEF5),
        darkCanvas = Color(0xFF171326),
        darkCard = Color(0xFF241E3C),
        darkChip = Color(0xFF322952),
        lightPrimary = Color(0xFF665B8A),
        lightAccent = Color(0xFF7D70A6),
        lightCanvas = Color(0xFFF5F2FB),
        lightCard = Color(0xFFFAF8FE),
        lightChip = Color(0xFFE7E1F6),
    ),
    ROUGE(
        id = "rouge",
        label = "胭脂",
        swatch1 = Color(0xFF8A4A58),
        swatch2 = Color(0xFFF7CFD8),
        swatch3 = Color(0xFFFCE0C8),
        darkPrimary = Color(0xFFE39BAA),
        darkAccent = Color(0xFFF0B7C4),
        darkCanvas = Color(0xFF1E1318),
        darkCard = Color(0xFF301E26),
        darkChip = Color(0xFF422934),
        lightPrimary = Color(0xFF8A4A58),
        lightAccent = Color(0xFFA35D6C),
        lightCanvas = Color(0xFFFBF2F4),
        lightCard = Color(0xFFFFF8F9),
        lightChip = Color(0xFFF4E0E5),
    ),
    WHEAT(
        id = "wheat",
        label = "麦穗",
        swatch1 = Color(0xFF7D6324),
        swatch2 = Color(0xFFF4DC92),
        swatch3 = Color(0xFFCCE8C4),
        darkPrimary = Color(0xFFDEC078),
        darkAccent = Color(0xFFECD49A),
        darkCanvas = Color(0xFF1B1810),
        darkCard = Color(0xFF2C2619),
        darkChip = Color(0xFF3D3422),
        lightPrimary = Color(0xFF826626),
        lightAccent = Color(0xFF9C7D38),
        lightCanvas = Color(0xFFFAF6EC),
        lightCard = Color(0xFFFFFDF7),
        lightChip = Color(0xFFF0E7D1),
    ),
    GRAPHITE(
        id = "graphite",
        label = "石墨",
        swatch1 = Color(0xFF3A5673),
        swatch2 = Color(0xFFB9D4EB),
        swatch3 = Color(0xFFDCD8EB),
        darkPrimary = Color(0xFF90B2D4),
        darkAccent = Color(0xFFA8C4E0),
        darkCanvas = Color(0xFF10151C),
        darkCard = Color(0xFF1A222E),
        darkChip = Color(0xFF253040),
        lightPrimary = Color(0xFF3E5872),
        lightAccent = Color(0xFF53718E),
        lightCanvas = Color(0xFFF2F5F8),
        lightCard = Color(0xFFF9FBFD),
        lightChip = Color(0xFFE0E7EF),
    );

    fun applyTo(base: ExtendedColors): ExtendedColors {
        if (this == NONE) return base

        return if (base.isNightMode) {
            base.copy(
                primary = darkPrimary,
                accent = darkAccent,
                background = darkCanvas,
                windowBackground = darkCanvas,
                card = darkCard,
                floorCard = darkCard,
                topBar = darkCanvas,
                topBarSurface = darkChip,
                bottomBar = darkCanvas,
                bottomBarSurface = darkCard,
                chip = darkChip,
                divider = darkPrimary.copy(alpha = 0.20f),
                indicator = darkCard,
            )
        } else {
            base.copy(
                primary = lightPrimary,
                accent = lightAccent,
                background = lightCanvas,
                windowBackground = lightCanvas,
                card = lightCard,
                floorCard = lightChip,
                topBar = if (base.topBar == base.primary) lightPrimary else lightCanvas,
                topBarSurface = lightChip,
                bottomBar = lightCanvas,
                bottomBarSurface = lightCard,
                chip = lightChip,
                divider = lightPrimary.copy(alpha = 0.16f),
                indicator = lightCard,
            )
        }
    }

    companion object {
        val selectablePresets: List<MonetPreset> = listOf(PINE, SEA, IRIS, ROUGE, WHEAT, GRAPHITE)

        fun fromId(id: String?): MonetPreset = entries.firstOrNull { it.id == id } ?: GRAPHITE
    }
}
