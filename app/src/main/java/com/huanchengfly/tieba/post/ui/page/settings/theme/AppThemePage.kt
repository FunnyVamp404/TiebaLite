package com.huanchengfly.tieba.post.ui.page.settings.theme

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BorderColor
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.godaddy.android.colorpicker.HsvColor
import com.godaddy.android.colorpicker.harmony.ColorHarmonyMode
import com.godaddy.android.colorpicker.harmony.HarmonyColorPicker
import com.huanchengfly.tieba.post.App
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.activities.TranslucentThemeActivity
import com.huanchengfly.tieba.post.components.dialogs.CustomThemeDialog
import com.huanchengfly.tieba.post.goToActivity
import com.huanchengfly.tieba.post.rememberPreferenceAsMutableState
import com.huanchengfly.tieba.post.rememberPreferenceAsState
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.common.theme.compose.MonetPreset
import com.huanchengfly.tieba.post.ui.common.theme.compose.MonetPresetState
import com.huanchengfly.tieba.post.ui.widgets.compose.BackNavigationIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.Dialog
import com.huanchengfly.tieba.post.ui.widgets.compose.DialogNegativeButton
import com.huanchengfly.tieba.post.ui.widgets.compose.DialogPositiveButton
import com.huanchengfly.tieba.post.ui.widgets.compose.MyScaffold
import com.huanchengfly.tieba.post.ui.widgets.compose.TitleCentredToolbar
import com.huanchengfly.tieba.post.ui.widgets.compose.feedCardSurface
import com.huanchengfly.tieba.post.ui.widgets.compose.rememberDialogState
import com.huanchengfly.tieba.post.utils.ThemeUtil
import com.huanchengfly.tieba.post.utils.appPreferences
import com.huanchengfly.tieba.post.utils.extension.toHexString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun AppThemePage(
    navigator: DestinationsNavigator,
) {
    val context = LocalContext.current
    val currentTheme by remember { ThemeUtil.themeState }
    val supportsDynamicTheme = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val useDynamicTheme by rememberPreferenceAsState(
        key = booleanPreferencesKey(ThemeUtil.KEY_USE_DYNAMIC_THEME),
        defaultValue = false
    )
    val isDynamicTheme = supportsDynamicTheme && useDynamicTheme
    val selectedPreset by remember { MonetPresetState.presetState }
    val customPrimaryColorDialogState = rememberDialogState()
    var customPrimaryColor by remember {
        mutableStateOf(
            Color(
                App.ThemeDelegate.getColorByAttr(
                    context,
                    R.attr.colorPrimary,
                    ThemeUtil.THEME_CUSTOM
                )
            )
        )
    }
    var customToolbarPrimaryColor by rememberPreferenceAsMutableState(
        key = booleanPreferencesKey(
            ThemeUtil.KEY_CUSTOM_TOOLBAR_PRIMARY_COLOR
        ),
        defaultValue = false
    )
    var customStatusBarFontDark by rememberPreferenceAsMutableState(
        key = booleanPreferencesKey(
            ThemeUtil.KEY_CUSTOM_STATUS_BAR_FONT_DARK
        ),
        defaultValue = false
    )

    Dialog(
        dialogState = customPrimaryColorDialogState,
        title = { Text(text = stringResource(id = R.string.title_custom_theme)) },
        buttons = {
            DialogPositiveButton(
                text = stringResource(id = R.string.button_finish),
                onClick = {
                    customStatusBarFontDark = customStatusBarFontDark || !customToolbarPrimaryColor
                    context.appPreferences.customPrimaryColor =
                        CustomThemeDialog.toString(customPrimaryColor.toArgb())
                    context.appPreferences.toolbarPrimaryColor = customToolbarPrimaryColor
                    context.appPreferences.customStatusBarFontDark = customStatusBarFontDark
                    ThemeUtil.setUseDynamicTheme(false)
                    MonetPresetState.setPreset(MonetPreset.NONE)
                    ThemeUtil.switchTheme(ThemeUtil.THEME_CUSTOM)
                }
            )
            DialogNegativeButton(
                text = stringResource(id = R.string.button_cancel),
                onClick = {
                    customPrimaryColor = Color(
                        App.ThemeDelegate.getColorByAttr(
                            context,
                            R.attr.colorPrimary,
                            ThemeUtil.THEME_CUSTOM
                        )
                    )
                }
            )
        }
    ) {
        var useInput by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedContent(
                targetState = useInput,
                label = "",
                modifier = Modifier
                    .wrapContentHeight()
                    .animateContentSize()
            ) { input ->
                if (input) {
                    var inputHexColor by remember { mutableStateOf(customPrimaryColor.toHexString()) }
                    val lastValidColor by produceState(
                        initialValue = customPrimaryColor,
                        inputHexColor
                    ) {
                        if ("^#([0-9a-fA-F]{6})$".toRegex().matches(inputHexColor)) {
                            value = Color(inputHexColor.toColorInt())
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(lastValidColor)
                        )
                        OutlinedTextField(
                            value = inputHexColor,
                            onValueChange = {
                                if ("^#([0-9a-fA-F]{0,6})$".toRegex().matches(it)) {
                                    inputHexColor = it
                                }
                            },
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                cursorColor = ExtendedTheme.colors.primary,
                                focusedBorderColor = ExtendedTheme.colors.primary,
                                focusedLabelColor = ExtendedTheme.colors.primary
                            )
                        )
                        IconButton(
                            onClick = {
                                customPrimaryColor = Color(inputHexColor.toColorInt())
                                useInput = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = stringResource(id = R.string.button_sure_default)
                            )
                        }
                    }
                } else {
                    Box {
                        HarmonyColorPicker(
                            harmonyMode = ColorHarmonyMode.ANALOGOUS,
                            color = HsvColor.from(customPrimaryColor),
                            onColorChanged = {
                                customPrimaryColor = it.toColor()
                            },
                            modifier = Modifier.sizeIn(maxWidth = 320.dp, maxHeight = 320.dp)
                        )

                        IconButton(
                            onClick = { useInput = true },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.BorderColor,
                                contentDescription = stringResource(id = R.string.desc_input_color)
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            customToolbarPrimaryColor =
                                !customToolbarPrimaryColor
                        }
                    )
            ) {
                Checkbox(
                    checked = customToolbarPrimaryColor,
                    onCheckedChange = {
                        customToolbarPrimaryColor = it
                    },
                )
                Text(text = stringResource(id = R.string.tip_toolbar_primary_color))
            }

            if (customToolbarPrimaryColor) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                customStatusBarFontDark =
                                    !customStatusBarFontDark
                            }
                        )
                ) {
                    Checkbox(
                        checked = customStatusBarFontDark,
                        onCheckedChange = {
                            customStatusBarFontDark = it
                        },
                    )
                    Text(text = stringResource(id = R.string.tip_status_bar_font))
                }
            }
        }
    }

    MyScaffold(
        backgroundColor = ExtendedTheme.colors.background,
        topBar = {
            TitleCentredToolbar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_theme),
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                ThemeSection(
                    eyebrow = "01 / TONAL PALETTE",
                    title = "莫奈配色",
                    subtitle = "浅色与深色自动配套，页面与控件温和保持一致",
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().selectableGroup(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ThemePill(
                            modifier = Modifier.weight(1f),
                            label = "原色",
                            selected = !isDynamicTheme && selectedPreset == MonetPreset.NONE,
                            onClick = {
                                ThemeUtil.setUseDynamicTheme(false)
                                MonetPresetState.setPreset(MonetPreset.NONE)
                            },
                        )
                        if (supportsDynamicTheme) {
                            ThemePill(
                                modifier = Modifier.weight(1f),
                                label = "系统取色",
                                selected = isDynamicTheme,
                                onClick = {
                                    MonetPresetState.setPreset(MonetPreset.NONE)
                                    ThemeUtil.setUseDynamicTheme(true)
                                },
                            )
                        }
                        ThemePill(
                            modifier = Modifier.weight(1f),
                            label = "莫奈配色",
                            selected = !isDynamicTheme && selectedPreset != MonetPreset.NONE,
                            onClick = {
                                ThemeUtil.setUseDynamicTheme(false)
                                if (selectedPreset == MonetPreset.NONE) {
                                    MonetPresetState.setPreset(MonetPreset.PINE)
                                }
                            },
                        )
                    }
                    TonalPalettePreview()
                }
            }
            item {
                ThemeSection(
                    eyebrow = "02 / SOURCE COLOR",
                    title = "选择主色",
                    subtitle = "6 套低饱和东方莫奈色板，点击即刻生效",
                ) {
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
                                        selected = !isDynamicTheme && selectedPreset == preset,
                                        onClick = {
                                            ThemeUtil.setUseDynamicTheme(false)
                                            MonetPresetState.setPreset(preset)
                                        },
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        ThemeTile(
                            modifier = Modifier.weight(1f),
                            label = "自选颜色",
                            previewColor = customPrimaryColor,
                            selected = !isDynamicTheme && currentTheme == ThemeUtil.THEME_CUSTOM,
                            onClick = { customPrimaryColorDialogState.show() },
                            isAction = true,
                        )
                        ThemeTile(
                            modifier = Modifier.weight(1f),
                            label = "半透明壁纸",
                            previewColor = ExtendedTheme.colors.primary.copy(alpha = 0.28f),
                            selected = ThemeUtil.isTranslucentTheme(currentTheme),
                            onClick = { context.goToActivity<TranslucentThemeActivity>() },
                            isAction = true,
                            previewShape = RoundedCornerShape(3.dp),
                        )
                    }
                }
            }
            item {
                ThemeSection(
                    eyebrow = "03 / APPEARANCE MODE",
                    title = "深浅与经典外观",
                ) {
                    val darkThemes = listOf(
                        Triple(ThemeUtil.THEME_BLUE_DARK, "静谧蓝", colorResource(R.color.theme_color_background_blue_dark)),
                        Triple(ThemeUtil.THEME_GREY_DARK, "深邃灰", colorResource(R.color.theme_color_background_grey_dark)),
                        Triple(ThemeUtil.THEME_AMOLED_DARK, "纯黑", colorResource(R.color.theme_color_background_amoled_dark)),
                    )
                    ThemeModeGroup(
                        title = "深色模式（可叠加莫奈配色）",
                        themes = darkThemes,
                        selectedTheme = currentTheme.takeUnless { isDynamicTheme },
                        onThemeSelected = { value ->
                            ThemeUtil.setUseDynamicTheme(false)
                            ThemeUtil.switchTheme(value)
                        },
                    )

                    val classicThemes = listOf(
                        Triple(ThemeUtil.THEME_DEFAULT, "深海蓝", colorResource(R.color.theme_color_primary_tieba)),
                        Triple(ThemeUtil.THEME_BLUE, "清新蓝", colorResource(R.color.theme_color_primary_blue)),
                        Triple(ThemeUtil.THEME_BLACK, "高级黑", colorResource(R.color.theme_color_primary_black)),
                        Triple(ThemeUtil.THEME_PINK, "少女粉", colorResource(R.color.theme_color_primary_pink)),
                        Triple(ThemeUtil.THEME_RED, "新年红", colorResource(R.color.theme_color_primary_red)),
                        Triple(ThemeUtil.THEME_PURPLE, "星空紫", colorResource(R.color.theme_color_primary_purple)),
                    )
                    ThemeModeGroup(
                        title = "经典浅色主题（切换后展示原色）",
                        themes = classicThemes,
                        selectedTheme = currentTheme.takeUnless { isDynamicTheme },
                        onThemeSelected = { value ->
                            ThemeUtil.setUseDynamicTheme(false)
                            MonetPresetState.setPreset(MonetPreset.NONE)
                            ThemeUtil.switchTheme(value)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun themeHairline(): Color = ExtendedTheme.colors.text.copy(alpha = 0.16f)

@Composable
private fun ThemeSection(
    eyebrow: String,
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = ExtendedTheme.colors
    Column(
        modifier = Modifier.fillMaxWidth().feedCardSurface().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = eyebrow,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = colors.textSecondary,
            )
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = colors.text,
            )
            if (subtitle != null) {
                Text(text = subtitle, fontSize = 12.sp, color = colors.textSecondary)
            }
        }
        content()
    }
}

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
private fun ThemePill(
    modifier: Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .themeChoiceSurface(CircleShape, selected)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .heightIn(min = 48.dp)
            .padding(horizontal = 4.dp, vertical = 8.dp),
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
    val shape = RoundedCornerShape(10.dp)
    val swatchShape = RoundedCornerShape(6.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background.copy(alpha = 0.45f), shape)
            .border(0.75.dp, hairline, shape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "贴吧，自有它的颜色。",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = colors.text,
            )
            Text(
                text = "页面、玻璃与控件，温和地保持一致。",
                fontSize = 12.sp,
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
                            .height(28.dp)
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
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .themeChoiceSurface(RoundedCornerShape(10.dp), selected, tintSelected = false)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(4.dp)
                .clip(RoundedCornerShape(6.dp)),
        ) {
            val stripe = Modifier.weight(1f).fillMaxHeight()
            Box(stripe.background(preset.swatch1))
            Box(stripe.background(preset.swatch2))
            Box(stripe.background(preset.swatch3))
        }
        Text(
            text = preset.label,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            color = ExtendedTheme.colors.text,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ThemeModeGroup(
    title: String,
    themes: List<Triple<String, String, Color>>,
    selectedTheme: String?,
    onThemeSelected: (String) -> Unit,
) {
    Column(
        modifier = Modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = ExtendedTheme.colors.textSecondary,
        )
        themes.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { (value, label, color) ->
                    ThemeTile(
                        modifier = Modifier.weight(1f),
                        label = label,
                        previewColor = color,
                        selected = selectedTheme == value,
                        onClick = { onThemeSelected(value) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeTile(
    modifier: Modifier,
    label: String,
    previewColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
    isAction: Boolean = false,
    previewShape: Shape = CircleShape,
) {
    val shape = RoundedCornerShape(if (isAction) 10.dp else 8.dp)
    Row(
        modifier = modifier
            .themeChoiceSurface(shape, selected)
            .selectable(
                selected = selected,
                role = if (isAction) Role.Button else Role.RadioButton,
                onClick = onClick,
            )
            .heightIn(min = 48.dp)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    ) {
        Box(
            Modifier
                .size(if (isAction) 12.dp else 14.dp)
                .background(previewColor, previewShape)
                .border(0.75.dp, themeHairline(), previewShape),
        )
        Text(
            text = label,
            color = ExtendedTheme.colors.text,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
