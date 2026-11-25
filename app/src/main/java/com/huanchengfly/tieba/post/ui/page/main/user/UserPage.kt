package com.huanchengfly.tieba.post.ui.page.main.user

import android.graphics.Typeface
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.arch.collectPartialAsState
import com.huanchengfly.tieba.post.arch.pageViewModel
import com.huanchengfly.tieba.post.models.database.Account
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.common.theme.compose.pullRefreshIndicator
import com.huanchengfly.tieba.post.ui.page.LocalNavigator
import com.huanchengfly.tieba.post.ui.page.destinations.AboutPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.AppThemePageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.HistoryPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.LoginPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.SettingsPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.ThreadStorePageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.UserProfilePageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.WebViewPageDestination
import com.huanchengfly.tieba.post.ui.page.main.LocalMainTabActive
import com.huanchengfly.tieba.post.ui.widgets.compose.Avatar
import com.huanchengfly.tieba.post.ui.widgets.compose.ConfirmDialog
import com.huanchengfly.tieba.post.ui.widgets.compose.EditorialChevronRightIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.EditorialMenuIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.HorizontalDivider
import com.huanchengfly.tieba.post.ui.widgets.compose.LocalFloatingBottomBarPadding
import com.huanchengfly.tieba.post.ui.widgets.compose.Sizes
import com.huanchengfly.tieba.post.ui.widgets.compose.Switch
import com.huanchengfly.tieba.post.ui.widgets.compose.archiveReveal
import com.huanchengfly.tieba.post.ui.widgets.compose.feedBackdropColor
import com.huanchengfly.tieba.post.ui.widgets.compose.feedCardSurface
import com.huanchengfly.tieba.post.ui.widgets.compose.placeholder.placeholder
import com.huanchengfly.tieba.post.ui.widgets.compose.rememberDialogState
import com.huanchengfly.tieba.post.utils.CuidUtils
import com.huanchengfly.tieba.post.utils.StringUtil
import com.huanchengfly.tieba.post.utils.ThemeUtil
import com.huanchengfly.tieba.post.utils.appPreferences

private fun Modifier.userCardReveal(index: Int): Modifier {
    val stagger = index.coerceAtMost(5) * 0.05f
    return archiveReveal(
        start = 0.18f + stagger,
        end = (0.84f + stagger).coerceAtMost(1f),
        travelY = 6.dp,
    )
}

@Composable
private fun StatCardPlaceholder(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.placeholder(visible = true, color = ExtendedTheme.colors.chip, shape = RectangleShape),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatCardItem(
            statNum = 0,
            statText = stringResource(id = R.string.text_stat_follow)
        )
        HorizontalDivider(
            modifier = Modifier.archiveReveal(start = 0f, end = 0.46f),
            color = ExtendedTheme.colors.divider.copy(alpha = 0.6f),
        )
        StatCardItem(
            statNum = 0,
            statText = stringResource(id = R.string.text_stat_fans)
        )
        HorizontalDivider(
            modifier = Modifier.archiveReveal(start = 0f, end = 0.46f),
            color = ExtendedTheme.colors.divider.copy(alpha = 0.6f),
        )
        StatCardItem(
            statNum = 0,
            statText = stringResource(id = R.string.title_stat_posts_num)
        )
    }
}

@Composable
private fun StatCard(
    account: Account,
    onFollowClick: () -> Unit,
    onFansClick: () -> Unit,
    onPostsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val postNum by animateIntAsState(targetValue = account.postNum?.toIntOrNull() ?: 0)
    val fansNum by animateIntAsState(targetValue = account.fansNum?.toIntOrNull() ?: 0)
    val concernNum by animateIntAsState(targetValue = account.concernNum?.toIntOrNull() ?: 0)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatCardItem(
            statNum = concernNum,
            statText = stringResource(id = R.string.text_stat_follow),
            onClick = onFollowClick,
        )
        HorizontalDivider(
            modifier = Modifier.archiveReveal(start = 0f, end = 0.46f),
            color = ExtendedTheme.colors.divider.copy(alpha = 0.6f),
        )
        StatCardItem(
            statNum = fansNum,
            statText = stringResource(id = R.string.text_stat_fans),
            onClick = onFansClick,
        )
        HorizontalDivider(
            modifier = Modifier.archiveReveal(start = 0f, end = 0.46f),
            color = ExtendedTheme.colors.divider.copy(alpha = 0.6f),
        )
        StatCardItem(
            statNum = postNum,
            statText = stringResource(id = R.string.title_stat_posts_num),
            onClick = onPostsClick,
        )
    }
}

