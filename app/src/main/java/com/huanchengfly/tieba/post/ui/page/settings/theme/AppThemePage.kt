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
import androidx.compose.material.Divider
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.fetch.newResourceUri
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
import com.huanchengfly.tieba.post.ui.common.theme.compose.dynamicTonalPalette
import com.huanchengfly.tieba.post.ui.widgets.compose.BackNavigationIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.Dialog
import com.huanchengfly.tieba.post.ui.widgets.compose.DialogNegativeButton
import com.huanchengfly.tieba.post.ui.widgets.compose.DialogPositiveButton
import com.huanchengfly.tieba.post.ui.widgets.compose.MyScaffold
import com.huanchengfly.tieba.post.ui.widgets.compose.TitleCentredToolbar
import com.huanchengfly.tieba.post.ui.widgets.compose.feedBackdropColor
import com.huanchengfly.tieba.post.ui.widgets.compose.feedCardSurface
import com.huanchengfly.tieba.post.ui.widgets.compose.rememberDialogState
import com.huanchengfly.tieba.post.utils.ThemeUtil
import com.huanchengfly.tieba.post.utils.appPreferences
import com.huanchengfly.tieba.post.utils.extension.toHexString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private const val THEME_WHITE = "white"

@Destination
@Composable
fun AppThemePage(
    navigator: DestinationsNavigator,
) {
    val context = LocalContext.current
    val themeValues = stringArrayResource(id = R.array.theme_values)
    val themeNames = stringArrayResource(id = R.array.themeNames)
    val currentTheme by remember { ThemeUtil.themeState }
    val isDynamicTheme by rememberPreferenceAsState(
        key = booleanPreferencesKey(ThemeUtil.KEY_USE_DYNAMIC_THEME),
        defaultValue = false
    )
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
                    ThemeUtil.switchTheme(ThemeUtil.THEME_CUSTOM)
                    MonetPresetState.setPreset(MonetPreset.NONE)
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
        backgroundColor = feedBackdropColor(),
        topBar = {
            TitleCentredToolbar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_theme),
                        fontWeight = FontWeight.Bold, style = MaterialTheme.typography.h6
                    )
                },
                navigationIcon = {
                    BackNavigationIcon(onBackPressed = { navigator.navigateUp() })
                }
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                MonetPaletteSection(
                    selectedPreset = selectedPreset,
                    onPresetSelected = { preset ->
                        if (preset != MonetPreset.NONE) {
                            ThemeUtil.setUseDynamicTheme(false)
                        }
                        MonetPresetState.setPreset(preset)
                    },
                )
            }
            item {
                EditorialSectionCard(
                    eyebrow = "02 / BASE APPEARANCE",
                    title = "基础外观",
                    subtitle = "深色模式可与上方莫奈配色自由叠加",
                ) {
                    val (nightThemes, lightThemes) = themeValues.zip(themeNames)
                        .partition { (value, _) -> ThemeUtil.isNightMode(value) }
                    Column(
                        modifier = Modifier.selectableGroup(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        val rows = nightThemes.chunked(2) + lightThemes.chunked(2)
                        rows.forEach { themes ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                themes.forEach { (value, name) ->
                                    val isNightTheme = ThemeUtil.isNightMode(value)
                                    val previewColor = remember(context, value) {
                                        val colorAttr = if (isNightTheme) {
                                            R.attr.colorBackground
                                        } else {
                                            R.attr.colorNewPrimary
                                        }
                                        Color(App.ThemeDelegate.getColorByAttr(context, colorAttr, value))
                                    }
                                    BaseThemeTile(
                                        modifier = Modifier.weight(1f),
                                        name = name,
                                        previewColor = previewColor,
                                        selected = !isDynamicTheme && currentTheme == value,
                                        onClick = {
                                            ThemeUtil.setUseDynamicTheme(false)
                                            ThemeUtil.switchTheme(value)
                                            if (!isNightTheme && value != THEME_WHITE &&
                                                value != ThemeUtil.THEME_DEFAULT
                                            ) {
                                                MonetPresetState.setPreset(MonetPreset.NONE)
                                            }
                                        },
                                    )
                                }
                                if (themes.size == 1) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
            item {
                EditorialSectionCard(
                    eyebrow = "03 / PERSONALIZATION",
                    title = "进阶外观",
                    subtitle = "系统动态取色、自定义主色与壁纸透明主题",
                ) {
                    val hairline = editorialHairline()
                    Column {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val tonalPalette = remember(context) { dynamicTonalPalette(context) }
                            PersonalizationRow(
                                title = stringResource(id = R.string.title_dynamic_theme),
                                subtitle = "跟随系统壁纸取色（将暂停莫奈预设）",
                                selected = isDynamicTheme,
                                onClick = {
                                    MonetPresetState.setPreset(MonetPreset.NONE)
                                    ThemeUtil.setUseDynamicTheme(true)
                                },
                                preview = {
                                    Box(
                                        Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.sweepGradient(
                                                    colors = listOf(
                                                        tonalPalette.primary50,
                                                        tonalPalette.secondary50,
                                                        tonalPalette.tertiary50,
                                                        tonalPalette.primary50,
                                                    ),
                                                ),
                                            )
                                            .border(0.75.dp, hairline, CircleShape),
                                    )
                                },
                            )
                            Divider(color = hairline, thickness = 0.5.dp)
                        }
                        PersonalizationRow(
                            title = stringResource(id = R.string.title_custom_color),
                            subtitle = "自由挑选主色调与顶栏配色",
                            selected = !isDynamicTheme && currentTheme == ThemeUtil.THEME_CUSTOM,
                            onClick = { customPrimaryColorDialogState.show() },
                            preview = {
                                Box(
                                    Modifier
                                        .size(28.dp)
                                        .background(customPrimaryColor, CircleShape)
                                        .border(0.75.dp, hairline, CircleShape),
                                )
                            },
                        )
                        Divider(color = hairline, thickness = 0.5.dp)
                        PersonalizationRow(
                            title = stringResource(id = R.string.title_theme_translucent),
                            subtitle = "使用自定义图片作为应用背景",
                            selected = ThemeUtil.isTranslucentTheme(currentTheme),
                            onClick = { context.goToActivity<TranslucentThemeActivity>() },
                            preview = {
                                val shape = RoundedCornerShape(7.dp)
                                val previewImageUri = context.appPreferences.translucentThemeBackgroundPath
                                    ?.let { newFileUri(it) } ?: newResourceUri(R.drawable.user_header)
                                AsyncImage(
                                    imageUri = previewImageUri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(shape)
                                        .border(0.75.dp, hairline, shape),
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun editorialHairline(): Color =
    if (ExtendedTheme.colors.isNightMode) Color.White.copy(alpha = 0.10f)
    else Color.Black.copy(alpha = 0.08f)

@Composable
private fun EditorialSectionCard(
    eyebrow: String,
    title: String,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = ExtendedTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .feedCardSurface()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = eyebrow,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = colors.textSecondary,
            )
            Box(Modifier.weight(1f).height(0.5.dp).background(editorialHairline()))
            Box(Modifier.size(4.dp).background(colors.primary, CircleShape))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = colors.text,
            )
            trailing?.invoke()
        }
        subtitle?.let {
            Text(text = it, fontSize = 12.sp, color = colors.textSecondary)
        }
        Spacer(Modifier.height(4.dp))
        content()
    }
}

@Composable
private fun MonetPaletteSection(
    selectedPreset: MonetPreset,
    onPresetSelected: (MonetPreset) -> Unit,
) {
    val colors = ExtendedTheme.colors
    val hairline = editorialHairline()
    val originalSelected = selectedPreset == MonetPreset.NONE
    val originalBackground = if (originalSelected) {
        colors.primary.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }
    val swatchCardShape = RoundedCornerShape(10.dp)

    Box(Modifier.selectableGroup()) {
        EditorialSectionCard(
            eyebrow = "01 / MONET PALETTE",
            title = "莫奈配色",
            subtitle = "低饱和东方色板，深色与浅色自动配套",
            trailing = {
                Text(
                    text = "恢复原色",
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(originalBackground)
                        .border(0.75.dp, if (originalSelected) colors.primary else hairline, CircleShape)
                        .selectable(
                            selected = originalSelected,
                            role = Role.RadioButton,
                            onClick = { onPresetSelected(MonetPreset.NONE) },
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    color = if (originalSelected) colors.primary else colors.textSecondary,
                )
            },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MonetPreset.selectablePresets.chunked(2).forEach { presets ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        presets.forEach { preset ->
                            val selected = selectedPreset == preset
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(swatchCardShape)
                                    .border(
                                        width = if (selected) 1.5.dp else 0.75.dp,
                                        color = if (selected) colors.primary else hairline,
                                        shape = swatchCardShape,
                                    )
                                    .selectable(
                                        selected = selected,
                                        role = Role.RadioButton,
                                        onClick = { onPresetSelected(preset) },
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
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
                                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp),
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                                    color = colors.text,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BaseThemeTile(
    modifier: Modifier,
    name: String,
    previewColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = ExtendedTheme.colors
    val hairline = editorialHairline()
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(if (selected) colors.primary.copy(alpha = 0.08f) else Color.Transparent)
            .border(
                width = if (selected) 1.5.dp else 0.75.dp,
                color = if (selected) colors.primary else hairline,
                shape = shape,
            )
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier
                .size(20.dp)
                .background(previewColor, CircleShape)
                .border(0.75.dp, hairline, CircleShape),
        )
        Text(
            text = name,
            modifier = Modifier.weight(1f),
            color = colors.text,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (selected) {
            ThemeSelectionCheck()
        }
    }
}

@Composable
private fun PersonalizationRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    preview: @Composable () -> Unit,
) {
    val colors = ExtendedTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, role = Role.Button, onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        preview()
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = colors.text,
            )
            Text(text = subtitle, fontSize = 12.sp, color = colors.textSecondary)
        }
        if (selected) {
            ThemeSelectionCheck()
        }
    }
}

@Composable
private fun ThemeSelectionCheck() {
    Icon(
        imageVector = Icons.Rounded.Check,
        contentDescription = null,
        tint = ExtendedTheme.colors.primary,
        modifier = Modifier.size(14.dp),
    )
}
