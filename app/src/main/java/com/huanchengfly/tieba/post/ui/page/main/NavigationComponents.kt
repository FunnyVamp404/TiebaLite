package com.huanchengfly.tieba.post.ui.page.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedColors
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.common.theme.compose.White
import com.huanchengfly.tieba.post.ui.utils.MainNavigationContentPosition
import com.huanchengfly.tieba.post.ui.widgets.compose.AccountNavIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.Avatar
import com.huanchengfly.tieba.post.ui.widgets.compose.EditorialNavIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.Sizes
import com.huanchengfly.tieba.post.utils.AccountUtil.LocalAccount
import kotlinx.collections.immutable.ImmutableList

enum class LayoutType {
    HEADER, CONTENT
}

@Composable
fun PermanentNavigationDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(modifier.fillMaxSize()) {
        drawerContent()
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}

private val ActiveIndicatorHeight = 56.dp
private val ActiveIndicatorWidth = 240.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NavigationDrawerItem(
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    badge: (@Composable () -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color = Color.Transparent,
    selectedBackgroundColor: Color = MaterialTheme.colors.primary.copy(0.25f),
    itemColor: Color = MaterialTheme.colors.onSurface,
    selectedItemColor: Color = MaterialTheme.colors.primary,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier
            .height(ActiveIndicatorHeight)
            .fillMaxWidth(),
        shape = shape,
        color = if (selected) selectedBackgroundColor else backgroundColor,
        interactionSource = interactionSource,
    ) {
        Row(
            Modifier.padding(start = 16.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                val iconColor = if (selected) selectedItemColor else itemColor
                CompositionLocalProvider(LocalContentColor provides iconColor, content = icon)
                Spacer(Modifier.width(12.dp))
            }
            Box(Modifier.weight(1f)) {
                val labelColor = if (selected) selectedItemColor else itemColor
                CompositionLocalProvider(LocalContentColor provides labelColor, content = label)
            }
            if (badge != null) {
                Spacer(Modifier.width(12.dp))
                val badgeColor = if (selected) selectedItemColor else itemColor
                CompositionLocalProvider(LocalContentColor provides badgeColor, content = badge)
            }
        }
    }
}

