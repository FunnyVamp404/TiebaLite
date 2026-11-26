package com.huanchengfly.tieba.post.ui.page.main.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.arch.GlobalEvent
import com.huanchengfly.tieba.post.arch.collectPartialAsState
import com.huanchengfly.tieba.post.arch.onGlobalEvent
import com.huanchengfly.tieba.post.arch.pageViewModel
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.common.theme.compose.pullRefreshIndicator
import com.huanchengfly.tieba.post.ui.page.LocalNavigator
import com.huanchengfly.tieba.post.ui.page.destinations.ForumPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.LoginPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.SearchPageDestination
import com.huanchengfly.tieba.post.ui.page.main.LocalMainTabActive
import com.huanchengfly.tieba.post.ui.widgets.compose.ActionItem
import com.huanchengfly.tieba.post.ui.widgets.compose.Avatar
import com.huanchengfly.tieba.post.ui.widgets.compose.Button
import com.huanchengfly.tieba.post.ui.widgets.compose.ConfirmDialog
import com.huanchengfly.tieba.post.ui.widgets.compose.EditorialActionIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.ErrorScreen
import com.huanchengfly.tieba.post.ui.widgets.compose.LongClickMenu
import com.huanchengfly.tieba.post.ui.widgets.compose.MenuState
import com.huanchengfly.tieba.post.ui.widgets.compose.MyLazyVerticalGrid
import com.huanchengfly.tieba.post.ui.widgets.compose.MyScaffold
import com.huanchengfly.tieba.post.ui.widgets.compose.TextButton
import com.huanchengfly.tieba.post.ui.widgets.compose.TipScreen
import com.huanchengfly.tieba.post.ui.widgets.compose.Toolbar
import com.huanchengfly.tieba.post.ui.widgets.compose.accountNavIconIfCompact
import com.huanchengfly.tieba.post.ui.widgets.compose.feedBackdropColor
import com.huanchengfly.tieba.post.ui.widgets.compose.feedCardSurface
import com.huanchengfly.tieba.post.ui.widgets.compose.placeholder.placeholder
import com.huanchengfly.tieba.post.ui.widgets.compose.rememberDialogState
import com.huanchengfly.tieba.post.ui.widgets.compose.rememberMenuState
import com.huanchengfly.tieba.post.ui.widgets.compose.states.StateScreen
import com.huanchengfly.tieba.post.utils.AccountUtil.LocalAccount
import com.huanchengfly.tieba.post.utils.HomeForumLayoutState
import com.huanchengfly.tieba.post.utils.TiebaUtil
import com.huanchengfly.tieba.post.utils.appPreferences
import kotlinx.collections.immutable.persistentListOf

private val forumGridPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp)

private fun getGridCells(isGridMode: Boolean): GridCells =
    if (isGridMode) GridCells.Fixed(2) else GridCells.Fixed(1)

@Preview("SearchBoxPreview")
@Composable
fun SearchBoxPreview() {
    SearchBox(onClick = {})
}

