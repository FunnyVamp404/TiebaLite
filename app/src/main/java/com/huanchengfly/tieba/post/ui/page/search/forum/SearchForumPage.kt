package com.huanchengfly.tieba.post.ui.page.search.forum

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.api.models.SearchForumBean
import com.huanchengfly.tieba.post.arch.collectPartialAsState
import com.huanchengfly.tieba.post.arch.onGlobalEvent
import com.huanchengfly.tieba.post.arch.pageViewModel
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.common.theme.compose.pullRefreshIndicator
import com.huanchengfly.tieba.post.ui.page.LocalNavigator
import com.huanchengfly.tieba.post.ui.page.destinations.ForumPageDestination
import com.huanchengfly.tieba.post.ui.page.search.SearchUiEvent
import com.huanchengfly.tieba.post.ui.widgets.compose.Avatar
import com.huanchengfly.tieba.post.ui.widgets.compose.EditorialChevronRightIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.ErrorScreen
import com.huanchengfly.tieba.post.ui.widgets.compose.LazyLoad
import com.huanchengfly.tieba.post.ui.widgets.compose.LocalShouldLoad
import com.huanchengfly.tieba.post.ui.widgets.compose.MyLazyColumn
import com.huanchengfly.tieba.post.ui.widgets.compose.Sizes
import com.huanchengfly.tieba.post.ui.widgets.compose.feedBackdropColor
import com.huanchengfly.tieba.post.ui.widgets.compose.feedCardSurface
import com.huanchengfly.tieba.post.ui.widgets.compose.states.StateScreen
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun SearchForumPage(
    keyword: String,
    viewModel: SearchForumViewModel = pageViewModel(),
) {
    val navigator = LocalNavigator.current
    LazyLoad(loaded = viewModel.initialized) {
        viewModel.send(SearchForumUiIntent.Refresh(keyword))
        viewModel.initialized = true
    }
    val currentKeyword by viewModel.uiState.collectPartialAsState(
        prop1 = SearchForumUiState::keyword,
        initial = ""
    )
    val isRefreshing by viewModel.uiState.collectPartialAsState(
        prop1 = SearchForumUiState::isRefreshing,
        initial = true
    )
    val error by viewModel.uiState.collectPartialAsState(
        prop1 = SearchForumUiState::error,
        initial = null
    )
    val exactMatchForum by viewModel.uiState.collectPartialAsState(
        prop1 = SearchForumUiState::exactMatchForum,
        initial = null
    )
    val fuzzyMatchForumList by viewModel.uiState.collectPartialAsState(
        prop1 = SearchForumUiState::fuzzyMatchForumList,
        initial = persistentListOf()
    )

    val showExactMatchResult by remember {
        derivedStateOf { exactMatchForum != null }
    }
    val showFuzzyMatchResult by remember {
        derivedStateOf { fuzzyMatchForumList.isNotEmpty() }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.send(SearchForumUiIntent.Refresh(keyword)) }
    )

    val isEmpty by remember {
        derivedStateOf { !showExactMatchResult && !showFuzzyMatchResult }
    }

    onGlobalEvent<SearchUiEvent.KeywordChanged> {
        viewModel.send(SearchForumUiIntent.Refresh(it.keyword))
    }
    val shouldLoad = LocalShouldLoad.current
    LaunchedEffect(currentKeyword) {
        if (currentKeyword.isNotEmpty() && keyword != currentKeyword) {
            if (shouldLoad) {
                viewModel.send(SearchForumUiIntent.Refresh(keyword))
            } else {
                viewModel.initialized = false
            }
        }
    }

    StateScreen(
        modifier = Modifier.fillMaxSize(),
        isEmpty = isEmpty,
        isError = error != null,
        isLoading = isRefreshing,
        onReload = { viewModel.send(SearchForumUiIntent.Refresh(keyword)) },
        errorScreen = {
            error?.let {
                val (e) = it
                ErrorScreen(error = e)
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            MyLazyColumn(
                modifier = Modifier.fillMaxSize().background(feedBackdropColor()),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (showExactMatchResult) {
                    stickyHeader(key = "ExactMatchHeader") {
                        Text(
                            text = stringResource(id = R.string.title_exact_match),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(feedBackdropColor())
                                .padding(vertical = 4.dp),
                            color = ExtendedTheme.colors.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    item(key = "ExactMatch") {
                        SearchForumItem(
                            item = exactMatchForum!!,
                            onClick = {
                                navigator.navigate(ForumPageDestination(exactMatchForum!!.forumName.orEmpty()))
                            }
                        )
                    }
                }
                if (showFuzzyMatchResult) {
                    stickyHeader(key = "FuzzyMatchHeader") {
                        Text(
                            text = stringResource(id = R.string.title_fuzzy_match),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(feedBackdropColor())
                                .padding(vertical = 4.dp),
                            color = ExtendedTheme.colors.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    items(fuzzyMatchForumList) {
                        SearchForumItem(
                            item = it,
                            onClick = {
                                navigator.navigate(ForumPageDestination(it.forumName.orEmpty()))
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

@Composable
private fun SearchForumItem(
    item: SearchForumBean.ForumInfoBean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .feedCardSurface()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Avatar(
            data = item.avatar,
            size = Sizes.Medium,
            contentDescription = item.forumNameShow,
            shape = RoundedCornerShape(10.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.title_forum, item.forumNameShow.orEmpty()),
                style = MaterialTheme.typography.subtitle1,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
            if (!item.intro.isNullOrEmpty()) {
                Text(
                    text = item.slogan.orEmpty(),
                    style = MaterialTheme.typography.body2,
                    color = ExtendedTheme.colors.textSecondary,
                    maxLines = 1
                )
            }
        }
        EditorialChevronRightIcon(tint = ExtendedTheme.colors.textSecondary.copy(alpha = 0.65f))
    }
}