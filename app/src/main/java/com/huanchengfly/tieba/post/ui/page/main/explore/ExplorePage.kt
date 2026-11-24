package com.huanchengfly.tieba.post.ui.page.main.explore

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.arch.GlobalEvent
import com.huanchengfly.tieba.post.arch.emitGlobalEvent
import com.huanchengfly.tieba.post.arch.onGlobalEvent
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.page.LocalNavigator
import com.huanchengfly.tieba.post.ui.page.destinations.SearchPageDestination
import com.huanchengfly.tieba.post.ui.page.main.explore.concern.ConcernPage
import com.huanchengfly.tieba.post.ui.page.main.explore.hot.HotPage
import com.huanchengfly.tieba.post.ui.page.main.explore.personalized.PersonalizedPage
import com.huanchengfly.tieba.post.ui.widgets.compose.ActionItem
import com.huanchengfly.tieba.post.ui.widgets.compose.LazyLoadHorizontalPager
import com.huanchengfly.tieba.post.ui.widgets.compose.Toolbar
import com.huanchengfly.tieba.post.ui.widgets.compose.accountNavIconIfCompact
import com.huanchengfly.tieba.post.utils.AccountUtil.LocalAccount
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch


@Immutable
data class ExplorePageItem(
    val id: String,
    val name: @Composable (selected: Boolean) -> Unit,
    val content: @Composable () -> Unit,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ColumnScope.ExplorePageTab(
    pagerState: PagerState,
    pages: ImmutableList<ExplorePageItem>
) {
    val coroutineScope = rememberCoroutineScope()
    val colors = ExtendedTheme.colors

    Row(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 2.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        pages.fastForEachIndexed { index, item ->
            val isSelected = pagerState.currentPage == index
            val pill = Modifier.clip(CircleShape)
            val pillStyle = if (isSelected) {
                pill
                    .background(
                        if (colors.isNightMode) Color.White.copy(alpha = 0.11f)
                        else colors.primary.copy(alpha = 0.10f)
                    )
                    .border(
                        width = 0.75.dp,
                        color = if (colors.isNightMode) Color.White.copy(alpha = 0.16f)
                        else colors.primary.copy(alpha = 0.22f),
                        shape = CircleShape
                    )
            } else {
                pill
            }
            Box(
                modifier = pillStyle
                    .semantics { selected = isSelected }
                    .clickable(role = Role.Tab) {
                        coroutineScope.launch {
                            if (pagerState.currentPage == index) {
                                emitGlobalEvent(GlobalEvent.Refresh(item.id))
                            } else {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                item.name(isSelected)
            }
        }
    }
}

@Composable
private fun TabText(
    text: String,
    selected: Boolean
) {
    val colors = ExtendedTheme.colors
    val style = MaterialTheme.typography.button.copy(
        fontSize = 14.sp,
        letterSpacing = 0.75.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        textAlign = TextAlign.Center
    )
    Text(
        text = text,
        color = if (selected) colors.onTopBar else colors.onTopBarSecondary.copy(alpha = 0.78f),
        style = style
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExplorePage() {
    val account = LocalAccount.current
    val navigator = LocalNavigator.current

    val loggedIn = remember(account) { account != null }

    val pages = remember {
        listOfNotNull(
            if (loggedIn) ExplorePageItem(
                "concern",
                { TabText(text = stringResource(id = R.string.title_concern), selected = it) },
                { ConcernPage() }
            ) else null,
            ExplorePageItem(
                "personalized",
                { TabText(text = stringResource(id = R.string.title_personalized), selected = it) },
                { PersonalizedPage() }
            ),
            ExplorePageItem(
                "hot",
                { TabText(text = stringResource(id = R.string.title_hot), selected = it) },
                { HotPage() }
            ),
        ).toImmutableList()
    }
    val pagerState = rememberPagerState(initialPage = if (account != null) 1 else 0) { pages.size }
    val coroutineScope = rememberCoroutineScope()

    onGlobalEvent<GlobalEvent.Refresh>(
        filter = { it.key == "explore" }
    ) {
        coroutineScope.emitGlobalEvent(GlobalEvent.Refresh(pages[pagerState.currentPage].id))
    }

    Scaffold(
        backgroundColor = Color.Transparent,
        topBar = {
            Toolbar(
                title = stringResource(id = R.string.title_explore),
                navigationIcon = accountNavIconIfCompact(),
                actions = {
                    ActionItem(
                        icon = Icons.Rounded.Search,
                        contentDescription = stringResource(id = R.string.title_search)
                    ) {
                        navigator.navigate(SearchPageDestination)
                    }
                },
            ) {
                ExplorePageTab(pagerState = pagerState, pages = pages)
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        LazyLoadHorizontalPager(
            contentPadding = paddingValues,
            state = pagerState,
            key = { pages[it].id },
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top,
            userScrollEnabled = true,
        ) {
            pages[it].content()
        }
    }
}