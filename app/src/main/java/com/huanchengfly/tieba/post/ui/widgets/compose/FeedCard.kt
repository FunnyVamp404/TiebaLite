package com.huanchengfly.tieba.post.ui.widgets.compose

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.OndemandVideo
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PhotoSizeSelectActual
import androidx.compose.material.icons.rounded.SwapCalls
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.huanchengfly.tieba.post.App
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.api.models.protos.Media
import com.huanchengfly.tieba.post.api.models.protos.OriginThreadInfo
import com.huanchengfly.tieba.post.api.models.protos.PostInfoList
import com.huanchengfly.tieba.post.api.models.protos.SimpleForum
import com.huanchengfly.tieba.post.api.models.protos.ThreadInfo
import com.huanchengfly.tieba.post.api.models.protos.User
import com.huanchengfly.tieba.post.api.models.protos.VideoInfo
import com.huanchengfly.tieba.post.api.models.protos.abstractText
import com.huanchengfly.tieba.post.api.models.protos.renders
import com.huanchengfly.tieba.post.arch.BaseComposeActivity.Companion.LocalWindowSizeClass
import com.huanchengfly.tieba.post.arch.ImmutableHolder
import com.huanchengfly.tieba.post.arch.wrapImmutable
import com.huanchengfly.tieba.post.findActivity
import com.huanchengfly.tieba.post.goToActivity
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.common.windowsizeclass.WindowWidthSizeClass
import com.huanchengfly.tieba.post.ui.page.photoview.PhotoViewActivity
import com.huanchengfly.tieba.post.ui.utils.getPhotoViewData
import com.huanchengfly.tieba.post.ui.utils.rememberSystemUiController
import com.huanchengfly.tieba.post.ui.widgets.compose.placeholder.PlaceholderHighlight
import com.huanchengfly.tieba.post.ui.widgets.compose.placeholder.fade
import com.huanchengfly.tieba.post.ui.widgets.compose.placeholder.placeholder
import com.huanchengfly.tieba.post.ui.widgets.compose.video.DefaultVideoPlayerController
import com.huanchengfly.tieba.post.ui.widgets.compose.video.OnFullScreenModeChangedListener
import com.huanchengfly.tieba.post.ui.widgets.compose.video.VideoPlayerSource
import com.huanchengfly.tieba.post.ui.widgets.compose.video.rememberVideoPlayerController
import com.huanchengfly.tieba.post.utils.DateTimeUtils
import com.huanchengfly.tieba.post.utils.EmoticonUtil.emoticonString
import com.huanchengfly.tieba.post.utils.ImageUtil
import com.huanchengfly.tieba.post.utils.StringUtil
import com.huanchengfly.tieba.post.utils.StringUtil.getShortNumString
import com.huanchengfly.tieba.post.utils.appPreferences
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.math.max
import kotlin.math.min

private val ImmutableHolder<Media>.url: String
    get() = ImageUtil.getUrl(
        App.INSTANCE,
        true,
        get { originPic },
        get { bigPic },
        get { dynamicPic },
        get { srcPic }
    )

@Composable
private fun UserHeader(
    userProvider: () -> ImmutableHolder<User>,
    timeProvider: () -> Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val context = LocalContext.current
    val user = remember(userProvider) { userProvider() }
    val time = remember(timeProvider) { timeProvider() }
    UserHeader(
        avatar = {
            Avatar(
                data = user.get { StringUtil.getAvatarUrl(portrait) },
                size = Sizes.Small,
                contentDescription = null
            )
        },
        name = {
            Text(
                text = StringUtil.getUsernameAnnotatedString(
                    context = LocalContext.current,
                    username = user.get { name },
                    nickname = user.get { nameShow },
                    color = LocalContentColor.current
                ),
                color = ExtendedTheme.colors.text
            )
        },
        onClick = onClick,
        desc = {
            Text(
                text = DateTimeUtils.getRelativeTimeString(
                    context,
                    time.toString()
                )
            )
        },
        content = content,
        modifier = modifier
    )
}

