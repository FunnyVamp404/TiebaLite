package com.huanchengfly.tieba.post.ui.page.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.huanchengfly.tieba.post.LocalDevicePosture
import com.huanchengfly.tieba.post.LocalNotificationCountFlow
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.arch.BaseComposeActivity.Companion.LocalWindowSizeClass
import com.huanchengfly.tieba.post.arch.GlobalEvent
import com.huanchengfly.tieba.post.arch.collectPartialAsState
import com.huanchengfly.tieba.post.arch.emitGlobalEvent
import com.huanchengfly.tieba.post.arch.pageViewModel
import com.huanchengfly.tieba.post.rememberPreferenceAsState
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.common.windowsizeclass.WindowHeightSizeClass
import com.huanchengfly.tieba.post.ui.common.windowsizeclass.WindowWidthSizeClass
import com.huanchengfly.tieba.post.ui.page.ProvideNavigator
import com.huanchengfly.tieba.post.ui.page.main.explore.ExplorePage
import com.huanchengfly.tieba.post.ui.page.main.home.HomePage
import com.huanchengfly.tieba.post.ui.page.main.notifications.NotificationsPage
import com.huanchengfly.tieba.post.ui.page.main.user.UserPage
import com.huanchengfly.tieba.post.ui.utils.DevicePosture
import com.huanchengfly.tieba.post.ui.utils.MainNavigationContentPosition
import com.huanchengfly.tieba.post.ui.utils.MainNavigationType
import com.huanchengfly.tieba.post.ui.widgets.compose.LocalArchiveMotionProgress
import com.huanchengfly.tieba.post.ui.widgets.compose.LocalFloatingBottomBarPadding
import com.huanchengfly.tieba.post.ui.widgets.compose.MyScaffold
import com.huanchengfly.tieba.post.ui.widgets.compose.ProvideShouldLoad
import com.huanchengfly.tieba.post.ui.widgets.compose.archivePlaneTransition
import com.huanchengfly.tieba.post.ui.widgets.compose.feedBackdropColor
import com.huanchengfly.tieba.post.utils.appPreferences
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

val LocalMainTabActive = compositionLocalOf { true }

