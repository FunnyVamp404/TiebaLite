package com.huanchengfly.tieba.post.ui.common.prefs

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.widgets.compose.feedCardSurface

@Composable
@ExperimentalMaterialApi
fun PrefsListItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    darkenOnDisable: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    textColor: Color = ExtendedTheme.colors.text,
    minimalHeight: Boolean = false,
    text: @Composable () -> Unit
) {

    val typography = MaterialTheme.typography

    val styledIcon = @Composable {
        val alpha = when {
            enabled || !darkenOnDisable -> ContentAlpha.high
            else -> ContentAlpha.disabled
        }
        CompositionLocalProvider(LocalContentAlpha provides alpha) {
            icon?.invoke()
        }
    }

    val styledText = applyTextStyle(
        typography.subtitle1.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        ),
        textColor,
        when {
            enabled || !darkenOnDisable -> ContentAlpha.high
            else -> ContentAlpha.disabled
        },
        text
    )!!
    val styledSecondaryText = applyTextStyle(
        typography.body2.copy(
            fontSize = 11.sp,
            lineHeight = 15.sp,
        ),
        ExtendedTheme.colors.textSecondary,
        when {
            enabled || !darkenOnDisable -> ContentAlpha.high
            else -> ContentAlpha.disabled
        },
        secondaryText
    )
    val styledTrailing = applyTextStyle(
        typography.caption,
        textColor,
        when {
            enabled || !darkenOnDisable -> ContentAlpha.high
            else -> ContentAlpha.disabled
        },
        trailing
    )

    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .feedCardSurface(),
    ) {
        AnyLine.CustomListItem(
            modifier = modifier,
            minimalHeight,
            styledIcon,
            styledSecondaryText,
            styledTrailing,
            styledText
        )
    }
}

private object AnyLine {
    private val MinHeight = 48.dp
    private val MinHeightSmaller = 32.dp
    private val HorizontalPadding = 14.dp
    private val VerticalPadding = 13.dp

    @Composable
    fun CustomListItem(
        modifier: Modifier = Modifier,
        minimalHeight: Boolean = true,
        icon: @Composable (() -> Unit)? = null,
        secondaryText: (@Composable (() -> Unit))? = null,
        trailing: @Composable (() -> Unit)? = null,
        text: @Composable (() -> Unit)
    ) {
        Row(
            modifier
                .heightIn(min = if (minimalHeight) MinHeightSmaller else MinHeight)
                .padding(
                    horizontal = HorizontalPadding,
                    vertical = if (minimalHeight) 8.dp else VerticalPadding,
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon != null) {
                Box(contentAlignment = Alignment.Center) { icon() }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                text()
                secondaryText?.invoke()
            }

            if (trailing != null) {
                trailing.invoke()
            }
        }
    }
}

private fun applyTextStyle(
    textStyle: TextStyle,
    textColor: Color,
    contentAlpha: Float,
    content: @Composable (() -> Unit)?
): @Composable (() -> Unit)? {
    if (content == null) return null
    return {
        val newTextStyle = textStyle.copy(
            color = textColor.copy(alpha = contentAlpha)
        )
        CompositionLocalProvider(
            LocalContentAlpha provides contentAlpha,
            LocalTextStyle provides newTextStyle
        ) {
            content()
        }
    }
}