@Composable
fun UserHeader(
    nameProvider: () -> String,
    nameShowProvider: () -> String,
    portraitProvider: () -> String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    timeProvider: (() -> Int)? = null,
    content: @Composable RowScope.() -> Unit = {},
) {
    val context = LocalContext.current
    val name = remember(nameProvider) { nameProvider() }
    val nameShow = remember(nameShowProvider) { nameShowProvider() }
    val portrait = remember(portraitProvider) { portraitProvider() }
    val time = remember(timeProvider) { timeProvider?.invoke() }
    UserHeader(
        avatar = {
            Avatar(
                data = StringUtil.getAvatarUrl(portrait),
                size = Sizes.Small,
                contentDescription = null
            )
        },
        name = {
            Text(
                text = StringUtil.getUsernameAnnotatedString(
                    context = LocalContext.current,
                    username = name,
                    nickname = nameShow,
                    color = LocalContentColor.current
                ),
                color = ExtendedTheme.colors.text
            )
        },
        onClick = onClick,
        desc = (@Composable {
            Text(
                text = DateTimeUtils.getRelativeTimeString(
                    context,
                    time.toString()
                )
            )
        }).takeIf { time != null },
        content = content,
        modifier = modifier
    )
}


/** Canvas color behind feed cards. */
@Composable
fun feedBackdropColor(): Color {
    val colors = ExtendedTheme.colors
    return if (colors.isNightMode) {
        if (colors.background == Color.Black) Color(0xFF0A0B0E)
        else lerp(colors.background, Color(0xFF0D0F13), 0.55f)
    } else {
        lerp(colors.background, Color(0xFFF6F5F1), 0.65f)
    }
}

/** Rounded card surface with a subtle border. Callers add spacing between cards. */
@Composable
fun Modifier.feedCardSurface(): Modifier {
    val colors = ExtendedTheme.colors
    val shape = RoundedCornerShape(18.dp)
    val surfaceColor = if (colors.isNightMode) {
        if (colors.background == Color.Black) Color(0xFF14161B)
        else lerp(colors.card, Color(0xFF1A1D24), 0.40f).copy(alpha = 0.84f)
    } else {
        Color.White.copy(alpha = 0.86f)
    }
    val borderColor = if (colors.isNightMode) {
        Color.White.copy(alpha = 0.09f)
    } else {
        Color.Black.copy(alpha = 0.07f)
    }
    return this
        .clip(shape)
        .background(surfaceColor)
        .border(width = 0.75.dp, color = borderColor, shape = shape)
}

@Composable
fun Card(
    modifier: Modifier = Modifier,
    header: @Composable ColumnScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {},
    action: @Composable (ColumnScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
) {
    val cardModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    val paddingModifier = if (action != null) Modifier.padding(top = 14.dp, bottom = 4.dp)
    else Modifier.padding(vertical = 14.dp)

    Column(
        modifier = Modifier
            .then(modifier)
            .then(cardModifier)
            .then(paddingModifier)
            .padding(contentPadding)
    ) {
        header()
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 10.dp)
        ) {
            content()
        }
        if (action != null) {
            val dividerColor = if (ExtendedTheme.colors.isNightMode) {
                Color.White.copy(alpha = 0.06f)
            } else {
                Color.Black.copy(alpha = 0.05f)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(dividerColor)
            )
            Spacer(modifier = Modifier.height(2.dp))
            action.invoke(this)
        }
    }
}

@Composable
fun Badge(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(100),
    backgroundColor: Color = Color.Black.copy(0.5f),
    contentColor: Color = Color.White,
) {
    Row(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(12.dp)
        )
        Text(text = text, fontSize = 12.sp, color = contentColor)
    }
}

