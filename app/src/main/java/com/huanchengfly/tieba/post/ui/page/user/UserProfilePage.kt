package com.huanchengfly.tieba.post.ui.page.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.NoAccounts
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.api.models.protos.User
import com.huanchengfly.tieba.post.arch.BaseComposeActivity.Companion.LocalWindowSizeClass
import com.huanchengfly.tieba.post.arch.GlobalEvent
import com.huanchengfly.tieba.post.arch.ImmutableHolder
import com.huanchengfly.tieba.post.arch.collectPartialAsState
import com.huanchengfly.tieba.post.arch.emitGlobalEvent
import com.huanchengfly.tieba.post.arch.getOrNull
import com.huanchengfly.tieba.post.arch.pageViewModel
import com.huanchengfly.tieba.post.goToActivity
import com.huanchengfly.tieba.post.models.database.Block
import com.huanchengfly.tieba.post.toastShort
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.common.windowsizeclass.WindowWidthSizeClass
import com.huanchengfly.tieba.post.ui.page.LocalNavigator
import com.huanchengfly.tieba.post.ui.page.ProvideNavigator
import com.huanchengfly.tieba.post.ui.page.destinations.WebViewPageDestination
import com.huanchengfly.tieba.post.ui.page.user.edit.EditProfileActivity
import com.huanchengfly.tieba.post.ui.page.user.likeforum.UserLikeForumPage
import com.huanchengfly.tieba.post.ui.page.user.post.UserPostPage
import com.huanchengfly.tieba.post.ui.widgets.compose.Avatar
import com.huanchengfly.tieba.post.ui.widgets.compose.BackNavigationIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.Button
import com.huanchengfly.tieba.post.ui.widgets.compose.ClickMenu
import com.huanchengfly.tieba.post.ui.widgets.compose.ErrorScreen
import com.huanchengfly.tieba.post.ui.widgets.compose.LazyLoad
import com.huanchengfly.tieba.post.ui.widgets.compose.LazyLoadHorizontalPager
import com.huanchengfly.tieba.post.ui.widgets.compose.MyScaffold
import com.huanchengfly.tieba.post.ui.widgets.compose.PagerTabIndicator
import com.huanchengfly.tieba.post.ui.widgets.compose.ProvideContentColor
import com.huanchengfly.tieba.post.ui.widgets.compose.PullToRefreshLayout
import com.huanchengfly.tieba.post.ui.widgets.compose.ScrollableTabRow
import com.huanchengfly.tieba.post.ui.widgets.compose.Sizes
import com.huanchengfly.tieba.post.ui.widgets.compose.Toolbar
import com.huanchengfly.tieba.post.ui.widgets.compose.UserHeader
import com.huanchengfly.tieba.post.ui.widgets.compose.feedBackdropColor
import com.huanchengfly.tieba.post.ui.widgets.compose.feedCardSurface
import com.huanchengfly.tieba.post.ui.widgets.compose.states.StateScreen
import com.huanchengfly.tieba.post.utils.AccountUtil.LocalAccount
import com.huanchengfly.tieba.post.utils.BlockManager
import com.huanchengfly.tieba.post.utils.StringUtil
import com.huanchengfly.tieba.post.utils.StringUtil.getShortNumString
import com.huanchengfly.tieba.post.utils.TiebaUtil
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

