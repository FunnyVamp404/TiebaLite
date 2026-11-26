package com.huanchengfly.tieba.post.ui.page.settings.block

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.dataStore
import com.huanchengfly.tieba.post.ui.common.prefs.PrefsScreen
import com.huanchengfly.tieba.post.ui.common.prefs.widgets.SwitchPref
import com.huanchengfly.tieba.post.ui.common.prefs.widgets.TextPref
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.page.destinations.BlockListPageDestination
import com.huanchengfly.tieba.post.ui.page.settings.LeadingIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.BackNavigationIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.EditorialSettingIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.MyScaffold
import com.huanchengfly.tieba.post.ui.widgets.compose.TitleCentredToolbar
import com.huanchengfly.tieba.post.ui.widgets.compose.feedBackdropColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun BlockSettingsPage(
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    MyScaffold(
        backgroundColor = feedBackdropColor(),
        topBar = {
            TitleCentredToolbar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_block_settings),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.h6,
                    )
                },
                eyebrow = "SETTINGS / CONTENT FILTER",
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
                    title = stringResource(id = R.string.title_block_list),
                    leadingIcon = {
                        LeadingIcon {
                            EditorialSettingIcon(
                                kind = "block",
                                tint = ExtendedTheme.colors.primary,
                                modifier = Modifier.size(24.dp),
                                delayMillis = 0,
                            )
                        }
                    },
                    onClick = { navigator.navigate(BlockListPageDestination) }
                )
            }
            prefsItem {
                SwitchPref(
                    key = "hideBlockedContent",
                    title = stringResource(id = R.string.settings_hide_blocked_content),
                    defaultChecked = false
                ) {
                    LeadingIcon {
                        EditorialSettingIcon(
                            kind = "hide",
                            tint = ExtendedTheme.colors.primary,
                            modifier = Modifier.size(24.dp),
                            delayMillis = 60,
                        )
                    }
                }
            }
            prefsItem {
                SwitchPref(
                    key = "blockVideo",
                    title = stringResource(id = R.string.settings_block_video),
                    summary = stringResource(id = R.string.settings_block_video_summary),
                    defaultChecked = false,
                    leadingIcon = {
                        LeadingIcon {
                            EditorialSettingIcon(
                                kind = "video_off",
                                tint = ExtendedTheme.colors.primary,
                                modifier = Modifier.size(24.dp),
                                delayMillis = 120,
                            )
                        }
                    }
                )
            }
        }
    }
}