@Composable
fun ThreadContent(
    modifier: Modifier = Modifier,
    title: String = "",
    abstractText: String = "",
    tabName: String = "",
    showTitle: Boolean = true,
    showAbstract: Boolean = true,
    isGood: Boolean = false,
    maxLines: Int = 5,
    highlightKeywords: ImmutableList<String> = persistentListOf(),
) {
    Column(
        modifier = Modifier.fillMaxWidth().then(modifier),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showTitle) {
            val titleAnnotatedString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.SemiBold,
                        color = ExtendedTheme.colors.text
                    )
                ) {
                    if (isGood) {
                        withStyle(style = SpanStyle(color = ExtendedTheme.colors.accent)) {
                            append(stringResource(id = R.string.tip_good))
                            append(" ")
                        }
                    }

                    if (tabName.isNotBlank()) {
                        withStyle(
                            style = SpanStyle(
                                color = ExtendedTheme.colors.textSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append(tabName)
                            append(" · ")
                        }
                    }

                    append(title)
                }
            }
            HighlightText(
                text = titleAnnotatedString,
                fontSize = 16.sp,
                lineHeight = 23.sp,
                maxLines = if (showAbstract) 2 else maxLines,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle1,
                highlightKeywords = highlightKeywords,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (showAbstract) {
            HighlightText(
                text = abstractText.emoticonString,
                color = ExtendedTheme.colors.textSecondary,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                maxLines = if (showTitle) (maxLines - 2).coerceAtLeast(2) else maxLines,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body2,
                highlightKeywords = highlightKeywords,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun FeedCardPlaceholder() {
    Card(
        header = { UserHeaderPlaceholder(avatarSize = Sizes.Small) },
        content = {
            Text(
                text = "TitlePlaceholder",
                style = MaterialTheme.typography.subtitle1,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.fade(),
                    )
            )

            Text(
                text = "Text",
                style = MaterialTheme.typography.body1,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.fade(),
                    )
            )
        },
        action = {
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(3) {
                    ActionBtnPlaceholder(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    )
}

@Composable
fun ForumInfoChip(
    imageUriProvider: () -> String?,
    nameProvider: () -> String,
    onClick: () -> Unit,
) {
    val imageUri = imageUriProvider()
    val name = nameProvider()
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(100))
            .background(color = ExtendedTheme.colors.chip)
            .clickable(onClick = onClick)
            .padding(
                start = if (imageUri != null) 4.dp else 10.dp,
                end = 10.dp,
                top = 4.dp,
                bottom = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        imageUri?.let {
            Avatar(
                data = imageUri,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                shape = RoundedCornerShape(100)
            )
        }
        Text(
            text = stringResource(id = R.string.title_forum_name, name),
            style = MaterialTheme.typography.body2,
            color = ExtendedTheme.colors.onChip,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun MediaPlaceholder(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(ExtendedTheme.colors.chip)
            .clickable(
                enabled = onClick != null
            ) { onClick?.invoke() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProvideContentColor(color = ExtendedTheme.colors.onChip) {
            Box(
                modifier = Modifier.size(16.dp),
            ) {
                icon()
            }
            ProvideTextStyle(
                value = MaterialTheme.typography.subtitle2,
                content = text
            )
        }
    }
}

@Composable
private fun ThreadMedia(
    forumId: Long,
    forumName: String,
    threadId: Long,
    modifier: Modifier = Modifier,
    medias: ImmutableList<ImmutableHolder<Media>> = persistentListOf(),
    videoInfo: ImmutableHolder<VideoInfo>? = null,
) {
    val context = LocalContext.current

    val mediaCount = remember(medias) {
        medias.size
    }
    val hasPhoto = remember(mediaCount) { mediaCount > 0 }
    val isSinglePhoto = remember(mediaCount) { mediaCount == 1 }

    val hideMedia = context.appPreferences.hideMedia

    val windowWidthSizeClass = LocalWindowSizeClass.current.widthSizeClass
    val singleMediaFraction = remember(windowWidthSizeClass) {
        if (windowWidthSizeClass == WindowWidthSizeClass.Compact)
            1f
        else 0.5f
    }

    val hasMedia = remember(hasPhoto, videoInfo) {
        hasPhoto || videoInfo != null
    }

    if (hasMedia) {
        Box(modifier = modifier) {
            if (videoInfo != null) {
                if (hideMedia) {
                    MediaPlaceholder(
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.OndemandVideo,
                                contentDescription = stringResource(id = R.string.desc_video)
                            )
                        },
                        text = {
                            Text(text = stringResource(id = R.string.desc_video))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    val aspectRatio = remember(videoInfo) {
                        max(
                            videoInfo
                                .get { thumbnailWidth }
                                .toFloat() / videoInfo.get { thumbnailHeight },
                            16f / 9
                        )
                    }
                    Box(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                    ) {
                        VideoPlayer(
                            videoUrl = videoInfo.get { videoUrl },
                            thumbnailUrl = videoInfo.get { thumbnailUrl },
                            modifier = Modifier
                                .fillMaxWidth(singleMediaFraction)
                                .aspectRatio(aspectRatio)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            } else if (hasPhoto) {
                val mediaWidthFraction = remember(isSinglePhoto, singleMediaFraction) {
                    if (isSinglePhoto) singleMediaFraction else 1f
                }
                val mediaAspectRatio = remember(isSinglePhoto) {
                    if (isSinglePhoto) 2f else 3f
                }
                if (hideMedia) {
                    val photoViewData = remember(
                        medias, forumId, forumName, threadId
                    ) {
                        getPhotoViewData(
                            medias = medias.map { it.get() },
                            forumId = forumId,
                            forumName = forumName,
                            threadId = threadId,
                            index = 0
                        )
                    }
                    MediaPlaceholder(
                        icon = {
                            Icon(
                                imageVector = if (isSinglePhoto) Icons.Rounded.Photo else Icons.Rounded.PhotoLibrary,
                                contentDescription = stringResource(id = R.string.desc_photo)
                            )
                        },
                        text = {
                            Text(text = stringResource(id = R.string.btn_open_photos, mediaCount))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            context.goToActivity<PhotoViewActivity> {
                                putExtra(
                                    PhotoViewActivity.EXTRA_PHOTO_VIEW_DATA,
                                    photoViewData
                                )
                            }
                        }
                    )
                } else {
                    val showMediaCount = remember(medias) { min(medias.size, 3) }
                    val hasMoreMedia = remember(medias) { medias.size > 3 }
                    val showMedias = remember(medias) { medias.subList(0, showMediaCount) }
                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(mediaWidthFraction)
                                .aspectRatio(mediaAspectRatio)
                                .clip(RoundedCornerShape(12.dp)),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            showMedias.fastForEachIndexed { index, media ->
                                val photoViewData = remember(
                                    index, medias, forumId, forumName, threadId
                                ) {
                                    getPhotoViewData(
                                        medias = medias.map { it.get() },
                                        forumId = forumId,
                                        forumName = forumName,
                                        threadId = threadId,
                                        index = index
                                    )
                                }
                                NetworkImage(
                                    imageUri = remember(media) { media.url },
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f),
                                    photoViewData = photoViewData,
                                    contentScale = ContentScale.Crop,
                                    enablePreview = true
                                )
                            }
                        }
                        if (hasMoreMedia) {
                            Badge(
                                icon = Icons.Rounded.PhotoSizeSelectActual,
                                text = "${medias.size}",
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThreadMedia(
    item: ImmutableHolder<ThreadInfo>,
    modifier: Modifier = Modifier,
) {
    ThreadMedia(
        forumId = item.get { forumId },
        forumName = item.get { forumName },
        threadId = item.get { threadId },
        medias = item.getImmutableList { media },
        videoInfo = item.get { videoInfo }?.wrapImmutable(),
        modifier = modifier,
    )
}

@Composable
fun OriginThreadCard(
    originThreadInfo: ImmutableHolder<OriginThreadInfo>,
    modifier: Modifier = Modifier,
) {
    val contentRenders = remember(originThreadInfo) { originThreadInfo.get { content.renders } }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column {
            contentRenders.fastForEach {
                it.Render()
            }
        }
        ThreadMedia(
            forumId = originThreadInfo.get { fid },
            forumName = originThreadInfo.get { fname },
            threadId = originThreadInfo.get { tid.toLong() },
            medias = originThreadInfo.getImmutableList { media },
            videoInfo = originThreadInfo.get { video_info }?.wrapImmutable()
        )
    }
}

@Composable
private fun ThreadForumInfo(
    item: ImmutableHolder<ThreadInfo>,
    onClick: (SimpleForum) -> Unit,
) {
    val hasForumInfo = remember(item) { item.isNotNull { forumInfo } }
    if (hasForumInfo) {
        val forumInfo = remember(item) { item.getImmutable { forumInfo!! } }
        ThreadForumInfo(
            forumName = forumInfo.get { name },
            forumAvatar = forumInfo.get { avatar },
            onClick = { onClick(forumInfo.get()) }
        )
    }
}

@Composable
private fun ThreadForumInfo(
    forumName: String,
    forumAvatar: String?,
    onClick: () -> Unit,
) {
    val hasForum = remember(forumName) { forumName.isNotBlank() }
    if (hasForum) {
        ForumInfoChip(
            imageUriProvider = { forumAvatar },
            nameProvider = { forumName },
            onClick = onClick
        )
    }
}

@Composable
fun ThreadReplyBtn(
    replyNum: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ActionBtn(
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_comment_new),
                contentDescription = stringResource(id = R.string.desc_comment),
            )
        },
        text = {
            Text(
                text = if (replyNum == 0)
                    stringResource(id = R.string.title_reply)
                else replyNum.getShortNumString()
            )
        },
        modifier = modifier,
        onClick = onClick,
        color = ExtendedTheme.colors.textSecondary.copy(alpha = 0.72f),
    )
}

@Composable
fun ThreadAgreeBtn(
    hasAgree: Boolean,
    agreeNum: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (hasAgree) {
        ExtendedTheme.colors.primary
    } else {
        ExtendedTheme.colors.textSecondary.copy(alpha = 0.72f)
    }
    val animatedColor by animateColorAsState(contentColor, label = "agreeBtnContentColor")

    ActionBtn(
        icon = {
            Icon(
                imageVector = if (hasAgree) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = stringResource(id = R.string.desc_like),
            )
        },
        text = {
            Text(
                text = if (agreeNum == 0)
                    stringResource(id = R.string.title_agree)
                else agreeNum.getShortNumString()
            )
        },
        modifier = modifier,
        color = animatedColor,
        onClick = onClick
    )
}

@Composable
fun ThreadShareBtn(
    shareNum: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ActionBtn(
        icon = {
            Icon(
                imageVector = Icons.Rounded.SwapCalls,
                contentDescription = stringResource(id = R.string.desc_share),
            )
        },
        text = {
            Text(
                text = if (shareNum == 0L)
                    stringResource(id = R.string.title_share)
                else shareNum.getShortNumString()
            )
        },
        modifier = modifier,
        onClick = onClick,
        color = ExtendedTheme.colors.textSecondary.copy(alpha = 0.72f),
    )
}

@Composable
fun FeedCard(
    item: ImmutableHolder<ThreadInfo>,
    onClick: (ThreadInfo) -> Unit,
    onAgree: (ThreadInfo) -> Unit,
    modifier: Modifier = Modifier,
    onClickReply: (ThreadInfo) -> Unit = {},
    onClickUser: (User) -> Unit = {},
    onClickForum: (SimpleForum) -> Unit = {},
    onClickOriginThread: (OriginThreadInfo) -> Unit = {},
    dislikeAction: @Composable () -> Unit = {},
) {
    Card(
        header = {
            val author = remember(item) { item.getNullableImmutable { author } }
            author?.let {
                UserHeader(
                    userProvider = { it },
                    timeProvider = { item.get { lastTimeInt } },
                    onClick = {
                        onClickUser(it.get())
                    },
                ) { dislikeAction() }
            }
        },
        content = {
            ThreadContent(
                title = item.get { title },
                abstractText = item.get { abstractText },
                tabName = item.get { tabName },
                showTitle = item.get { isNoTitle != 1 && title.isNotBlank() },
                showAbstract = item.get { abstractText.isNotBlank() },
                isGood = item.get { isGood == 1 },
            )

            ThreadMedia(
                item = item,
            )

            item.getNullableImmutable { origin_thread_info }
                .takeIf { item.get { is_share_thread } == 1 }?.let {
                    OriginThreadCard(
                        originThreadInfo = it,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ExtendedTheme.colors.floorCard)
                            .border(
                                width = 0.75.dp,
                                color = if (ExtendedTheme.colors.isNightMode) {
                                    Color.White.copy(alpha = 0.07f)
                                } else {
                                    Color.Black.copy(alpha = 0.06f)
                                },
                                shape = RoundedCornerShape(12.dp),
                            )
                            .clickable {
                                onClickOriginThread(it.get())
                            }
                            .padding(12.dp)
                    )
                }

            ThreadForumInfo(item = item, onClick = onClickForum)
        },
        action = {
            Row(modifier = Modifier.fillMaxWidth()) {
                ThreadShareBtn(
                    shareNum = item.get { shareNum },
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )

                ThreadReplyBtn(
                    replyNum = item.get { replyNum },
                    onClick = { onClickReply(item.get()) },
                    modifier = Modifier.weight(1f)
                )

                ThreadAgreeBtn(
                    hasAgree = item.get { agree?.hasAgree == 1 },
                    agreeNum = item.get { agreeNum },
                    onClick = { onAgree(item.get()) },
                    modifier = Modifier.weight(1f)
                )
            }
        },
        onClick = { onClick(item.get()) },
        modifier = modifier,
    )
}

@Composable
fun FeedCard(
    item: ImmutableHolder<PostInfoList>,
    onClick: (PostInfoList) -> Unit,
    onAgree: (PostInfoList) -> Unit,
    modifier: Modifier = Modifier,
    onClickReply: (PostInfoList) -> Unit = {},
    onClickUser: (id: Long) -> Unit = {},
    onClickForum: (name: String) -> Unit = {},
    onClickOriginThread: (OriginThreadInfo) -> Unit = {},
) {
    Card(
        header = {
            UserHeader(
                nameProvider = { item.get { user_name } },
                nameShowProvider = { item.get { name_show } },
                portraitProvider = { item.get { user_portrait } },
                timeProvider = { item.get { create_time } },
                onClick = {
                    onClickUser(item.get { user_id })
                },
            )
        },
        content = {
            ThreadContent(
                title = item.get { title },
                abstractText = item.get { abstractText },
                showTitle = item.get { is_ntitle != 1 && title.isNotBlank() },
                showAbstract = item.get { abstractText.isNotBlank() },
            )

            ThreadMedia(
                forumId = item.get { forum_id },
                forumName = item.get { forum_name },
                threadId = item.get { thread_id },
                medias = item.getImmutableList { media },
                videoInfo = item.getNullableImmutable { video_info }
            )

            item.getNullableImmutable { origin_thread_info }
                .takeIf { item.get { is_share_thread } == 1 }?.let {
                    OriginThreadCard(
                        originThreadInfo = it,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ExtendedTheme.colors.floorCard)
                            .border(
                                width = 0.75.dp,
                                color = if (ExtendedTheme.colors.isNightMode) {
                                    Color.White.copy(alpha = 0.07f)
                                } else {
                                    Color.Black.copy(alpha = 0.06f)
                                },
                                shape = RoundedCornerShape(12.dp),
                            )
                            .clickable {
                                onClickOriginThread(it.get())
                            }
                            .padding(12.dp)
                    )
                }

            ThreadForumInfo(
                forumName = item.get { forum_name },
                forumAvatar = null,
                onClick = { onClickForum(item.get { forum_name }) }
            )
        },
        action = {
            Row(modifier = Modifier.fillMaxWidth()) {
                ThreadShareBtn(
                    shareNum = item.get { share_num }.toLong(),
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )

                ThreadReplyBtn(
                    replyNum = item.get { reply_num },
                    onClick = { onClickReply(item.get()) },
                    modifier = Modifier.weight(1f)
                )

                ThreadAgreeBtn(
                    hasAgree = item.get { agree?.hasAgree == 1 },
                    agreeNum = item.get { agree_num },
                    onClick = { onAgree(item.get()) },
                    modifier = Modifier.weight(1f)
                )
            }
        },
        onClick = { onClick(item.get()) },
        modifier = modifier,
    )
}

@Composable
private fun ActionBtnPlaceholder(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Button",
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.fade(),
                ),
        )
    }
}

@Composable
private fun ActionBtn(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    onClick: (() -> Unit)? = null,
) {
    val clickableModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .then(clickableModifier)
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        ProvideContentColor(color = color) {
            Box(modifier = Modifier.size(15.dp)) {
                icon()
            }
            Spacer(modifier = Modifier.width(6.dp))
            ProvideTextStyle(value = MaterialTheme.typography.caption) {
                text()
            }
        }
    }
}

@Composable
fun VideoPlayer(
    videoUrl: String,
    thumbnailUrl: String,
    modifier: Modifier = Modifier,
    title: String = "",
) {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val videoPlayerController = rememberVideoPlayerController(
        source = VideoPlayerSource.Network(videoUrl),
        thumbnailUrl = thumbnailUrl,
        fullScreenModeChangedListener = object : OnFullScreenModeChangedListener {
            override fun onFullScreenModeChanged(isFullScreen: Boolean) {
                Log.i("VideoPlayer", "onFullScreenModeChanged $isFullScreen")
                systemUiController.isStatusBarVisible = !isFullScreen
                systemUiController.isNavigationBarVisible = !isFullScreen
                if (isFullScreen) {
                    context.findActivity()?.requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                } else {
                    context.findActivity()?.requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }
        }
    )

    val fullScreen by (videoPlayerController as DefaultVideoPlayerController).collect { isFullScreen }
    val videoPlayerContent =
        movableContentOf { isFullScreen: Boolean, playerModifier: Modifier ->
            com.huanchengfly.tieba.post.ui.widgets.compose.video.VideoPlayer(
                videoPlayerController = videoPlayerController,
                modifier = playerModifier,
                backgroundColor = if (isFullScreen) Color.Black else Color.Transparent
            )
        }

    if (fullScreen) {
        Spacer(
            modifier = modifier
        )
        FullScreen {
            videoPlayerContent(
                true,
                Modifier.fillMaxSize()
            )
        }
    } else {
        videoPlayerContent(
            false,
            modifier
        )
    }
}

@Preview("FeedCardPreview")
@Composable
fun FeedCardPreview() {
    FeedCard(
        item = wrapImmutable(
            ThreadInfo(
                title = "预览",
                author = User(),
                lastTimeInt = (System.currentTimeMillis() / 1000).toInt()
            )
        ),
        onClick = {},
        onAgree = {},
        modifier = Modifier.background(ExtendedTheme.colors.card)
    )
}