@Composable
fun NavigationDrawerContent(
    currentPosition: Int,
    onChangePosition: (position: Int) -> Unit,
    onReselected: (position: Int) -> Unit,
    navigationItems: ImmutableList<NavigationItem>,
    navigationContentPosition: MainNavigationContentPosition
) {
    PositionLayout(
        modifier = Modifier
            .width(ActiveIndicatorWidth)
            .background(ExtendedTheme.colors.bottomBar)
            .padding(16.dp),
        content = {
            Column(
                modifier = Modifier
                    .layoutId(LayoutType.HEADER)
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp) // NavigationRailVerticalPadding
            ) {
                val account = LocalAccount.current
                if (account != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        AccountNavIcon(spacer = false, size = Sizes.Large)
                        Text(
                            text = account.nameShow ?: account.name,
                            style = MaterialTheme.typography.subtitle1,
                            color = ExtendedTheme.colors.text
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Avatar(
                            data = R.drawable.ic_launcher_new_round,
                            size = Sizes.Small,
                            contentDescription = stringResource(id = R.string.app_name)
                        )
                        Text(
                            text = stringResource(id = R.string.app_name).uppercase(),
                            style = MaterialTheme.typography.h6,
                            color = ExtendedTheme.colors.primary
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .layoutId(LayoutType.CONTENT)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                navigationItems.fastForEachIndexed { index, navigationItem ->
                    NavigationDrawerItem(
                        selected = index == currentPosition,
                        onClick = {
                            if (index == currentPosition) {
                                onReselected(index)
                            } else {
                                onChangePosition(index)
                            }
                        },
                        label = { Text(text = navigationItem.title(index == currentPosition)) },
                        icon = {
                            Box {
                                EditorialNavIcon(
                                    id = navigationItem.id,
                                    selected = index == currentPosition,
                                    tint = LocalContentColor.current,
                                    modifier = Modifier.size(22.dp),
                                )
                                if (navigationItem.badge) {
                                    Text(
                                        textAlign = TextAlign.Center,
                                        fontSize = 10.sp,
                                        color = White,
                                        text = navigationItem.badgeText ?: "",
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .align(Alignment.TopEnd)
                                            .background(
                                                color = MaterialTheme.colors.secondary,
                                                shape = CircleShape
                                            ),
                                    )
                                }
                            }
                        }
                    )
                }
            }
        },
        navigationContentPosition = navigationContentPosition
    )
}

@Composable
private fun PositionLayout(
    navigationContentPosition: MainNavigationContentPosition,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = { measurables, constraints ->
            lateinit var headerMeasurable: Measurable
            lateinit var contentMeasurable: Measurable
            measurables.forEach {
                when (it.layoutId) {
                    LayoutType.HEADER -> headerMeasurable = it
                    LayoutType.CONTENT -> contentMeasurable = it
                    else -> error("Unknown layoutId encountered!")
                }
            }

            val headerPlaceable = headerMeasurable.measure(constraints)
            val contentPlaceable = contentMeasurable.measure(
                constraints.offset(vertical = -headerPlaceable.height)
            )
            layout(constraints.maxWidth, constraints.maxHeight) {
                // Place the header, this goes at the top
                headerPlaceable.placeRelative(0, 0)

                // Determine how much space is not taken up by the content
                val nonContentVerticalSpace = constraints.maxHeight - contentPlaceable.height

                val contentPlaceableY = when (navigationContentPosition) {
                    // Figure out the place we want to place the content, with respect to the
                    // parent (ignoring the header for now)
                    MainNavigationContentPosition.TOP -> 0
                    MainNavigationContentPosition.CENTER -> nonContentVerticalSpace / 2
                }
                    // And finally, make sure we don't overlap with the header.
                    .coerceAtLeast(headerPlaceable.height)

                contentPlaceable.placeRelative(0, contentPlaceableY)
            }
        }
    )
}

@Composable
fun NavigationRail(
    currentPosition: Int,
    onChangePosition: (position: Int) -> Unit,
    onReselected: (position: Int) -> Unit,
    navigationItems: ImmutableList<NavigationItem>,
    navigationContentPosition: MainNavigationContentPosition
) {
    NavigationRail(
        backgroundColor = ExtendedTheme.colors.bottomBar,
        contentColor = ExtendedTheme.colors.unselected,
        modifier = Modifier
            .fillMaxHeight()
            .statusBarsPadding(),
        elevation = 0.dp,
        header = {
            AccountNavIcon(spacer = false)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = if (navigationContentPosition == MainNavigationContentPosition.TOP) Arrangement.Top else Arrangement.Center
        ) {
            navigationItems.forEachIndexed { index, navigationItem ->
                NavigationRailItem(
                    selected = index == currentPosition,
                    onClick = {
                        if (index == currentPosition) {
                            onReselected(index)
                        } else {
                            onChangePosition(index)
                        }
                    },
                    icon = {
                        Box {
                            val title = navigationItem.title(index == currentPosition)
                            EditorialNavIcon(
                                id = navigationItem.id,
                                selected = index == currentPosition,
                                tint = LocalContentColor.current,
                                modifier = Modifier
                                    .size(22.dp)
                                    .semantics { contentDescription = title },
                            )
                            if (navigationItem.badge) {
                                Text(
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.sp,
                                    color = White,
                                    text = navigationItem.badgeText ?: "",
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .align(Alignment.TopEnd)
                                        .background(
                                            color = MaterialTheme.colors.secondary,
                                            shape = CircleShape
                                        ),
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationDivider(
    themeColors: ExtendedColors = ExtendedTheme.colors
) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(
                themeColors.divider.copy(alpha = if (themeColors.isNightMode) 0.25f else 0.5f)
            )
    )
}

@Composable
fun BottomNavigation(
    currentPosition: Int,
    onChangePosition: (position: Int) -> Unit,
    onReselected: (position: Int) -> Unit,
    navigationItems: ImmutableList<NavigationItem>,
    themeColors: ExtendedColors = ExtendedTheme.colors
) {
    val tabInteractions = navigationItems.map { item ->
        key(item.id) {
            val source = remember { MutableInteractionSource() }
            source to source.collectIsPressedAsState()
        }
    }
    val anyPressed = tabInteractions.any { (_, isPressed) -> isPressed.value }
    val dockSpring = remember { spring<Float>(dampingRatio = 0.72f, stiffness = 270f) }
    val pressure by animateFloatAsState(
        targetValue = if (anyPressed) 1f else 0f,
        animationSpec = dockSpring,
        label = "Dock press pressure",
    )
    val animatedPosition by animateFloatAsState(
        targetValue = currentPosition.toFloat(),
        animationSpec = dockSpring,
        label = "Dock highlight position",
    )
    val dockColor = if (themeColors.isNightMode) {
        lerp(themeColors.card, themeColors.primary, 0.14f).copy(alpha = 0.93f)
    } else {
        lerp(Color.White, themeColors.primary, 0.05f).copy(alpha = 0.94f)
    }
    val rimColor = if (themeColors.isNightMode) {
        lerp(Color.White, themeColors.primary, 0.48f).copy(alpha = 0.22f)
    } else {
        lerp(Color.Black, themeColors.primary, 0.25f).copy(alpha = 0.10f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(min = 240.dp, max = 336.dp)
                .fillMaxWidth()
                .height(58.dp)
                .graphicsLayer {
                    scaleX = 1f + pressure * 0.022f
                    scaleY = 1f + pressure * 0.065f
                    translationY = -pressure * 0.6.dp.toPx()
                }
                .shadow(
                    elevation = if (themeColors.isNightMode) 14.dp else 10.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.45f),
                    spotColor = Color.Black.copy(alpha = 0.45f),
                )
                .clip(CircleShape)
                .background(dockColor)
                .border(width = 0.75.dp, color = rimColor, shape = CircleShape)
                .padding(horizontal = 10.dp),
        ) {
            if (navigationItems.isNotEmpty()) {
                val segmentWidth = maxWidth / navigationItems.size
                Box(
                    modifier = Modifier
                        .offset { IntOffset((segmentWidth * animatedPosition).roundToPx(), 0) }
                        .width(segmentWidth)
                        .fillMaxHeight()
                        .padding(horizontal = 2.dp, vertical = 4.dp)
                        .background(
                            color = themeColors.primary.copy(
                                alpha = if (themeColors.isNightMode) 0.16f else 0.11f
                            ),
                            shape = CircleShape,
                        )
                        .border(
                            width = 0.6.dp,
                            color = themeColors.primary.copy(alpha = 0.25f),
                            shape = CircleShape,
                        ),
                )
            }
            Row(
                modifier = Modifier.fillMaxSize().selectableGroup(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                navigationItems.fastForEachIndexed { index, navigationItem ->
                    val isSelected = index == currentPosition
                    val indicatorWidth by animateDpAsState(
                        targetValue = if (isSelected) 18.dp else 0.dp,
                        label = "NavIndicatorWidth",
                    )
                    val labelLetterSpacing by animateFloatAsState(
                        targetValue = if (isSelected) 1.4f else 0f,
                        animationSpec = spring(dampingRatio = 0.55f, stiffness = 360f),
                        label = "Navigation label letter spacing",
                    )
                    val labelScaleX by animateFloatAsState(
                        targetValue = if (isSelected) 1.06f else 1f,
                        animationSpec = spring(dampingRatio = 0.55f, stiffness = 360f),
                        label = "Navigation label horizontal scale",
                    )
                    val animatedContentColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            themeColors.primary
                        } else {
                            themeColors.textSecondary.copy(
                                alpha = if (themeColors.isNightMode) 0.65f else 0.70f
                            )
                        },
                        label = "Navigation content color",
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .semantics { selected = isSelected }
                            .clickable(
                                interactionSource = tabInteractions[index].first,
                                indication = null,
                                role = Role.Tab,
                            ) {
                                if (isSelected) {
                                    onReselected(index)
                                } else {
                                    onChangePosition(index)
                                }
                                navigationItem.onClick?.invoke()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(modifier = Modifier.size(21.dp)) {
                            EditorialNavIcon(
                                id = navigationItem.id,
                                selected = isSelected,
                                tint = animatedContentColor,
                                modifier = Modifier.size(21.dp),
                            )
                            if (navigationItem.badge) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .offset(x = 14.dp, y = (-5).dp)
                                        .wrapContentWidth(Alignment.Start, unbounded = true)
                                        .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp)
                                        .background(color = themeColors.accent, shape = CircleShape)
                                        .padding(horizontal = 4.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = navigationItem.badgeText.orEmpty(),
                                        color = White,
                                        fontSize = 10.sp,
                                        lineHeight = 12.sp,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = navigationItem.title(isSelected),
                            modifier = Modifier.graphicsLayer { scaleX = labelScaleX },
                            fontSize = 10.sp,
                            letterSpacing = labelLetterSpacing.sp,
                            lineHeight = 12.sp,
                            maxLines = 1,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                            color = animatedContentColor,
                            style = MaterialTheme.typography.caption,
                        )
                        Spacer(Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .width(indicatorWidth)
                                .height(2.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) animatedContentColor else Color.Transparent),
                        )
                    }
                }
            }
        }
    }
}

@Immutable
data class NavigationItem @OptIn(ExperimentalAnimationGraphicsApi::class) constructor(
    val id: String,
    val icon: @Composable () -> AnimatedImageVector,
    val title: @Composable (selected: Boolean) -> String,
    val badge: Boolean = false,
    val badgeText: String? = null,
    val onClick: (() -> Unit)? = null,
    val content: @Composable () -> Unit = {},
)