@Composable
fun SearchBox(
    modifier: Modifier = Modifier,
    contentColor: Color = ExtendedTheme.colors.textSecondary,
    iconTrigger: Int = 0,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .background(Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .feedCardSurface()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            EditorialActionIcon(
                kind = "search",
                tint = contentColor,
                trigger = iconTrigger,
                delayMillis = 80,
                modifier = Modifier.size(19.dp),
            )
            Text(
                text = stringResource(id = R.string.hint_search),
                color = contentColor,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun Header(
    text: String,
    modifier: Modifier = Modifier,
    invert: Boolean = false
) {
    Text(
        text = text,
        modifier = Modifier.padding(vertical = 4.dp).then(modifier),
        color = if (invert) ExtendedTheme.colors.primary else ExtendedTheme.colors.textSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun ForumItemPlaceholder(
    isGridMode: Boolean,
) {
    val color = ExtendedTheme.colors.chip
    ForumItemLayout(
        isGridMode = isGridMode,
        modifier = Modifier.fillMaxWidth().feedCardSurface(),
        avatar = {
            Spacer(
                modifier = Modifier
                    .size(if (isGridMode) 32.dp else 38.dp)
                    .background(color, RoundedCornerShape(10.dp))
            )
        },
        name = {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth(if (isGridMode) 0.8f else 0.5f)
                    .height(if (isGridMode) 17.dp else 18.dp)
                    .background(color, RoundedCornerShape(4.dp))
            )
        },
        level = {
            Spacer(
                modifier = Modifier
                    .width(if (isGridMode) 35.dp else 48.dp)
                    .height(if (isGridMode) 16.dp else 22.dp)
                    .background(color, RoundedCornerShape(6.dp))
            )
        },
    )
}

@Composable
private fun ForumItemMenuContent(
    menuState: MenuState,
    isTopForum: Boolean,
    onDeleteTopForum: () -> Unit,
    onAddTopForum: () -> Unit,
    onCopyName: () -> Unit,
    onUnfollow: () -> Unit,
) {
    DropdownMenuItem(
        onClick = {
            if (isTopForum) {
                onDeleteTopForum()
            } else {
                onAddTopForum()
            }
            menuState.expanded = false
        }
    ) {
        if (isTopForum) {
            Text(text = stringResource(id = R.string.menu_top_del))
        } else {
            Text(text = stringResource(id = R.string.menu_top))
        }
    }
    DropdownMenuItem(
        onClick = {
            onCopyName()
            menuState.expanded = false
        }
    ) {
        Text(text = stringResource(id = R.string.title_copy_forum_name))
    }
    DropdownMenuItem(
        onClick = {
            onUnfollow()
            menuState.expanded = false
        }
    ) {
        Text(text = stringResource(id = R.string.button_unfollow))
    }
}

@Composable
private fun ForumItemContent(
    item: HomeUiState.Forum,
    isGridMode: Boolean,
) {
    ForumItemLayout(
        isGridMode = isGridMode,
        avatar = {
            Avatar(
                data = item.avatar,
                size = if (isGridMode) 32.dp else 38.dp,
                contentDescription = null,
                shape = RoundedCornerShape(10.dp),
            )
        },
        name = {
            Text(
                text = item.forumName,
                color = ExtendedTheme.colors.text,
                fontSize = if (isGridMode) 14.sp else 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        level = { ForumLevelBadge(item, compact = isGridMode) },
    )
}

@Composable
private fun ForumItemLayout(
    isGridMode: Boolean,
    modifier: Modifier = Modifier,
    avatar: @Composable () -> Unit,
    name: @Composable () -> Unit,
    level: @Composable () -> Unit,
) {
    if (!isGridMode) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            avatar()
            Box(modifier = Modifier.weight(1f)) { name() }
            level()
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(modifier = Modifier.weight(1f)) { name() }
            level()
        }
    }
}

@Composable
private fun ForumLevelBadge(item: HomeUiState.Forum, compact: Boolean) {
    val colors = ExtendedTheme.colors
    val contentColor = if (item.isSign) colors.primary else colors.textSecondary
    val backgroundColor = if (item.isSign) {
        colors.primary.copy(alpha = if (colors.isNightMode) 0.18f else 0.12f)
    } else {
        colors.chip.copy(alpha = 0.7f)
    }
    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .padding(
                horizontal = if (compact) 6.dp else 8.dp,
                vertical = if (compact) 2.dp else 4.dp,
            ),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "Lv.${item.levelId}",
            color = contentColor,
            fontSize = if (compact) 10.sp else 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
        )
        if (item.isSign) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = stringResource(id = R.string.tip_signed),
                modifier = Modifier.size(if (compact) 10.dp else 12.dp),
                tint = contentColor,
            )
        }
    }
}