@Destination
@Composable
fun UserProfilePage(
    uid: Long,
    initialTab: Int = 0,
    navigator: DestinationsNavigator,
    viewModel: UserProfileViewModel = pageViewModel(),
) {
    val account = LocalAccount.current

    val isSelf = remember(account, uid) {
        account?.uid == uid.toString()
    }

    LazyLoad(loaded = viewModel.initialized) {
        viewModel.send(UserProfileUiIntent.Refresh(uid))
        viewModel.initialized = true
    }

    val isRefreshing by viewModel.uiState.collectPartialAsState(
        prop1 = UserProfileUiState::isRefreshing,
        initial = true
    )
    val error by viewModel.uiState.collectPartialAsState(
        prop1 = UserProfileUiState::error,
        initial = null
    )
    val user by viewModel.uiState.collectPartialAsState(
        prop1 = UserProfileUiState::user,
        initial = null
    )
    val disableButton by viewModel.uiState.collectPartialAsState(
        prop1 = UserProfileUiState::disableButton,
        initial = false
    )

    val isError by remember {
        derivedStateOf { error != null }
    }
    val isEmpty by remember {
        derivedStateOf { user == null }
    }

    ProvideNavigator(navigator = navigator) {
        StateScreen(
            modifier = Modifier.fillMaxSize().background(feedBackdropColor()),
            isEmpty = isEmpty,
            isError = isError,
            isLoading = isRefreshing,
            onReload = { viewModel.send(UserProfileUiIntent.Refresh(uid)) },
            errorScreen = { ErrorScreen(error = error.getOrNull()) }
        ) {
            user?.let {
                UserProfileContent(
                    user = it,
                    showActionBtn = account != null,
                    disableButton = disableButton,
                    isSelf = isSelf,
                    initialTab = initialTab,
                    onBack = { navigator.navigateUp() },
                    onFollow = {
                        viewModel.send(
                            UserProfileUiIntent.Follow(
                                it.get { portrait },
                                account!!.tbs,
                            )
                        )
                    },
                    onUnfollow = {
                        viewModel.send(
                            UserProfileUiIntent.Unfollow(
                                it.get { portrait },
                                account!!.tbs,
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun UserProfileContent(
    user: ImmutableHolder<User>,
    showActionBtn: Boolean,
    disableButton: Boolean,
    isSelf: Boolean,
    initialTab: Int = 0,
    onBack: () -> Unit,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit,
) {
    when (LocalWindowSizeClass.current.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> {
            UserProfileContentExpanded(
                user = user,
                showActionBtn = showActionBtn,
                disableButton = disableButton,
                isSelf = isSelf,
                initialTab = initialTab,
                onBack = onBack,
                onFollow = onFollow,
                onUnfollow = onUnfollow
            )
        }

        else -> {
            UserProfileContentNormal(
                user = user,
                showActionBtn = showActionBtn,
                disableButton = disableButton,
                isSelf = isSelf,
                initialTab = initialTab,
                onBack = onBack,
                onFollow = onFollow,
                onUnfollow = onUnfollow
            )
        }
    }
}

@Composable
private fun UserProfileToolbar(
    user: ImmutableHolder<User>,
    isSelf: Boolean,
    showTitle: Boolean,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    Toolbar(
        title = {
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ToolbarUserTitle(user = user)
            }
        },
        navigationIcon = {
            BackNavigationIcon(onBackPressed = onBack)
        },
        actions = {
            user.takeUnless { isSelf }?.let {
                ClickMenu(
                    menuContent = {
                        DropdownMenuItem(
                            onClick = {
                                BlockManager.addBlockAsync(
                                    Block(
                                        category = Block.CATEGORY_BLACK_LIST,
                                        type = Block.TYPE_USER,
                                        username = it.get { name },
                                        uid = it.get { id }.toString()
                                    )
                                ) {
                                    if (it) context.toastShort(R.string.toast_add_success)
                                }
                            }
                        ) {
                            Text(text = stringResource(id = R.string.menu_add_user_to_black_list))
                        }
                        DropdownMenuItem(
                            onClick = {
                                BlockManager.addBlockAsync(
                                    Block(
                                        category = Block.CATEGORY_WHITE_LIST,
                                        type = Block.TYPE_USER,
                                        username = it.get { name },
                                        uid = it.get { id }.toString()
                                    )
                                ) {
                                    if (it) context.toastShort(R.string.toast_add_success)
                                }
                            }
                        ) {
                            Text(text = stringResource(id = R.string.menu_add_user_to_white_list))
                        }
                    },
                    triggerShape = CircleShape
                ) {
                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.NoAccounts,
                            contentDescription = stringResource(id = R.string.btn_block)
                        )
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserProfileContentNormal(
    user: ImmutableHolder<User>,
    showActionBtn: Boolean,
    disableButton: Boolean,
    isSelf: Boolean,
    initialTab: Int = 0,
    onBack: () -> Unit,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    var heightOffset by rememberSaveable { mutableFloatStateOf(0f) }
    var headerHeight by rememberSaveable {
        mutableFloatStateOf(
            with(density) {
                (96.dp + 16.dp).toPx()
            }
        )
    }

    val isShowHeaderArea by remember {
        derivedStateOf {
            heightOffset.absoluteValue < headerHeight
        }
    }

    MyScaffold(
        backgroundColor = feedBackdropColor(),
        topBar = {
            UserProfileToolbar(
                user = user,
                isSelf = isSelf,
                showTitle = !isShowHeaderArea,
                onBack = onBack
            )
        }
    ) { paddingValues ->
        var isFakeRefreshing by remember { mutableStateOf(false) }

        LaunchedEffect(isFakeRefreshing) {
            if (isFakeRefreshing) {
                delay(1000)
                isFakeRefreshing = false
            }
        }

        PullToRefreshLayout(
            refreshing = isFakeRefreshing,
            onRefresh = {
                coroutineScope.emitGlobalEvent(GlobalEvent.Refresh(key = "user_profile"))
                isFakeRefreshing = true
            }
        ) {
            val headerNestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource,
                    ): Offset {
                        if (available.y < 0) {
                            val prevHeightOffset = heightOffset
                            heightOffset = max(heightOffset + available.y, -headerHeight)
                            if (prevHeightOffset != heightOffset) {
                                return available.copy(x = 0f)
                            }
                        }

                        return Offset.Zero
                    }

                    override fun onPostScroll(
                        consumed: Offset,
                        available: Offset,
                        source: NestedScrollSource,
                    ): Offset {
                        if (available.y > 0f) {
                            // Adjust the height offset in case the consumed delta Y is less than what was
                            // recorded as available delta Y in the pre-scroll.
                            val prevHeightOffset = heightOffset
                            heightOffset = min(heightOffset + available.y, 0f)
                            if (prevHeightOffset != heightOffset) {
                                return available.copy(x = 0f)
                            }
                        }

                        return Offset.Zero
                    }
                }
            }

            ProvideContentColor(color = ExtendedTheme.colors.text) {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .nestedScroll(headerNestedScrollConnection)
                ) {
                    val pages = remember {
                        listOfNotNull(
                            UserProfilePageData(
                                id = "threads",
                                title = {
                                    stringResource(
                                        id = R.string.title_profile_threads_tab,
                                        it.get { thread_num }.getShortNumString()
                                    )
                                },
                                content = { user, fluid ->
                                    UserProfilePostList(
                                        uid = user.get { id },
                                        isThread = true,
                                        fluid = fluid,
                                    )
                                }
                            ),
                            UserProfilePageData(
                                id = "posts",
                                title = {
                                    stringResource(
                                        id = R.string.title_profile_posts_tab,
                                        it.get { post_num }.getShortNumString()
                                    )
                                },
                                content = { user, fluid ->
                                    UserProfilePostList(
                                        uid = user.get { id },
                                        isThread = false,
                                        fluid = fluid,
                                    )
                                }
                            ).takeIf { isSelf },
                            UserProfilePageData(
                                id = "concern_forums",
                                title = {
                                    stringResource(
                                        id = R.string.title_profile_concern_forums_tab,
                                        it.get { my_like_num }.toString()
                                    )
                                },
                                content = { user, fluid ->
                                    UserLikeForumPage(
                                        uid = user.get { id },
                                        fluid = fluid,
                                    )
                                }
                            ),
                        ).toImmutableList()
                    }
                    val pagerState = rememberPagerState(
                        initialPage = initialTab.coerceIn(0, (pages.size - 1).coerceAtLeast(0))
                    ) { pages.size }

                    val containerHeight by remember {
                        derivedStateOf {
                            with(density) {
                                (headerHeight + heightOffset).toDp()
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .height(containerHeight)
                            .clipToBounds()
                    ) {
                        Box(
                            modifier = Modifier
                                .wrapContentHeight(
                                    align = Alignment.Bottom,
                                    unbounded = true
                                )
                                .onSizeChanged {
                                    headerHeight = it.height.toFloat()
                                }
                        ) {
                            UserProfileDetail(
                                user = user,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                showBtn = showActionBtn,
                                isSelf = isSelf,
                                onBtnClick = {
                                    if (disableButton || !showActionBtn) {
                                        return@UserProfileDetail
                                    }
                                    if (isSelf) {
                                        context.goToActivity<EditProfileActivity>()
                                    } else if (user.get { has_concerned } == 0) {
                                        onFollow()
                                    } else {
                                        onUnfollow()
                                    }
                                },
                                onCopyIdClick = {
                                    TiebaUtil.copyText(
                                        context,
                                        user.get { id }.toString()
                                    )
                                }
                            )
                        }
                    }

                    UserProfileTabRow(
                        user = user,
                        pages = pages,
                        pagerState = pagerState,
                    )

                    LazyLoadHorizontalPager(
                        state = pagerState,
                        key = { pages[it].id },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        pages[it].content(user, false)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserProfileContentExpanded(
    user: ImmutableHolder<User>,
    showActionBtn: Boolean,
    disableButton: Boolean,
    isSelf: Boolean,
    initialTab: Int = 0,
    onBack: () -> Unit,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    MyScaffold(
        backgroundColor = feedBackdropColor(),
        topBar = {
            UserProfileToolbar(
                user = user,
                isSelf = isSelf,
                showTitle = false,
                onBack = onBack
            )
        }
    ) { paddingValues ->
        ProvideContentColor(color = ExtendedTheme.colors.text) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxHeight()
                        .fillMaxWidth(0.75f)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val pages = remember {
                        listOfNotNull(
                            UserProfilePageData(
                                id = "threads",
                                title = {
                                    stringResource(
                                        id = R.string.title_profile_threads_tab,
                                        it.get { thread_num }.getShortNumString()
                                    )
                                },
                                content = { user, expanded ->
                                    UserProfilePostList(
                                        uid = user.get { id },
                                        isThread = true,
                                        fluid = expanded,
                                    )
                                }
                            ),
                            UserProfilePageData(
                                id = "posts",
                                title = {
                                    stringResource(
                                        id = R.string.title_profile_posts_tab,
                                        it.get { post_num }.getShortNumString()
                                    )
                                },
                                content = { user, expanded ->
                                    UserProfilePostList(
                                        uid = user.get { id },
                                        isThread = false,
                                        fluid = expanded,
                                    )
                                }
                            ).takeIf { isSelf },
                            UserProfilePageData(
                                id = "concern_forums",
                                title = {
                                    stringResource(
                                        id = R.string.title_profile_concern_forums_tab,
                                        it.get { my_like_num }.toString()
                                    )
                                },
                                content = { user, expanded ->
                                    UserLikeForumPage(
                                        uid = user.get { id },
                                        fluid = expanded,
                                        enablePullRefresh = expanded,
                                    )
                                }
                            ),
                        ).toImmutableList()
                    }
                    val pagerState = rememberPagerState(
                        initialPage = initialTab.coerceIn(0, (pages.size - 1).coerceAtLeast(0))
                    ) { pages.size }

                    UserProfileDetail(
                        user = user,
                        modifier = Modifier
                            .weight(1.5f)
                            .align(Alignment.Top)
                            .padding(top = 8.dp),
                        showBtn = showActionBtn,
                        isSelf = isSelf,
                        onBtnClick = {
                            if (disableButton || !showActionBtn) {
                                return@UserProfileDetail
                            }
                            if (isSelf) {
                                context.goToActivity<EditProfileActivity>()
                            } else if (user.get { has_concerned } == 0) {
                                onFollow()
                            } else {
                                onUnfollow()
                            }
                        },
                        onCopyIdClick = {
                            TiebaUtil.copyText(
                                context,
                                user.get { id }.toString()
                            )
                        }
                    )

                    Column(
                        modifier = Modifier.weight(2f)
                    ) {
                        UserProfileTabRow(
                            user = user,
                            pages = pages,
                            pagerState = pagerState,
                        )

                        LazyLoadHorizontalPager(
                            state = pagerState,
                            key = { pages[it].id },
                            modifier = Modifier.weight(1f)
                        ) {
                            pages[it].content(user, true)
                        }
                    }
                }
            }
        }
    }
}

@Immutable
data class UserProfilePageData(
    val id: String,
    val title: @Composable (ImmutableHolder<User>) -> String,
    val content: @Composable (ImmutableHolder<User>, Boolean) -> Unit,
)

@Composable
private fun UserProfilePostList(uid: Long, isThread: Boolean, fluid: Boolean) {
    // UserPostPage 内部的 FeedCard 已是独立纸感卡片，这里不再额外套卡片，避免卡中卡
    UserPostPage(
        uid = uid,
        isThread = isThread,
        fluid = fluid,
        enablePullRefresh = fluid,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserProfileTabRow(
    user: ImmutableHolder<User>,
    pages: ImmutableList<UserProfilePageData>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    val tabTextStyle = MaterialTheme.typography.button.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        letterSpacing = 0.sp
    )

    Column(modifier = modifier.fillMaxWidth()) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                PagerTabIndicator(
                    pagerState = pagerState,
                    tabPositions = tabPositions
                )
            },
            divider = {},
            backgroundColor = Color.Transparent,
            contentColor = ExtendedTheme.colors.primary,
            edgePadding = 12.dp,
        ) {
            pages.fastForEachIndexed { i, pageData ->
                Tab(
                    selected = pagerState.currentPage == i,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(i)
                        }
                    },
                    selectedContentColor = ExtendedTheme.colors.primary,
                    unselectedContentColor = ExtendedTheme.colors.textSecondary,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .height(44.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(text = pageData.title(user), style = tabTextStyle)
                    }
                }
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(0.6.dp)
                .background(ExtendedTheme.colors.divider.copy(alpha = 0.5f))
        )
    }
}

@Composable
private fun ToolbarUserTitle(
    user: ImmutableHolder<User>,
    modifier: Modifier = Modifier,
) {
    UserHeader(
        avatar = {
            Avatar(
                data = StringUtil.getAvatarUrl(user.get { portrait }),
                size = Sizes.Small,
                contentDescription = null
            )
        },
        name = {
            Text(
                text = StringUtil.getUsernameAnnotatedString(
                    LocalContext.current,
                    user.get { name },
                    user.get { nameShow },
                    LocalContentColor.current
                )
            )
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UserProfileDetail(
    user: ImmutableHolder<User>,
    modifier: Modifier = Modifier,
    showBtn: Boolean = true,
    isSelf: Boolean = false,
    onBtnClick: () -> Unit = {},
    onCopyIdClick: () -> Unit = {},
) {
    val colors = ExtendedTheme.colors
    val intro = user.get { intro }
    val ipAddress = user.get { ip_address }
    val tiebaAge = user.get { tb_age }
    val isFollowing = user.get { has_concerned } != 0
    val navigator = LocalNavigator.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .feedCardSurface()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Avatar(
                data = StringUtil.getBigAvatarUrl(user.get { portrait }),
                size = 64.dp,
                contentDescription = null,
                modifier = Modifier.border(
                    width = 0.8.dp,
                    color = colors.divider.copy(alpha = 0.6f),
                    shape = CircleShape,
                )
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = StringUtil.getUsernameAnnotatedString(
                        LocalContext.current,
                        user.get { name },
                        user.get { nameShow },
                        colors.text,
                    ),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val verification = user.getNullableImmutable { bazhu_grade }?.get { desc }
                    ?: user.getNullableImmutable { new_god_data }
                        ?.takeIf { it.get { status } != 0 }
                        ?.let {
                            stringResource(id = R.string.text_god_verify, it.get { field_name })
                        }
                if (verification != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Verified,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = colors.primary,
                        )
                        Text(
                            text = verification,
                            fontSize = 12.sp,
                            color = colors.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            if (showBtn) {
                Button(
                    onClick = onBtnClick,
                    elevation = null,
                    shape = RoundedCornerShape(100),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (isSelf || isFollowing) {
                            colors.chip.copy(alpha = 0.7f)
                        } else {
                            colors.primary
                        },
                        contentColor = when {
                            isSelf -> colors.text
                            isFollowing -> colors.textSecondary
                            else -> colors.onAccent
                        },
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    if (isSelf || !isFollowing) {
                        Icon(
                            imageVector = if (isSelf) Icons.Rounded.Edit else Icons.Rounded.Add,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = stringResource(
                            id = when {
                                isSelf -> R.string.menu_edit_info
                                isFollowing -> R.string.button_unfollow
                                else -> R.string.button_follow
                            }
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                    )
                }
            }
        }
        Text(
            text = intro.takeIf { it.isNotEmpty() } ?: stringResource(id = R.string.tip_no_intro),
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = if (intro.isNotEmpty()) colors.textSecondary else colors.textDisabled,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            when (user.get { sex }) {
                1 -> ProfileMetaPill(text = "♂")
                2 -> ProfileMetaPill(text = "♀")
            }
            ProfileMetaPill(
                text = stringResource(
                    id = R.string.text_profile_user_id,
                    user.get { tieba_uid }.toString()
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = colors.textSecondary,
                    )
                },
                onClick = onCopyIdClick,
            )
            if (ipAddress.isNotEmpty()) {
                ProfileMetaPill(
                    text = stringResource(id = R.string.text_profile_ip_location, ipAddress),
                )
            }
            if (tiebaAge.isNotEmpty()) {
                ProfileMetaPill(
                    text = stringResource(id = R.string.text_profile_tb_age, tiebaAge),
                )
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(0.6.dp)
                .background(colors.divider.copy(alpha = 0.5f))
        )
        val statDivider = Modifier
            .width(0.6.dp)
            .height(20.dp)
            .background(colors.divider.copy(alpha = 0.45f))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            UserProfileStat(
                number = user.get { concern_num }.getShortNumString(),
                label = stringResource(id = R.string.text_stat_follow),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        navigator.navigate(
                            WebViewPageDestination(
                                initialUrl = "https://tieba.baidu.com/home/concern?id=${user.get { portrait }}&fr=home"
                            )
                        )
                    }
            )
            Box(statDivider)
            UserProfileStat(
                number = user.get { fans_num }.getShortNumString(),
                label = stringResource(id = R.string.text_stat_fans),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        navigator.navigate(
                            WebViewPageDestination(
                                initialUrl = "https://tieba.baidu.com/home/fans?id=${user.get { portrait }}&fr=home"
                            )
                        )
                    }
            )
            Box(statDivider)
            UserProfileStat(
                number = user.get { total_agree_num }.getShortNumString(),
                label = stringResource(id = R.string.text_stat_agrees),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ProfileMetaPill(
    text: String,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val colors = ExtendedTheme.colors
    val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.chip.copy(alpha = 0.55f))
            .then(clickModifier)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(text = text, fontSize = 11.sp, color = colors.textSecondary)
        trailingIcon?.invoke()
    }
}

@Composable
private fun UserProfileStat(number: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = number,
            fontSize = 17.sp,
            color = ExtendedTheme.colors.text,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = ExtendedTheme.colors.textSecondary,
        )
    }
}
