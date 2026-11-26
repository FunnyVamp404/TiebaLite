package com.huanchengfly.tieba.post.ui.page.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.models.database.Account
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.page.LocalNavigator
import com.huanchengfly.tieba.post.ui.page.ProvideNavigator
import com.huanchengfly.tieba.post.ui.page.destinations.AccountManagePageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.BlockSettingsPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.CustomSettingsPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.HabitSettingsPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.LoginPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.MoreSettingsPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.OKSignSettingsPageDestination
import com.huanchengfly.tieba.post.ui.widgets.compose.Avatar
import com.huanchengfly.tieba.post.ui.widgets.compose.BackNavigationIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.EditorialChevronRightIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.EditorialSettingIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.MyScaffold
import com.huanchengfly.tieba.post.ui.widgets.compose.Sizes
import com.huanchengfly.tieba.post.ui.widgets.compose.TitleCentredToolbar
import com.huanchengfly.tieba.post.ui.widgets.compose.feedBackdropColor
import com.huanchengfly.tieba.post.ui.widgets.compose.feedCardSurface
import com.huanchengfly.tieba.post.utils.AccountUtil.LocalAccount
import com.huanchengfly.tieba.post.utils.StringUtil
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
internal fun LeadingIcon(
    content: @Composable () -> Unit
) {
    val colors = ExtendedTheme.colors
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                color = colors.primary.copy(alpha = if (colors.isNightMode) 0.14f else 0.10f),
                shape = RoundedCornerShape(9.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides colors.primary) {
            content()
        }
    }
}

@Composable
fun NowAccountItem(
    account: Account?,
    modifier: Modifier = Modifier
) {
    val navigator = LocalNavigator.current
    SettingsCard(
        title = stringResource(id = R.string.title_account_manage),
        subtitle = if (account != null) {
            stringResource(id = R.string.summary_now_account, account.nameShow ?: account.name)
        } else {
            stringResource(id = R.string.summary_not_logged_in)
        },
        modifier = modifier,
        onClick = {
            if (account != null) {
                navigator.navigate(AccountManagePageDestination)
            } else {
                navigator.navigate(LoginPageDestination)
            }
        },
    ) { trigger ->
        if (account != null) {
            Avatar(
                data = StringUtil.getAvatarUrl(account.portrait),
                size = Sizes.Small,
                contentDescription = null,
            )
        } else {
            EditorialSettingIcon(
                kind = "account",
                tint = ExtendedTheme.colors.primary,
                modifier = Modifier.size(22.dp),
                trigger = trigger,
                delayMillis = 0,
            )
        }
    }
}

@Destination
@Composable
fun SettingsPage(
    navigator: DestinationsNavigator,
) {
    ProvideNavigator(navigator = navigator) {
        MyScaffold(
            backgroundColor = feedBackdropColor(),
            topBar = {
                TitleCentredToolbar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.title_settings),
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.h6,
                        )
                    },
                    eyebrow = "SYSTEM / PREFERENCES",
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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    NowAccountItem(account = LocalAccount.current)
                }
                item {
                    SettingsCard(
                        title = stringResource(id = R.string.title_block_settings),
                        subtitle = stringResource(id = R.string.summary_block_settings),
                        onClick = { navigator.navigate(BlockSettingsPageDestination) },
                    ) { trigger ->
                        EditorialSettingIcon(
                            kind = "block",
                            tint = ExtendedTheme.colors.primary,
                            modifier = Modifier.size(22.dp),
                            trigger = trigger,
                            delayMillis = 70,
                        )
                    }
                }
                item {
                    SettingsCard(
                        title = stringResource(id = R.string.title_settings_custom),
                        subtitle = stringResource(id = R.string.summary_settings_custom),
                        onClick = { navigator.navigate(CustomSettingsPageDestination) },
                    ) { trigger ->
                        EditorialSettingIcon(
                            kind = "brush",
                            tint = ExtendedTheme.colors.primary,
                            modifier = Modifier.size(22.dp),
                            trigger = trigger,
                            delayMillis = 140,
                        )
                    }
                }
                item {
                    SettingsCard(
                        title = stringResource(id = R.string.title_settings_read_habit),
                        subtitle = stringResource(id = R.string.summary_settings_habit),
                        onClick = { navigator.navigate(HabitSettingsPageDestination) },
                    ) { trigger ->
                        EditorialSettingIcon(
                            kind = "layout",
                            tint = ExtendedTheme.colors.primary,
                            modifier = Modifier.size(22.dp),
                            trigger = trigger,
                            delayMillis = 210,
                        )
                    }
                }
                item {
                    SettingsCard(
                        title = stringResource(id = R.string.title_oksign),
                        subtitle = stringResource(id = R.string.summary_settings_oksign),
                        onClick = { navigator.navigate(OKSignSettingsPageDestination) },
                    ) { trigger ->
                        EditorialSettingIcon(
                            kind = "rocket",
                            tint = ExtendedTheme.colors.primary,
                            modifier = Modifier.size(22.dp),
                            trigger = trigger,
                            delayMillis = 280,
                        )
                    }
                }
                item {
                    SettingsCard(
                        title = stringResource(id = R.string.title_settings_more),
                        subtitle = stringResource(id = R.string.summary_settings_more),
                        onClick = { navigator.navigate(MoreSettingsPageDestination) },
                    ) { trigger ->
                        EditorialSettingIcon(
                            kind = "more",
                            tint = ExtendedTheme.colors.primary,
                            modifier = Modifier.size(22.dp),
                            trigger = trigger,
                            delayMillis = 350,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (trigger: Int) -> Unit,
) {
    val colors = ExtendedTheme.colors
    var trigger by remember { mutableIntStateOf(0) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .feedCardSurface()
            .clickable {
                trigger++
                onClick()
            }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = colors.primary.copy(alpha = if (colors.isNightMode) 0.14f else 0.10f),
                    shape = RoundedCornerShape(9.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            icon(trigger)
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colors.text,
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = colors.textSecondary,
            )
        }
        EditorialChevronRightIcon(tint = colors.textSecondary.copy(alpha = 0.65f))
    }
}