@Composable
private fun NavigationWrapper(
    currentPosition: Int,
    onChangePosition: (position: Int) -> Unit,
    onReselected: (position: Int) -> Unit,
    navigationItems: ImmutableList<NavigationItem>,
    navigationType: MainNavigationType,
    navigationContentPosition: MainNavigationContentPosition,
    content: @Composable () -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == MainNavigationType.PERMANENT_NAVIGATION_DRAWER) {
            NavigationDrawerContent(
                currentPosition = currentPosition,
                onChangePosition = onChangePosition,
                onReselected = onReselected,
                navigationItems = navigationItems,
                navigationContentPosition = navigationContentPosition
            )
        }
        AnimatedVisibility(visible = navigationType == MainNavigationType.NAVIGATION_RAIL) {
            NavigationRail(
                currentPosition = currentPosition,
                onChangePosition = onChangePosition,
                onReselected = onReselected,
                navigationItems = navigationItems,
                navigationContentPosition = navigationContentPosition
            )
        }
        Column(modifier = Modifier
            .weight(1f)
            .fillMaxHeight()) {
            content()
        }
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun MainPage(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = pageViewModel<MainUiIntent, MainViewModel>(emptyList()),
) {
    val windowSizeClass = LocalWindowSizeClass.current
    val windowHeightSizeClass by rememberUpdatedState(newValue = windowSizeClass.heightSizeClass)
    val windowWidthSizeClass by rememberUpdatedState(newValue = windowSizeClass.widthSizeClass)
    val foldingDevicePosture by LocalDevicePosture.current

    val messageCount by viewModel.uiState.collectPartialAsState(
        prop1 = MainUiState::messageCount,
        initial = 0
    )

    val notificationCountFlow = LocalNotificationCountFlow.current
    LaunchedEffect(null) {
        notificationCountFlow.collect {
            viewModel.send(MainUiIntent.NewMessage.Receive(it))
        }
    }

    val hideExplore by rememberPreferenceAsState(
        key = booleanPreferencesKey("hideExplore"),
        defaultValue = LocalContext.current.appPreferences.hideExplore
    )
    val tabIds by remember {
        derivedStateOf {
            if (hideExplore) listOf("home", "notification", "user")
            else listOf("home", "explore", "notification", "user")
        }
    }
    val defaultTab = LocalContext.current.appPreferences.defaultTab ?: "home"
    val initialPage = tabIds.indexOf(defaultTab).coerceAtLeast(0)
    var currentTab by rememberSaveable { mutableIntStateOf(initialPage) }
    var outgoingTab by remember { mutableStateOf<Int?>(null) }
    var tabDirection by remember { mutableFloatStateOf(1f) }
    val activeTab = currentTab.coerceIn(tabIds.indices)
    val visitedTabs = remember { mutableStateListOf(tabIds[activeTab]) }
    val pageTransition = remember { Animatable(1f) }
    var transitionJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()

    fun selectTab(index: Int) {
        val previousTab = currentTab.coerceIn(tabIds.indices)
        if (index == previousTab || index !in tabIds.indices) return

        transitionJob?.cancel()
        tabDirection = if (index > previousTab) 1f else -1f
        outgoingTab = previousTab
        currentTab = index
        val tabId = tabIds[index]
        if (tabId !in visitedTabs) visitedTabs.add(tabId)
        transitionJob = coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            pageTransition.snapTo(0f)
            pageTransition.animateTo(1f, tween(durationMillis = 420, easing = LinearEasing))
            outgoingTab = null
        }
    }

    LaunchedEffect(tabIds) {
        transitionJob?.cancel()
        outgoingTab = null
        currentTab = currentTab.coerceIn(tabIds.indices)
        val tabId = tabIds[currentTab]
        if (tabId !in visitedTabs) visitedTabs.add(tabId)
        pageTransition.snapTo(1f)
    }

    val themeColors = ExtendedTheme.colors
    val navigationItems by remember {
        derivedStateOf {
            listOfNotNull(
                NavigationItem(
                    id = "home",
                    icon = { AnimatedImageVector.animatedVectorResource(id = R.drawable.ic_animated_rounded_inventory_2) },
                    title = { stringResource(id = R.string.title_main) },
                    content = {
                        HomePage(
                            canOpenExplore = !hideExplore
                        ) {
                            selectTab(tabIds.indexOf("explore"))
                        }
                    }
                ),
                if (hideExplore) null
                else NavigationItem(
                    id = "explore",
                    icon = {
                        AnimatedImageVector.animatedVectorResource(id = R.drawable.ic_animated_toy_fans)
                    },
                    title = { stringResource(id = R.string.title_explore) },
                    content = {
                        ExplorePage()
                    }
                ),
                NavigationItem(
                    id = "notification",
                    icon = {
                        AnimatedImageVector.animatedVectorResource(id = R.drawable.ic_animated_rounded_notifications)
                    },
                    title = { stringResource(id = R.string.title_notifications) },
                    badge = messageCount > 0,
                    badgeText = "$messageCount",
                    onClick = {
                        viewModel.send(MainUiIntent.NewMessage.Clear)
                    },
                    content = {
                        NotificationsPage()
                    }
                ),
                NavigationItem(
                    id = "user",
                    icon = {
                        AnimatedImageVector.animatedVectorResource(id = R.drawable.ic_animated_rounded_person)
                    },
                    title = { stringResource(id = R.string.title_user) },
                    content = {
                        UserPage()
                    }
                ),
            ).toImmutableList()
        }
    }
    val navigationType by remember {
        derivedStateOf {
            when (windowWidthSizeClass) {
                WindowWidthSizeClass.Compact -> {
                    MainNavigationType.BOTTOM_NAVIGATION
                }

                WindowWidthSizeClass.Medium -> {
                    MainNavigationType.NAVIGATION_RAIL
                }

                WindowWidthSizeClass.Expanded -> {
                    if (foldingDevicePosture is DevicePosture.BookPosture) {
                        MainNavigationType.NAVIGATION_RAIL
                    } else {
                        MainNavigationType.PERMANENT_NAVIGATION_DRAWER
                    }
                }

                else -> {
                    MainNavigationType.BOTTOM_NAVIGATION
                }
            }
        }
    }

    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val floatingBottomPadding = if (navigationType == MainNavigationType.BOTTOM_NAVIGATION) {
        86.dp + navBarBottomPadding
    } else {
        0.dp
    }

    /**
     * Content inside Navigation Rail/Drawer can also be positioned at top, bottom or center for
     * ergonomics and reachability depending upon the height of the device.
     */
    val navigationContentPosition by remember {
        derivedStateOf {
            when (windowHeightSizeClass) {
                WindowHeightSizeClass.Compact -> {
                    MainNavigationContentPosition.TOP
                }

                WindowHeightSizeClass.Medium,
                WindowHeightSizeClass.Expanded -> {
                    MainNavigationContentPosition.CENTER
                }

                else -> {
                    MainNavigationContentPosition.TOP
                }
            }
        }
    }
    val onReselected: (Int) -> Unit = {
        coroutineScope.emitGlobalEvent(
            GlobalEvent.Refresh(navigationItems[it].id)
        )
    }
    ProvideNavigator(navigator = navigator) {
        NavigationWrapper(
            currentPosition = activeTab,
            onChangePosition = ::selectTab,
            onReselected = onReselected,
            navigationItems = navigationItems,
            navigationType = navigationType,
            navigationContentPosition = navigationContentPosition
        ) {
            CompositionLocalProvider(LocalFloatingBottomBarPadding provides floatingBottomPadding) {
                MyScaffold(
                    backgroundColor = Color.Transparent,
                    modifier = Modifier.fillMaxSize(),
                ) { paddingValues ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                            for (pageIndex in navigationItems.indices) {
                                val item = navigationItems[pageIndex]
                                val active = pageIndex == activeTab
                                if (!active && item.id !in visitedTabs) continue

                                key(item.id) {
                                    val outgoing = !active && pageIndex == outgoingTab
                                    val progress = if (active || outgoing) pageTransition.value else 1f
                                    val transition = if (active || outgoing) {
                                        Modifier.archivePlaneTransition(progress, outgoing, tabDirection)
                                    } else {
                                        Modifier
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .zIndex(
                                                when {
                                                    active -> 2f
                                                    outgoing -> 1f
                                                    else -> 0f
                                                }
                                            )
                                            .layout { measurable, constraints ->
                                                val placeable = measurable.measure(constraints)
                                                layout(placeable.width, placeable.height) {
                                                    if (active || outgoing) placeable.place(0, 0)
                                                }
                                            }
                                            .then(transition)
                                            .background(feedBackdropColor()),
                                    ) {
                                        CompositionLocalProvider(
                                            LocalMainTabActive provides active,
                                            LocalArchiveMotionProgress provides if (active) progress else 1f,
                                        ) {
                                            ProvideShouldLoad(shouldLoad = active) {
                                                item.content()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        AnimatedVisibility(
                            visible = navigationType == MainNavigationType.BOTTOM_NAVIGATION,
                            modifier = Modifier.align(Alignment.BottomCenter),
                        ) {
                            BottomNavigation(
                                currentPosition = activeTab,
                                onChangePosition = ::selectTab,
                                onReselected = onReselected,
                                navigationItems = navigationItems,
                                themeColors = themeColors,
                            )
                        }
                    }
                }
            }
        }
    }
    BackHandler(enabled = activeTab != initialPage) {
        selectTab(initialPage)
    }
}

