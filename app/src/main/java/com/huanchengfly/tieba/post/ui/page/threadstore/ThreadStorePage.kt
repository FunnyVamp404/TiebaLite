package com.huanchengfly.tieba.post.ui.page.threadstore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.api.models.ThreadStoreBean
import com.huanchengfly.tieba.post.arch.collectPartialAsState
import com.huanchengfly.tieba.post.arch.onEvent
import com.huanchengfly.tieba.post.arch.pageViewModel
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.common.theme.compose.pullRefreshIndicator
import com.huanchengfly.tieba.post.ui.page.destinations.ThreadPageDestination
import com.huanchengfly.tieba.post.ui.page.destinations.UserProfilePageDestination
import com.huanchengfly.tieba.post.ui.page.thread.ThreadPageFrom
import com.huanchengfly.tieba.post.ui.page.thread.ThreadPageFromStoreExtra
import com.huanchengfly.tieba.post.ui.page.thread.ThreadSortType
import com.huanchengfly.tieba.post.ui.widgets.compose.Avatar
import com.huanchengfly.tieba.post.ui.widgets.compose.BackNavigationIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.EditorialPageHeader
import com.huanchengfly.tieba.post.ui.widgets.compose.ErrorScreen
import com.huanchengfly.tieba.post.ui.widgets.compose.LazyLoad
import com.huanchengfly.tieba.post.ui.widgets.compose.LoadMoreLayout
import com.huanchengfly.tieba.post.ui.widgets.compose.LongClickMenu
import com.huanchengfly.tieba.post.ui.widgets.compose.MyLazyColumn
import com.huanchengfly.tieba.post.ui.widgets.compose.MyScaffold
import com.huanchengfly.tieba.post.ui.widgets.compose.Sizes
import com.huanchengfly.tieba.post.ui.widgets.compose.TitleCentredToolbar
import com.huanchengfly.tieba.post.ui.widgets.compose.UserHeader
import com.huanchengfly.tieba.post.ui.widgets.compose.feedBackdropColor
import com.huanchengfly.tieba.post.ui.widgets.compose.feedCardSurface
import com.huanchengfly.tieba.post.ui.widgets.compose.states.StateScreen
import com.huanchengfly.tieba.post.utils.StringUtil
import com.huanchengfly.tieba.post.utils.StringUtil.getUsernameAnnotatedString
import com.huanchengfly.tieba.post.utils.appPreferences
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private val UpdateTipTextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 10.sp)

