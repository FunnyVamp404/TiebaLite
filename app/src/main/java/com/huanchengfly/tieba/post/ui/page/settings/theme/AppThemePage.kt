package com.huanchengfly.tieba.post.ui.page.settings.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.huanchengfly.tieba.post.putString
import com.huanchengfly.tieba.post.rememberPreferenceAsState
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.common.theme.compose.MonetPreset
import com.huanchengfly.tieba.post.ui.common.theme.compose.MonetPresetState
import com.huanchengfly.tieba.post.ui.widgets.compose.AnimatedOptionSection
import com.huanchengfly.tieba.post.ui.widgets.compose.BackNavigationIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.MyScaffold
import com.huanchengfly.tieba.post.ui.widgets.compose.Switch
import com.huanchengfly.tieba.post.ui.widgets.compose.TitleCentredToolbar
import com.huanchengfly.tieba.post.ui.widgets.compose.feedBackdropColor
import com.huanchengfly.tieba.post.ui.widgets.compose.feedCardSurface
import com.huanchengfly.tieba.post.utils.ThemeUtil
import com.huanchengfly.tieba.post.utils.extension.toHexString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun AppThemePage(
    navigator: DestinationsNavigator,
) {
    val currentTheme by remember { ThemeUtil.themeState }
    val supportsDynamicTheme = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val useDynamicTheme by rememberPreferenceAsState(
        key = booleanPreferencesKey(ThemeUtil.KEY_USE_DYNAMIC_THEME),
        defaultValue = false
    )
    val isDynamicTheme = supportsDynamicTheme && useDynamicTheme
    val selectedPreset by remember { MonetPresetState.presetState }
    val showSourceColors = !isDynamicTheme && selectedPreset != MonetPreset.NONE

    MyScaffold(
        backgroundColor = feedBackdropColor(),
        topBar = {
            TitleCentredToolbar(
                title = {
                    Text(
                        text = "主题配色",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.h6,
                    )
                },
                eyebrow = "APPEARANCE / MONET",
                navigationIcon = {
                    BackNavigationIcon(onBackPressed = { navigator.navigateUp() })
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                Column {
                    ArchiveSectionHeader(
                        code = "01 / TONAL PALETTE",
                        title = "莫奈配色",
                        subtitle = "浅色与深色自动配套，页面与控件温和保持一致。",
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.selectableGroup(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ThemePill(
                            label = "原色",
                            selected = !isDynamicTheme && selectedPreset == MonetPreset.NONE,
                            onClick = {
                                ThemeUtil.setUseDynamicTheme(false)
                                MonetPresetState.setPreset(MonetPreset.NONE)
                            },
                        )
                        if (supportsDynamicTheme) {
                            ThemePill(
                                label = "系统取色",
                                selected = isDynamicTheme,
                                onClick = {
                                    MonetPresetState.setPreset(MonetPreset.NONE)
                                    ThemeUtil.setUseDynamicTheme(true)
                                },
                            )
                        }
                        ThemePill(
                            label = "自选配色",
                            selected = showSourceColors,
                            onClick = {
                                ThemeUtil.setUseDynamicTheme(false)
                                if (selectedPreset == MonetPreset.NONE) {
                                    MonetPresetState.setPreset(MonetPreset.PINE)
                                }
                            },
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    TonalPalettePreview()
                    AnimatedOptionSection(expanded = showSourceColors) {
                        Column {
                            Spacer(Modifier.height(20.dp))
                            ArchiveSectionHeader(code = "02 / SOURCE COLOR", title = "选择主色")
                            Spacer(Modifier.height(12.dp))
                            Column(
                                modifier = Modifier.selectableGroup(),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                MonetPreset.selectablePresets.chunked(3).forEach { presets ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        presets.forEach { preset ->
                                            PresetSwatch(
                                                modifier = Modifier.weight(1f),
                                                preset = preset,
                                                selected = selectedPreset == preset,
                                                enabled = showSourceColors,
                                                onClick = { MonetPresetState.setPreset(preset) },
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            val colors = ExtendedTheme.colors
                            Text(
                                text = "当前主色 ${colors.primary.toHexString().uppercase()}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = colors.textSecondary,
                            )
                        }
                    }
                }
            }
            item {
                val isNightMode = ThemeUtil.isNightMode(currentTheme)
                val colors = ExtendedTheme.colors
                Column {
                    ArchiveSectionHeader(code = "03 / APPEARANCE MODE", title = "外观模式")
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "深色模式",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.text,
                            )
                            Text(
                                text = "切换夜间底色与浅色外观",
                                fontSize = 12.sp,
                                color = colors.textSecondary,
                            )
                        }
                        Switch(
                            checked = isNightMode,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    ThemeUtil.dataStore.putString(
                                        ThemeUtil.KEY_DARK_THEME,
                                        ThemeUtil.THEME_GREY_DARK,
                                    )
                                    ThemeUtil.switchTheme(ThemeUtil.THEME_GREY_DARK)
                                } else if (ThemeUtil.isNightMode()) {
                                    ThemeUtil.switchNightMode()
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchiveSectionHeader(code: String, title: String, subtitle: String? = null) {
    val colors = ExtendedTheme.colors
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = code,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = colors.textSecondary,
                fontFamily = FontFamily.Monospace,
            )
            Spacer(Modifier.width(8.dp))
            Divider(
                modifier = Modifier.weight(1f),
                thickness = 0.6.dp,
                color = colors.divider,
            )
            Spacer(Modifier.width(6.dp))
            Box(Modifier.size(3.dp).background(colors.text))
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.text,
        )
        if (subtitle != null) {
            Text(text = subtitle, fontSize = 12.sp, color = colors.textSecondary)
        }
    }
}

@Composable
private fun themeHairline(): Color = ExtendedTheme.colors.text.copy(alpha = 0.16f)

@Composable
private fun Modifier.themeChoiceSurface(
    shape: Shape,
    selected: Boolean,
    tintSelected: Boolean = true,
): Modifier {
    val colors = ExtendedTheme.colors
    return clip(shape)
        .background(if (selected && tintSelected) colors.primary.copy(alpha = 0.10f) else Color.Transparent)
        .border(
            width = if (selected) 1.5.dp else 0.75.dp,
            color = if (selected) colors.primary else themeHairline(),
            shape = shape,
        )
}

@Composable
private fun ThemePill(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .themeChoiceSurface(CircleShape, selected)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .heightIn(min = 36.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = ExtendedTheme.colors.text,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TonalPalettePreview() {
    val colors = ExtendedTheme.colors
    val hairline = themeHairline()
    val swatchShape = RoundedCornerShape(4.dp)
    Column(
        modifier = Modifier.fillMaxWidth().feedCardSurface().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "当前色板",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = colors.text,
            )
            Text(
                text = colors.primary.toHexString().uppercase(),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = colors.textSecondary,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            listOf(colors.primary, colors.accent, colors.chip, colors.card, colors.background)
                .forEach { color ->
                    Box(
                        Modifier
                            .weight(1f)
                            .height(26.dp)
                            .background(color, swatchShape)
                            .border(0.5.dp, hairline, swatchShape),
                    )
                }
        }
    }
}

@Composable
private fun PresetSwatch(
    modifier: Modifier,
    preset: MonetPreset,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier.selectable(
            selected = selected,
            enabled = enabled,
            role = Role.RadioButton,
            onClick = onClick,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .themeChoiceSurface(RoundedCornerShape(5.dp), selected, tintSelected = false)
                .padding(4.dp),
        ) {
            Row(modifier = Modifier.clip(RoundedCornerShape(2.dp))) {
                val stripe = Modifier.weight(1f).fillMaxHeight()
                Box(stripe.background(preset.swatch1))
                Box(stripe.background(preset.swatch2))
                Box(stripe.background(preset.swatch3))
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = preset.label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            color = ExtendedTheme.colors.text,
            textAlign = TextAlign.Center,
        )
    }
}