@Composable
private fun InfoCard(
    modifier: Modifier = Modifier,
    userName: String = "",
    userIntro: String = "",
    avatar: String? = null,
    isPlaceholder: Boolean = false
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier.userCardReveal(index = 0),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.Bottom)
            ) {
                Text(
                    text = userName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ExtendedTheme.colors.text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .placeholder(visible = isPlaceholder, color = ExtendedTheme.colors.chip, shape = RectangleShape),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userIntro,
                    fontSize = 12.sp,
                    color = ExtendedTheme.colors.textSecondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .placeholder(visible = isPlaceholder, color = ExtendedTheme.colors.chip, shape = RectangleShape),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            if (avatar != null) {
                Avatar(
                    data = avatar,
                    size = Sizes.Large,
                    contentDescription = stringResource(id = R.string.desc_user_avatar),
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .placeholder(visible = isPlaceholder, color = ExtendedTheme.colors.chip, shape = RectangleShape),
                )
            }
        }
    }
}

@Composable
private fun RowScope.StatCardItem(
    statNum: Int,
    statText: String,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(vertical = 18.dp)
            .userCardReveal(index = 1),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "$statNum",
            fontSize = 20.sp,
            fontFamily = FontFamily(Typeface.createFromAsset(LocalContext.current.assets, "bebas.ttf")),
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = statText,
            fontSize = 12.sp,
            color = ExtendedTheme.colors.textSecondary
        )
    }
}