@OptIn(ExperimentalMaterialApi::class, ExperimentalTextApi::class)
@Destination(
    deepLinks = [
        DeepLink(uriPattern = "tblite://favorite")
    ]
)
@Composable
fun ThreadStorePage(
    navigator: DestinationsNavigator,
    viewModel: ThreadStoreViewModel = pageViewModel()
) {
    LazyLoad(loaded = viewModel.initialized) {
        viewModel.send(ThreadStoreUiIntent.Refresh)
        viewModel.initialized = true
    }
    val isRefreshing by viewModel.uiState.collectPartialAsState(
        prop1 = ThreadStoreUiState::isRefreshing,
        initial = false
    )
    val isLoadingMore by viewModel.uiState.collectPartialAsState(
        prop1 = ThreadStoreUiState::isLoadingMore,
        initial = false
    )
    val hasMore by viewModel.uiState.collectPartialAsState(
        prop1 = ThreadStoreUiState::hasMore,
        initial = true
    )
    val currentPage by viewModel.uiState.collectPartialAsState(
        prop1 = ThreadStoreUiState::currentPage,
        initial = 0
    )
    val data by viewModel.uiState.collectPartialAsState(
        prop1 = ThreadStoreUiState::data,
        initial = emptyList()
    )
    val error by viewModel.uiState.collectPartialAsState(
        prop1 = ThreadStoreUiState::error,
        initial = null
    )
    val isError by remember { derivedStateOf { error != null } }

    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    viewModel.onEvent<ThreadStoreUiEvent.Delete.Failure> {
        scaffoldState.snackbarHostState.showSnackbar(
            context.getString(
                R.string.delete_store_failure,
                it.errorMsg
            )
        )
    }
    viewModel.onEvent<ThreadStoreUiEvent.Delete.Success> {
        scaffoldState.snackbarHostState.showSnackbar(context.getString(R.string.delete_store_success))
    }
    MyScaffold(
        backgroundColor = feedBackdropColor(),
        scaffoldState = scaffoldState,
        topBar = {
            TitleCentredToolbar(
                title = {
                    EditorialPageHeader(
                        title = stringResource(id = R.string.title_my_collect),
                        eyebrow = "TIEBA / FAVORITES",
                        centered = true
                    )
                },
                navigationIcon = {
                    BackNavigationIcon(onBackPressed = { navigator.navigateUp() })
                }
            )
        }
    ) { contentPaddings ->
        StateScreen(
            isEmpty = data.isEmpty(),
            isError = isError,
            isLoading = isRefreshing,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPaddings),
            onReload = {
                viewModel.send(ThreadStoreUiIntent.Refresh)
            },
            errorScreen = {
                error?.let {
                    val (e) = it
                    ErrorScreen(error = e)
                }
            }
        ) {
            val pullRefreshState = rememberPullRefreshState(
                refreshing = isRefreshing,
                onRefresh = { viewModel.send(ThreadStoreUiIntent.Refresh) }
            )

            val lazyListState = rememberLazyListState()

            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                LoadMoreLayout(
                    isLoading = isLoadingMore,
                    onLoadMore = { viewModel.send(ThreadStoreUiIntent.LoadMore(currentPage + 1)) },
                    loadEnd = !hasMore,
                    lazyListState = lazyListState
                ) {
                    MyLazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(
                            items = data,
                            key = { it.threadId }
                        ) { info ->
                            StoreItem(
                                info = info,
                                onUserClick = {
                                    info.author.lzUid?.let {
                                        navigator.navigate(UserProfilePageDestination(it.toLong()))
                                    }
                                },
                                onClick = {
                                    navigator.navigate(
                                        ThreadPageDestination(
                                            threadId = info.threadId.toLong(),
                                            postId = info.markPid.toLong(),
                                            seeLz = context.appPreferences.collectThreadSeeLz,
                                            sortType = if(context.appPreferences.collectThreadDescSort) ThreadSortType.SORT_TYPE_DESC else ThreadSortType.SORT_TYPE_DEFAULT,
                                            from = ThreadPageFrom.FROM_STORE,
                                            extra = ThreadPageFromStoreExtra(
                                                maxPid = info.maxPid.toLong(),
                                                maxFloor = info.postNo.toInt()
                                            )
                                        )
                                    )
                                },
                                onDelete = {
                                    viewModel.send(
                                        ThreadStoreUiIntent.Delete(
                                            info.threadId
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    backgroundColor = ExtendedTheme.colors.pullRefreshIndicator,
                    contentColor = ExtendedTheme.colors.primary,
                )
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun StoreItem(
    info: ThreadStoreBean.ThreadStoreInfo,
    onUserClick: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasUpdate = remember(info) { info.count != "0" && info.postNo != "0" }
    val isDeleted = remember(info) { info.isDeleted == "1" }
    val colors = ExtendedTheme.colors
    val textMeasurer = rememberTextMeasurer()
    LongClickMenu(
        modifier = modifier
            .fillMaxWidth()
            .feedCardSurface(),
        menuContent = {
            DropdownMenuItem(onClick = onDelete) {
                Text(text = stringResource(id = R.string.title_collect_on))
            }
        },
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UserHeader(
                avatar = {
                    Avatar(
                        data = StringUtil.getAvatarUrl(info.author.userPortrait),
                        size = Sizes.Small,
                        contentDescription = null,
                        shape = RoundedCornerShape(8.dp)
                    )
                },
                name = {
                    Text(
                        text = getUsernameAnnotatedString(
                            LocalContext.current,
                            info.author.name ?: "",
                            info.author.nameShow,
                            colors.textSecondary
                        ),
                        color = colors.textSecondary
                    )
                },
                onClick = onUserClick,
            )
            val title = remember(info, hasUpdate) {
                buildAnnotatedString {
                    append(info.title)
                    if (hasUpdate) {
                        append(" ")
                        appendInlineContent("Update", info.postNo)
                    }
                }
            }
            val updateTip = stringResource(
                id = R.string.tip_thread_store_update,
                info.postNo
            )
            val result = remember(textMeasurer, updateTip) {
                textMeasurer.measure(
                    AnnotatedString(updateTip),
                    style = UpdateTipTextStyle
                ).size
            }
            val badgeHorizontalPadding = 8.dp
            val badgeVerticalPadding = 2.dp
            val density = LocalDensity.current
            val width = with(density) {
                (result.width + 2 * badgeHorizontalPadding.roundToPx() + 1).toSp()
            }
            val height = with(density) {
                (result.height + 2 * badgeVerticalPadding.roundToPx() + 1).toSp()
            }
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDeleted) colors.textDisabled else colors.text,
                inlineContent = mapOf(
                    "Update" to InlineTextContent(
                        placeholder = Placeholder(
                            width = width,
                            height = height,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                        ),
                        children = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        color = colors.primary.copy(
                                            alpha = if (colors.isNightMode) 0.18f else 0.12f
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(
                                        horizontal = badgeHorizontalPadding,
                                        vertical = badgeVerticalPadding
                                    )
                            ) {
                                Text(
                                    text = updateTip,
                                    style = UpdateTipTextStyle,
                                    color = colors.primary,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    )
                )
            )
            if (isDeleted) {
                Text(
                    text = stringResource(id = R.string.tip_thread_store_deleted),
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
            }
        }
    }
}