@Composable
private fun ForumItem(
    item: HomeUiState.Forum,
    isGridMode: Boolean,
    onClick: (HomeUiState.Forum) -> Unit,
    onUnfollow: (HomeUiState.Forum) -> Unit,
    onAddTopForum: (HomeUiState.Forum) -> Unit,
    onDeleteTopForum: (HomeUiState.Forum) -> Unit,
    isTopForum: Boolean = false,
) {
    val context = LocalContext.current
    val menuState = rememberMenuState()
    LongClickMenu(
        modifier = Modifier.fillMaxWidth().feedCardSurface(),
        menuContent = {
            ForumItemMenuContent(
                menuState = menuState,
                isTopForum = isTopForum,
                onDeleteTopForum = { onDeleteTopForum(item) },
                onAddTopForum = { onAddTopForum(item) },
                onCopyName = {
                    TiebaUtil.copyText(context, item.forumName)
                },
                onUnfollow = { onUnfollow(item) }
            )
        },
        menuState = menuState,
        onClick = {
            onClick(item)
        }
    ) {
        ForumItemContent(item = item, isGridMode = isGridMode)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomePage(
    viewModel: HomeViewModel = pageViewModel<HomeUiIntent, HomeViewModel>(listOf(HomeUiIntent.Refresh)),
    canOpenExplore: Boolean = false,
    onOpenExplore: () -> Unit = {},
) {
    val account = LocalAccount.current
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val isTabActive = LocalMainTabActive.current
    var enterTrigger by remember { mutableIntStateOf(0) }
    var signTrigger by remember { mutableIntStateOf(0) }
    var layoutTrigger by remember { mutableIntStateOf(0) }
    var searchTrigger by remember { mutableIntStateOf(0) }
    LaunchedEffect(isTabActive) {
        if (isTabActive) enterTrigger++
    }

    val isLoading by viewModel.uiState.collectPartialAsState(
        prop1 = HomeUiState::isLoading,
        initial = true
    )
    val forums by viewModel.uiState.collectPartialAsState(
        prop1 = HomeUiState::forums,
        initial = persistentListOf()
    )
    val topForums by viewModel.uiState.collectPartialAsState(
        prop1 = HomeUiState::topForums,
        initial = persistentListOf()
    )
    val historyForums by viewModel.uiState.collectPartialAsState(
        prop1 = HomeUiState::historyForums,
        initial = persistentListOf()
    )
    val expandHistoryForum by viewModel.uiState.collectPartialAsState(
        prop1 = HomeUiState::expandHistoryForum,
        initial = true
    )
    val error by viewModel.uiState.collectPartialAsState(
        prop1 = HomeUiState::error,
        initial = null
    )
    val isLoggedIn = remember(account) { account != null }
    val isEmpty by remember { derivedStateOf { forums.isEmpty() } }
    val hasTopForum by remember { derivedStateOf { topForums.isNotEmpty() } }
    val showHistoryForum by remember { derivedStateOf { context.appPreferences.homePageShowHistoryForum && historyForums.isNotEmpty() } }
    val isGridMode by remember { HomeForumLayoutState.gridModeState }
    val gridCells = remember(isGridMode) { getGridCells(isGridMode) }
    val isError by remember { derivedStateOf { error != null } }

    onGlobalEvent<GlobalEvent.Refresh>(
        filter = { it.key == "home" }
    ) {
        viewModel.send(HomeUiIntent.Refresh)
    }

    var unfollowForum by remember { mutableStateOf<HomeUiState.Forum?>(null) }
    val confirmUnfollowDialog = rememberDialogState()
    ConfirmDialog(
        dialogState = confirmUnfollowDialog,
        onConfirm = {
            unfollowForum?.let {
                viewModel.send(HomeUiIntent.Unfollow(it.forumId, it.forumName))
            }
        },
    ) {
        Text(
            text = stringResource(
                id = R.string.title_dialog_unfollow_forum,
                unfollowForum?.forumName.orEmpty()
            )
        )
    }

    LaunchedEffect(Unit) {
        if (viewModel.initialized) viewModel.send(HomeUiIntent.RefreshHistory)
    }

    MyScaffold(
        backgroundColor = feedBackdropColor(),
        topBar = {
            Toolbar(
                title = stringResource(id = R.string.title_main),
                eyebrow = "TIEBA / MY FORUMS",
                animateKey = enterTrigger,
                navigationIcon = accountNavIconIfCompact(),
                actions = {
                    ActionItem(
                        contentDescription = stringResource(id = R.string.title_oksign),
                        onClick = {
                            signTrigger++
                            TiebaUtil.startSign(context)
                        },
                    ) {
                        EditorialActionIcon(
                            kind = "sign",
                            tint = ExtendedTheme.colors.onTopBar,
                            trigger = enterTrigger + signTrigger,
                        )
                    }
                    ActionItem(
                        contentDescription = stringResource(
                            id = if (isGridMode) R.string.title_switch_list_single else R.string.title_switch_list_grid
                        ),
                        onClick = {
                            layoutTrigger++
                            HomeForumLayoutState.setGridMode(!isGridMode)
                        },
                    ) {
                        EditorialActionIcon(
                            kind = if (isGridMode) "layout_grid" else "layout_single",
                            tint = ExtendedTheme.colors.onTopBar,
                            trigger = enterTrigger + layoutTrigger,
                            delayMillis = 45,
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { contentPaddings ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = isLoading,
            onRefresh = { viewModel.send(HomeUiIntent.Refresh) }
        )
        Box(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .padding(contentPaddings)
        ) {
            Column {
                SearchBox(
                    modifier = Modifier.padding(bottom = 4.dp),
                    iconTrigger = enterTrigger + searchTrigger,
                ) {
                    searchTrigger++
                    navigator.navigate(SearchPageDestination)
                }
                StateScreen(
                    isEmpty = isEmpty,
                    isError = isError,
                    isLoading = isLoading,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    onReload = {
                        viewModel.send(HomeUiIntent.Refresh)
                    },
                    emptyScreen = {
                        EmptyScreen(
                            loggedIn = isLoggedIn,
                            canOpenExplore = canOpenExplore,
                            onOpenExplore = onOpenExplore
                        )
                    },
                    loadingScreen = {
                        HomePageSkeletonScreen(
                            isGridMode = isGridMode,
                            gridCells = gridCells,
                        )
                    },
                    errorScreen = {
                        error?.let { ErrorScreen(error = it) }
                    }
                ) {
                    MyLazyVerticalGrid(
                        columns = gridCells,
                        contentPadding = forumGridPadding,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        if (showHistoryForum) {
                            item(key = "HistoryForums", span = { GridItemSpan(maxLineSpan) }) {
                                val rotate by animateFloatAsState(
                                    targetValue = if (expandHistoryForum) 90f else 0f,
                                    label = "rotate"
                                )
                                Column {
                                    Row(
                                        verticalAlignment = CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                viewModel.send(
                                                    HomeUiIntent.ToggleHistory(
                                                        expandHistoryForum
                                                    )
                                                )
                                            }
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Header(
                                            text = stringResource(id = R.string.title_history_forum),
                                        )

                                        Spacer(modifier = Modifier.weight(1f))

                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                            contentDescription = stringResource(id = R.string.desc_show),
                                            tint = ExtendedTheme.colors.textSecondary,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .rotate(rotate)
                                        )
                                    }
                                    AnimatedVisibility(visible = expandHistoryForum) {
                                        val pillShape = RoundedCornerShape(100)
                                        val colors = ExtendedTheme.colors
                                        LazyRow(
                                            contentPadding = PaddingValues(vertical = 2.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            items(
                                                historyForums,
                                                key = { it.data }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .clip(pillShape)
                                                        .background(colors.card)
                                                        .border(
                                                            width = 0.6.dp,
                                                            color = colors.divider.copy(alpha = 0.55f),
                                                            shape = pillShape,
                                                        )
                                                        .clickable {
                                                            navigator.navigate(
                                                                ForumPageDestination(
                                                                    it.data
                                                                )
                                                            )
                                                        }
                                                        .padding(
                                                            start = 5.dp,
                                                            end = 10.dp,
                                                            top = 5.dp,
                                                            bottom = 5.dp,
                                                        ),
                                                    verticalAlignment = CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Avatar(
                                                        data = it.avatar,
                                                        contentDescription = null,
                                                        size = 24.dp,
                                                        shape = CircleShape
                                                    )
                                                    Text(
                                                        text = it.title,
                                                        color = colors.text,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Medium,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (hasTopForum) {
                            item(key = "TopForumHeader", span = { GridItemSpan(maxLineSpan) }) {
                                Header(
                                    text = stringResource(id = R.string.title_top_forum),
                                    invert = true,
                                )
                            }
                            items(
                                items = topForums,
                                key = { "Top${it.forumId}" }
                            ) { item ->
                                ForumItem(
                                    item = item,
                                    isGridMode = isGridMode,
                                    onClick = {
                                        navigator.navigate(ForumPageDestination(it.forumName))
                                    },
                                    onUnfollow = {
                                        unfollowForum = it
                                        confirmUnfollowDialog.show()
                                    },
                                    onAddTopForum = {
                                        viewModel.send(HomeUiIntent.TopForums.Add(it))
                                    },
                                    onDeleteTopForum = {
                                        viewModel.send(HomeUiIntent.TopForums.Delete(it.forumId))
                                    },
                                    isTopForum = true
                                )
                            }
                        }
                        if (showHistoryForum || hasTopForum) {
                            item(key = "ForumHeader", span = { GridItemSpan(maxLineSpan) }) {
                                Header(text = stringResource(id = R.string.forum_list_title))
                            }
                        }
                        items(
                            items = forums,
                            key = { it.forumId }
                        ) { item ->
                            ForumItem(
                                item = item,
                                isGridMode = isGridMode,
                                onClick = {
                                    navigator.navigate(ForumPageDestination(it.forumName))
                                },
                                onUnfollow = {
                                    unfollowForum = it
                                    confirmUnfollowDialog.show()
                                },
                                onAddTopForum = {
                                    viewModel.send(HomeUiIntent.TopForums.Add(it))
                                },
                                onDeleteTopForum = {
                                    viewModel.send(HomeUiIntent.TopForums.Delete(it.forumId))
                                }
                            )
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = ExtendedTheme.colors.pullRefreshIndicator,
                contentColor = ExtendedTheme.colors.primary,
            )
        }
    }
}

@Composable
private fun HomePageSkeletonScreen(
    isGridMode: Boolean,
    gridCells: GridCells,
) {
    MyLazyVerticalGrid(
        columns = gridCells,
        contentPadding = forumGridPadding,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "TopForumHeaderPlaceholder", span = { GridItemSpan(maxLineSpan) }) {
            Header(
                text = stringResource(id = R.string.title_top_forum),
                modifier = Modifier.placeholder(
                    visible = true,
                    color = ExtendedTheme.colors.chip,
                    shape = RoundedCornerShape(4.dp),
                ),
                invert = true,
            )
        }
        items(6, key = { "TopPlaceholder$it" }) {
            ForumItemPlaceholder(isGridMode = isGridMode)
        }
        item(key = "ForumHeaderPlaceholder", span = { GridItemSpan(maxLineSpan) }) {
            Header(
                text = stringResource(id = R.string.forum_list_title),
                modifier = Modifier.placeholder(
                    visible = true,
                    color = ExtendedTheme.colors.chip,
                    shape = RoundedCornerShape(4.dp),
                ),
            )
        }
        items(12, key = { "Placeholder$it" }) {
            ForumItemPlaceholder(isGridMode = isGridMode)
        }
    }
}

@Composable
fun EmptyScreen(
    loggedIn: Boolean,
    canOpenExplore: Boolean,
    onOpenExplore: () -> Unit
) {
    val navigator = LocalNavigator.current
    TipScreen(
        title = {
            if (!loggedIn) {
                Text(text = stringResource(id = R.string.title_empty_login))
            } else {
                Text(text = stringResource(id = R.string.title_empty))
            }
        },
        image = {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_astronaut))
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)
            )
        },
        message = {
            if (!loggedIn) {
                Text(
                    text = stringResource(id = R.string.home_empty_login),
                    style = MaterialTheme.typography.body1,
                    color = ExtendedTheme.colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        },
        actions = {
            if (!loggedIn) {
                Button(
                    onClick = {
                        navigator.navigate(LoginPageDestination)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.button_login))
                }
            }
            if (canOpenExplore) {
                TextButton(
                    onClick = onOpenExplore,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.button_go_to_explore))
                }
            }
        },
    )
}
