package com.huanchengfly.tieba.post.ui.page.settings.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.huanchengfly.tieba.post.BuildConfig
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.toastShort
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.widgets.compose.BackNavigationIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.EditorialChevronRightIcon
import com.huanchengfly.tieba.post.ui.widgets.compose.MyScaffold
import com.huanchengfly.tieba.post.ui.widgets.compose.TitleCentredToolbar
import com.huanchengfly.tieba.post.ui.widgets.compose.feedBackdropColor
import com.huanchengfly.tieba.post.ui.widgets.compose.feedCardSurface
import com.huanchengfly.tieba.post.utils.appPreferences
import com.huanchengfly.tieba.post.utils.launchUrl
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

private const val SOURCE_CODE_URL = "https://github.com/HuanCheng65/TiebaLite"

@Destination
@Composable
fun AboutPage(
    navigator: DestinationsNavigator,
) {
    val context = LocalContext.current
    val colors = ExtendedTheme.colors
    var lastClickTime by remember { mutableLongStateOf(0L) }
    var clickCount by remember { mutableIntStateOf(0) }

    MyScaffold(
        backgroundColor = feedBackdropColor(),
        topBar = {
            TitleCentredToolbar(
                title = {
                    Text(
                        text = stringResource(id = R.string.title_about),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.h6
                    )
                },
                eyebrow = "TIEBA / ABOUT",
                navigationIcon = {
                    BackNavigationIcon(onBackPressed = { navigator.navigateUp() })
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .feedCardSurface()
                    .padding(vertical = 28.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Image(
                    painter = rememberDrawablePainter(
                        drawable = context.getDrawable(R.mipmap.ic_launcher_new)
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(76.dp).clip(RoundedCornerShape(18.dp)),
                )
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.text,
                )
                Text(
                    text = stringResource(id = R.string.tip_about, BuildConfig.VERSION_NAME),
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100))
                        .background(colors.chip)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastClickTime < 500) {
                                    clickCount++
                                } else {
                                    clickCount = 1
                                }
                                lastClickTime = currentTime
                                if (clickCount >= 7) {
                                    clickCount = 0
                                    context.appPreferences.showExperimentalFeatures =
                                        !context.appPreferences.showExperimentalFeatures
                                    if (context.appPreferences.showExperimentalFeatures) {
                                        context.toastShort(R.string.toast_experimental_features_enabled)
                                    } else {
                                        context.toastShort(R.string.toast_experimental_features_disabled)
                                    }
                                }
                            },
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .feedCardSurface()
                    .clickable { launchUrl(context, navigator, SOURCE_CODE_URL) }
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = stringResource(R.string.source_code),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.text,
                    )
                    Text(
                        text = SOURCE_CODE_URL,
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                    )
                }
                EditorialChevronRightIcon(tint = colors.textSecondary.copy(alpha = 0.65f))
            }
        }
    }
}
