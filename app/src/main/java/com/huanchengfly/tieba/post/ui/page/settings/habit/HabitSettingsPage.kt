package com.huanchengfly.tieba.post.ui.page.settings.habit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.dataStore
import com.huanchengfly.tieba.post.ui.common.prefs.PrefsScreen
import com.huanchengfly.tieba.post.ui.common.prefs.widgets.ListPref
import com.huanchengfly.tieba.post.ui.common.prefs.widgets.SwitchPref
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.page.settings.LeadingIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.BackNavigationIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.EditorialSettingIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.MyScaffold
import com.huanchengfly.tieba.post.ui.widgets.compose.TitleCentredToolbar
import com.huanchengfly.tieba.post.utils.isPhotoPickerAvailable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Destination
@Composable
fun HabitSettingsPage(
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    MyScaffold(
        backgroundColor = Color.Transparent,
        topBar = {
            TitleCentredToolbar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_settings_read_habit),
                        fontWeight = FontWeight.Bold, style = MaterialTheme.typography.h6
                    )
                },
                navigationIcon = {
                    BackNavigationIcon(onBackPressed = { navigator.navigateUp() })
                }
            )
        },
    ) { paddingValues ->
        PrefsScreen(
            dataStore = LocalContext.current.dataStore,
            dividerThickness = 0.dp,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            prefsItem {
                ListPref(
                    key = "defaultTab",
                    title = stringResource(id = R.string.title_default_tab),
                    entries = mapOf(
                        "home" to stringResource(id = R.string.title_main),
                        "explore" to stringResource(id = R.string.title_explore),
                        "notification" to stringResource(id = R.string.title_notifications),
                        "user" to stringResource(id = R.string.title_user)
                    ),
                    useSelectedAsSummary = true,
                    defaultValue = "home",
                    leadingIcon = {
                        LeadingIcon {
                            EditorialSettingIcon(
                                kind = "layout",
                                tint = ExtendedTheme.colors.primary,
                                modifier = Modifier.size(24.dp),
                                delayMillis = 0,
                            )
                        }
                    },
                )
            }
            prefsItem {
                ListPref(
                    key = "image_load_type",
                    title = stringResource(id = R.string.title_settings_image_load_type),
                    entries = mapOf(
                        "0" to stringResource(id = R.string.title_image_load_type_smart_origin),
                        "1" to stringResource(id = R.string.title_image_load_type_smart_load),
                        "2" to stringResource(id = R.string.title_image_load_type_all_origin),
                        "3" to stringResource(id = R.string.title_image_load_type_all_no)
                    ),
                    useSelectedAsSummary = true,
                    defaultValue = "0",
                    leadingIcon = {
                        LeadingIcon {
                            EditorialSettingIcon(
                                kind = "default",
                                tint = ExtendedTheme.colors.primary,
                                modifier = Modifier.size(24.dp),
                                delayMillis = 60,
                            )
                        }
                    },
                )
            }
            prefsItem {
                ListPref(
                    key = "pic_watermark_type",
                    title = stringResource(id = R.string.title_settings_image_watermark),
                    entries = mapOf(
                        "0" to stringResource(id = R.string.title_image_watermark_none),
                        "1" to stringResource(id = R.string.title_image_watermark_user_name),
                        "2" to stringResource(id = R.string.title_image_watermark_forum_name)
                    ),
                    useSelectedAsSummary = true,
                    defaultValue = "2",
                    leadingIcon = {
                        LeadingIcon {
                            EditorialSettingIcon(
                                kind = "edit",
                                tint = ExtendedTheme.colors.primary,
                                modifier = Modifier.size(24.dp),
                                delayMillis = 120,
                            )
                        }
                    },
                )
            }
            prefsItem {
                SwitchPref(
                    key = "imageDarkenWhenNightMode",
                    title = stringResource(id = R.string.settings_image_darken_when_night_mode),
                    defaultChecked = true,
                ) {
                    LeadingIcon {
                        EditorialSettingIcon(
                            kind = "moon",
                            tint = ExtendedTheme.colors.primary,
                            modifier = Modifier.size(24.dp),
                            delayMillis = 180,
                        )
                    }
                }
            }
            prefsItem {
                ListPref(
                    key = "default_sort_type",
                    title = stringResource(id = R.string.title_settings_default_sort_type),
                    entries = mapOf(
                        "0" to stringResource(id = R.string.title_sort_by_reply),
                        "1" to stringResource(id = R.string.title_sort_by_send),
                    ),
                    useSelectedAsSummary = true,
                    defaultValue = "0",
                    leadingIcon = {
                        LeadingIcon {
                            EditorialSettingIcon(
                                kind = "agenda",
                                tint = ExtendedTheme.colors.primary,
                                modifier = Modifier.size(24.dp),
                                delayMillis = 240,
                            )
                        }
                    },
                )
            }
            prefsItem {
                ListPref(
                    key = "forumFabFunction",
                    title = stringResource(id = R.string.settings_forum_fab_function),
                    defaultValue = "post",
                    leadingIcon = {
                        LeadingIcon {
                            EditorialSettingIcon(
                                kind = "compass",
                                tint = ExtendedTheme.colors.primary,
                                modifier = Modifier.size(24.dp),
                                delayMillis = 300,
                            )
                        }
                    },
                    useSelectedAsSummary = true,
                    entries = mapOf(
                        "post" to stringResource(id = R.string.btn_post),
                        "refresh" to stringResource(id = R.string.btn_refresh),
                        "back_to_top" to stringResource(id = R.string.btn_back_to_top),
                        "hide" to stringResource(id = R.string.btn_hide)
                    )
                )
            }
            prefsItem {
                SwitchPref(
                    key = "hideMedia",
                    title = stringResource(id = R.string.title_hide_media),
                    defaultChecked = false
                ) {
                    LeadingIcon {
                        EditorialSettingIcon(
                            kind = "hide",
                            tint = ExtendedTheme.colors.primary,
                            modifier = Modifier.size(24.dp),
                            delayMillis = 360,
                        )
                    }
                }
            }
//            prefsItem {
//                SwitchPref(
//                    key = "showShortcutInThread",
//                    title = stringResource(id = R.string.settings_show_shortcut),
//                    defaultChecked = true,
//                    leadingIcon = {
//                        LeadingIcon {
//                            AvatarIcon(
//                                icon = ImageVector.vectorResource(id = R.drawable.ic_quick_yellow),
//                                size = Sizes.Small,
//                                contentDescription = null,
//                            )
//                        }
//                    },
//                    summaryOn = stringResource(id = R.string.tip_show_shortcut_in_thread_on),
//                    summaryOff = stringResource(id = R.string.tip_show_shortcut_in_thread)
//                )
//            }
            prefsItem {
                SwitchPref(
                    key = "collect_thread_see_lz",
                    title = stringResource(id = R.string.settings_collect_thread_see_lz),
                    defaultChecked = true,
                    leadingIcon = {
                        LeadingIcon {
                            EditorialSettingIcon(
                                kind = "account",
                                tint = ExtendedTheme.colors.primary,
                                modifier = Modifier.size(24.dp),
                                delayMillis = 420,
                            )
                        }
                    },
                    summaryOn = stringResource(id = R.string.tip_collect_thread_see_lz_on),
                    summaryOff = stringResource(id = R.string.tip_collect_thread_see_lz)
                )
            }
            prefsItem {
                SwitchPref(
                    key = "collect_thread_desc_sort",
                    title = stringResource(id = R.string.settings_collect_thread_desc_sort),
                    defaultChecked = false,
                    leadingIcon = {
                        LeadingIcon {
                            EditorialSettingIcon(
                                kind = "tune",
                                tint = ExtendedTheme.colors.primary,
                                modifier = Modifier.size(24.dp),
                                delayMillis = 480,
                            )
                        }
                    },
                    summaryOn = stringResource(id = R.string.tip_collect_thread_desc_sort_on),
                    summaryOff = stringResource(id = R.string.tip_collect_thread_desc_sort)
                )
            }
            prefsItem {
                SwitchPref(
                    key = "show_both_username_and_nickname",
                    title = stringResource(id = R.string.title_show_both_username_and_nickname),
                    defaultChecked = false,
                ) {
                    LeadingIcon {
                        EditorialSettingIcon(
                            kind = "account",
                            tint = ExtendedTheme.colors.primary,
                            modifier = Modifier.size(24.dp),
                            delayMillis = 540,
                        )
                    }
                }
            }
            prefsItem {
                SwitchPref(
                    key = "homePageShowHistoryForum",
                    title = stringResource(id = R.string.settings_home_page_show_history_forum),
                    defaultChecked = true,
                    leadingIcon = {
                        LeadingIcon {
                            EditorialSettingIcon(
                                kind = "time",
                                tint = ExtendedTheme.colors.primary,
                                modifier = Modifier.size(24.dp),
                                delayMillis = 600,
                            )
                        }
                    },
                )
            }
            prefsItem {
                SwitchPref(
                    key = "postOrReplyWarning",
                    title = stringResource(id = R.string.title_post_or_reply_warning),
                    defaultChecked = false,
                ) {
                    LeadingIcon {
                        EditorialSettingIcon(
                            kind = "bell",
                            tint = ExtendedTheme.colors.primary,
                            modifier = Modifier.size(24.dp),
                            delayMillis = 660,
                        )
                    }
                }
            }
            prefsItem {
                SwitchPref(
                    key = "hideReply",
                    title = stringResource(id = R.string.title_hide_reply),
                    defaultChecked = false,
                ) {
                    LeadingIcon {
                        EditorialSettingIcon(
                            kind = "hide",
                            tint = ExtendedTheme.colors.primary,
                            modifier = Modifier.size(24.dp),
                            delayMillis = 720,
                        )
                    }
                }
            }
            prefsItem {
                SwitchPref(
                    key = "doNotUsePhotoPicker",
                    title = stringResource(id = R.string.title_do_not_use_photo_picker),
                    summary = {
                        if (!isPhotoPickerAvailable()) {
                            context.getString(R.string.summary_photo_picker_not_supported)
                        } else if (it) {
                            context.getString(R.string.summary_do_not_use_photo_picker)
                        } else {
                            context.getString(R.string.summary_use_photo_picker)
                        }
                    },
                    defaultChecked = false,
                    leadingIcon = {
                        LeadingIcon {
                            EditorialSettingIcon(
                                kind = "default",
                                tint = ExtendedTheme.colors.primary,
                                modifier = Modifier.size(24.dp),
                                delayMillis = 780,
                            )
                        }
                    },
                    enabled = isPhotoPickerAvailable()
                )
            }
        }
    }
}