@Composable
private fun LoginTipCard(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier.userCardReveal(index = 0),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.Bottom)
            ) {
                Text(
                    text = stringResource(id = R.string.tip_login),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ExtendedTheme.colors.text,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = null,
                tint = ExtendedTheme.colors.onChip,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(Sizes.Large)
                    .background(color = ExtendedTheme.colors.chip)
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun UserMenuCard(
    iconKind: String,
    title: String,
    subtitle: String,
    trigger: Int,
    delayMillis: Int,
    cardIndex: Int,
    onClick: () -> Unit,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    val colors = ExtendedTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .feedCardSurface()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().userCardReveal(cardIndex),
            verticalAlignment = Alignment.CenterVertically,
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
                EditorialMenuIcon(
                    kind = iconKind,
                    tint = colors.primary,
                    trigger = trigger,
                    delayMillis = delayMillis,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            if (trailing != null) {
                trailing()
            } else {
                EditorialChevronRightIcon(tint = colors.textSecondary.copy(alpha = 0.65f))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserPage(
    viewModel: UserViewModel = pageViewModel<UserUiIntent, UserViewModel>(
        listOf(
            UserUiIntent.Refresh
        )
    )
) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val isTabActive = LocalMainTabActive.current
    var enterTrigger by remember { mutableIntStateOf(0) }
    val menuClickTriggers = remember { mutableStateMapOf<String, Int>() }
    LaunchedEffect(isTabActive) {
        if (isTabActive) enterTrigger++
    }

    fun onMenuClick(kind: String) {
        menuClickTriggers[kind] = (menuClickTriggers[kind] ?: 0) + 1
    }

    val isLoading by viewModel.uiState.collectPartialAsState(
        prop1 = UserUiState::isLoading,
        initial = false
    )
    val account by viewModel.uiState.collectPartialAsState(
        prop1 = UserUiState::account,
        initial = null
    )

    val switchToNightDialogState = rememberDialogState()
    ConfirmDialog(
        dialogState = switchToNightDialogState,
        onConfirm = {},
        onCancel = {
            context.appPreferences.followSystemNight = false
            ThemeUtil.switchNightMode()
        },
        confirmText = stringResource(id = R.string.btn_keep_following),
        cancelText = stringResource(id = R.string.btn_close_following)
    ) {
        Text(text = stringResource(id = R.string.message_dialog_follow_system_night))
    }

    Scaffold(
        backgroundColor = feedBackdropColor(),
        modifier = Modifier.fillMaxSize()
    ) { contentPaddings ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = isLoading,
            onRefresh = { viewModel.send(UserUiIntent.Refresh) })
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPaddings)
                .pullRefresh(pullRefreshState),
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .verticalScroll(state = rememberScrollState())
                    .fillMaxSize()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text(
                        text = stringResource(id = R.string.title_user),
                        color = ExtendedTheme.colors.onTopBar,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.4.sp,
                        lineHeight = 23.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "PROFILE / ACCOUNT & TOOLS",
                        modifier = Modifier.archiveReveal(start = 0.10f, end = 0.68f),
                        color = ExtendedTheme.colors.textSecondary,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        lineHeight = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                val accountCardModifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .feedCardSurface()
                if (account != null) {
                    InfoCard(
                        modifier = accountCardModifier
                            .clickable {
                                navigator.navigate(UserProfilePageDestination(account!!.uid.toLong()))
                            }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        userName = account!!.nameShow ?: account!!.name,
                        userIntro = account!!.intro ?: stringResource(id = R.string.tip_no_intro),
                        avatar = StringUtil.getAvatarUrl(account!!.portrait),
                    )
                    StatCard(
                        account = account!!,
                        onFollowClick = {
                            navigator.navigate(
                                WebViewPageDestination(
                                    initialUrl = "https://tieba.baidu.com/home/concern?id=${account!!.portrait}&fr=home"
                                )
                            )
                        },
                        onFansClick = {
                            navigator.navigate(
                                WebViewPageDestination(
                                    initialUrl = "https://tieba.baidu.com/home/fans?id=${account!!.portrait}&fr=home"
                                )
                            )
                        },
                        onPostsClick = {
                            navigator.navigate(
                                UserProfilePageDestination(
                                    uid = account!!.uid.toLong(),
                                    initialTab = 1,
                                )
                            )
                        },
                        modifier = accountCardModifier,
                    )
                } else if (isLoading) {
                    InfoCard(
                        modifier = accountCardModifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        isPlaceholder = true,
                    )
                    StatCardPlaceholder(modifier = accountCardModifier)
                } else {
                    LoginTipCard(
                        modifier = accountCardModifier
                            .clickable {
                                navigator.navigate(LoginPageDestination)
                            }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                    )
                }
                Spacer(Modifier.height(10.dp))
                val firstMenuCardIndex = if (account != null || isLoading) 2 else 1
                val historyCardIndex = firstMenuCardIndex + if (account != null) 1 else 0
                val settingsCardIndex = historyCardIndex + if (account != null) 3 else 2
                if (account != null) {
                    UserMenuCard(
                        iconKind = "collect",
                        title = stringResource(id = R.string.title_my_collect),
                        subtitle = "收藏的帖子与楼层",
                        trigger = enterTrigger + (menuClickTriggers["collect"] ?: 0),
                        delayMillis = 0,
                        cardIndex = firstMenuCardIndex,
                        onClick = {
                            onMenuClick("collect")
                            navigator.navigate(ThreadStorePageDestination)
                        },
                    )
                }
                UserMenuCard(
                    iconKind = "history",
                    title = stringResource(id = R.string.title_history),
                    subtitle = "最近浏览过的帖子与贴吧",
                    trigger = enterTrigger + (menuClickTriggers["history"] ?: 0),
                    delayMillis = 40,
                    cardIndex = historyCardIndex,
                    onClick = {
                        onMenuClick("history")
                        navigator.navigate(HistoryPageDestination)
                    },
                )

                UserMenuCard(
                    iconKind = "theme",
                    title = stringResource(id = R.string.title_theme),
                    subtitle = "莫奈色板与深色模式",
                    trigger = enterTrigger + (menuClickTriggers["theme"] ?: 0),
                    delayMillis = 80,
                    cardIndex = historyCardIndex + 1,
                    onClick = {
                        onMenuClick("theme")
                        navigator.navigate(AppThemePageDestination)
                    },
                ) {
                    Text(
                        text = stringResource(id = R.string.my_info_night),
                        color = ExtendedTheme.colors.textSecondary,
                        fontSize = 12.sp,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Switch(
                        checked = ThemeUtil.isNightMode(ThemeUtil.themeState.value),
                        onCheckedChange = {
                            if (context.appPreferences.followSystemNight) {
                                switchToNightDialogState.show()
                            } else {
                                ThemeUtil.switchNightMode()
                            }
                        }
                    )
                }
                if (account != null) {
                    UserMenuCard(
                        iconKind = "service",
                        title = stringResource(id = R.string.my_info_service_center),
                        subtitle = "账号状态与服务中心",
                        trigger = enterTrigger + (menuClickTriggers["service"] ?: 0),
                        delayMillis = 120,
                        cardIndex = historyCardIndex + 2,
                        onClick = {
                            onMenuClick("service")
                            navigator.navigate(
                                WebViewPageDestination(
                                    initialUrl = "https://tieba.baidu.com/mo/q/hybrid-main-service/uegServiceCenter?cuid=${CuidUtils.getNewCuid()}&cuid_galaxy2=${CuidUtils.getNewCuid()}&cuid_gid=&timestamp=${System.currentTimeMillis()}&_client_version=12.52.1.0&nohead=1"
                                )
                            )
                        },
                    )
                }

                UserMenuCard(
                    iconKind = "settings",
                    title = stringResource(id = R.string.my_info_settings),
                    subtitle = "偏好、屏蔽与显示设置",
                    trigger = enterTrigger + (menuClickTriggers["settings"] ?: 0),
                    delayMillis = 160,
                    cardIndex = settingsCardIndex,
                    onClick = {
                        onMenuClick("settings")
                        navigator.navigate(SettingsPageDestination)
                    },
                )
                UserMenuCard(
                    iconKind = "about",
                    title = stringResource(id = R.string.my_info_about),
                    subtitle = "版本信息与项目说明",
                    trigger = enterTrigger + (menuClickTriggers["about"] ?: 0),
                    delayMillis = 200,
                    cardIndex = settingsCardIndex + 1,
                    onClick = {
                        onMenuClick("about")
                        navigator.navigate(AboutPageDestination)
                    },
                )
                Spacer(modifier = Modifier.height(LocalFloatingBottomBarPadding.current + 12.dp))
            }

            PullRefreshIndicator(
                refreshing = isLoading,
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding(),
                backgroundColor = ExtendedTheme.colors.pullRefreshIndicator,
                contentColor = ExtendedTheme.colors.primary,
            )
        }
    }
}
