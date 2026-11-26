package com.huanchengfly.tieba.post.ui.page.settings.custom

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.activities.AppFontSizeActivity
import com.huanchengfly.tieba.post.dataStore
import com.huanchengfly.tieba.post.goToActivity
import com.huanchengfly.tieba.post.ui.common.prefs.PrefsScreen
import com.huanchengfly.tieba.post.ui.common.prefs.widgets.ListPref
import com.huanchengfly.tieba.post.ui.common.prefs.widgets.SwitchPref
import com.huanchengfly.tieba.post.ui.common.prefs.widgets.TextPref
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.page.settings.LeadingIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.BackNavigationIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.EditorialSettingIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.MyScaffold
import com.huanchengfly.tieba.post.ui.widgets.compose.Sizes
import com.huanchengfly.tieba.post.ui.widgets.compose.TitleCentredToolbar
import com.huanchengfly.tieba.post.ui.widgets.compose.feedBackdropColor
import com.huanchengfly.tieba.post.utils.AppIconUtil
import com.huanchengfly.tieba.post.utils.LauncherIcons
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Destination
@Composable
fun CustomSettingsPage(
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    MyScaffold(
        backgroundColor = feedBackdropColor(),
        topBar = {
            TitleCentredToolbar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_settings_custom),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.h6,
                    )
                },
                eyebrow = "SETTINGS / PERSONALIZE",
                navigationIcon = {
                    BackNavigationIcon(onBackPressed = { navigator.navigateUp() })
                }
            )
        },
    ) { paddingValues ->
        PrefsScreen(
            dataStore = context.dataStore,
            dividerThickness = 0.dp,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            prefsItem {
                TextPref(
                    title = stringResource(id = R.string.title_custom_font_size),
                    leadingIcon = {
                        LeadingIcon {
                            EditorialSettingIcon(
                                kind = "font",
                                tint = ExtendedTheme.colors.primary,
                                modifier = Modifier.size(22.dp),
                                delayMillis = 0,
                            )
                        }
                    },
                    onClick = {
                        context.goToActivity<AppFontSizeActivity>()
                    }
                )
            }
            prefsItem {
                ListPref(
                    key = "app_icon",
                    title = stringResource(id = R.string.settings_app_icon),
                    defaultValue = LauncherIcons.DEFAULT_ICON,
                    leadingIcon = {
                        LeadingIcon {
                            EditorialSettingIcon(
                                kind = "apps",
                                tint = ExtendedTheme.colors.primary,
                                modifier = Modifier.size(22.dp),
                                delayMillis = 60,
                            )
                        }
                    },
                    entries = mapOf(
                        LauncherIcons.NEW_ICON to "自定义图标",
                        LauncherIcons.OLD_ICON to "旧图标",
                    ),
                    icons = mapOf(
                        LauncherIcons.NEW_ICON to {
                            Image(
                                painter = rememberDrawablePainter(
                                    drawable = LocalContext.current.getDrawable(
                                        R.drawable.ic_launcher_new_round
                                    )
                                ),
                                contentDescription = "自定义图标",
                                modifier = Modifier.size(Sizes.Medium)
                            )
                        },
                        LauncherIcons.OLD_ICON to {
                            Image(
                                painter = rememberDrawablePainter(
                                    drawable = LocalContext.current.getDrawable(
                                        R.drawable.ic_launcher_round
                                    )
                                ),
                                contentDescription = "旧图标",
                                modifier = Modifier.size(Sizes.Medium)
                            )
                        },
                    ),
                    onValueChange = { AppIconUtil.setIcon(icon = it) },
                    useSelectedAsSummary = true,
                )
            }
            prefsItem {
                SwitchPref(
                    key = "hideExplore",
                    title = stringResource(id = R.string.title_hide_explore),
                    defaultChecked = false,
                ) {
                    LeadingIcon {
                        EditorialSettingIcon(
                            kind = "compass",
                            tint = ExtendedTheme.colors.primary,
                            modifier = Modifier.size(22.dp),
                            delayMillis = 120,
                        )
                    }
                }
            }
        }
    }
}
