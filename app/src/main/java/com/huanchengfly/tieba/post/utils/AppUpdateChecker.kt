package com.huanchengfly.tieba.post.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huanchengfly.tieba.post.BuildConfig
import com.huanchengfly.tieba.post.R
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme
import com.huanchengfly.tieba.post.ui.widgets.compose.AlertDialog
import com.huanchengfly.tieba.post.ui.widgets.compose.DialogNegativeButton
import com.huanchengfly.tieba.post.ui.widgets.compose.DialogPositiveButton
import com.huanchengfly.tieba.post.ui.widgets.compose.DialogState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.concurrent.TimeUnit

data class AppReleaseInfo(
    val tagName: String,
    val versionName: String,
    val title: String,
    val releaseNotes: String,
    val htmlUrl: String,
    val apkDownloadUrl: String?,
)

sealed interface UpdateCheckResult {
    data class Available(val release: AppReleaseInfo) : UpdateCheckResult
    data object UpToDate : UpdateCheckResult
    data class Error(val throwable: Throwable) : UpdateCheckResult
}

object AppUpdateChecker {
    private const val RELEASES_API_URL =
        "https://api.github.com/repos/FunnyVamp404/TiebaLitePlus/releases?per_page=5"
    private const val AUTO_CHECK_INTERVAL_MS = 24L * 60L * 60L * 1000L

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(8, TimeUnit.SECONDS)
            .build()
    }

    suspend fun checkForUpdate(
        context: Context,
        isManual: Boolean,
    ): UpdateCheckResult {
        val prefs = context.appPreferences
        val now = System.currentTimeMillis()
        if (!isManual && now - prefs.lastUpdateCheckTime < AUTO_CHECK_INTERVAL_MS) {
            return UpdateCheckResult.UpToDate
        }
        return withContext(Dispatchers.IO) {
            runCatching {
                val request = Request.Builder()
                    .url(RELEASES_API_URL)
                    .header("Accept", "application/vnd.github+json")
                    .header("User-Agent", "TiebaLitePlus/${BuildConfig.VERSION_NAME}")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        error("HTTP ${response.code}")
                    }
                    val body = response.body?.string().orEmpty()
                    val latest = parseLatestRelease(body)
                    prefs.lastUpdateCheckTime = now
                    if (latest != null && isNewerVersion(latest.versionName, BuildConfig.VERSION_NAME)) {
                        if (!isManual && latest.tagName == prefs.ignoredUpdateTag) {
                            UpdateCheckResult.UpToDate
                        } else {
                            UpdateCheckResult.Available(latest)
                        }
                    } else {
                        UpdateCheckResult.UpToDate
                    }
                }
            }.getOrElse { UpdateCheckResult.Error(it) }
        }
    }

    fun openDownload(context: Context, release: AppReleaseInfo) {
        val targetUrl = release.apkDownloadUrl ?: release.htmlUrl
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }

    private fun parseLatestRelease(json: String): AppReleaseInfo? {
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            if (obj.optBoolean("draft", false)) continue
            val tagName = obj.optString("tag_name").trim()
            val htmlUrl = obj.optString("html_url").trim()
            if (tagName.isEmpty() || htmlUrl.isEmpty()) continue
            val versionName = tagName.removePrefix("v").removePrefix("V")
            val title = obj.optString("name").trim().ifEmpty { tagName }
            val releaseNotes = obj.optString("body").trim()
            var apkUrl: String? = null
            val assets = obj.optJSONArray("assets")
            if (assets != null) {
                for (j in 0 until assets.length()) {
                    val asset = assets.optJSONObject(j) ?: continue
                    val name = asset.optString("name")
                    val downloadUrl = asset.optString("browser_download_url").trim()
                    if (name.endsWith(".apk", ignoreCase = true) && downloadUrl.isNotEmpty()) {
                        apkUrl = downloadUrl
                        break
                    }
                }
            }
            return AppReleaseInfo(
                tagName = tagName,
                versionName = versionName,
                title = title,
                releaseNotes = releaseNotes,
                htmlUrl = htmlUrl,
                apkDownloadUrl = apkUrl,
            )
        }
        return null
    }

    internal fun isNewerVersion(remoteVersion: String, localVersion: String): Boolean {
        val remote = parseVersion(remoteVersion)
        val local = parseVersion(localVersion)
        val maxMain = maxOf(remote.main.size, local.main.size)
        for (i in 0 until maxMain) {
            val r = remote.main.getOrElse(i) { 0 }
            val l = local.main.getOrElse(i) { 0 }
            if (r != l) return r > l
        }
        if (remote.tier != local.tier) return remote.tier > local.tier
        return remote.sub > local.sub
    }

    private data class ParsedVersion(
        val main: List<Int>,
        // -1: alpha/beta/rc prerelease, 0: plain release, 1: plus release
        val tier: Int,
        val sub: Int,
    )

    private fun parseVersion(raw: String): ParsedVersion {
        val cleaned = raw.trim()
            .removePrefix("v")
            .removePrefix("V")
            .substringBefore("+")
        val mainPart = cleaned.substringBefore("-")
        val suffixPart = cleaned.substringAfter("-", "")
        val mainNums = mainPart.split(".").map { it.toIntOrNull() ?: 0 }
        val lower = suffixPart.lowercase()
        val (tier, sub) = when {
            lower.isEmpty() -> 0 to 0
            lower.startsWith("plus") -> {
                val n = Regex("\\d+").find(lower)?.value?.toIntOrNull() ?: 0
                1 to n
            }
            else -> {
                val n = Regex("\\d+").find(lower)?.value?.toIntOrNull() ?: 0
                -1 to n
            }
        }
        return ParsedVersion(main = mainNums, tier = tier, sub = sub)
    }
}

@Composable
fun UpdateAvailableDialog(
    dialogState: DialogState,
    release: AppReleaseInfo?,
    showIgnoreButton: Boolean = true,
) {
    val context = LocalContext.current
    val currentVersion = BuildConfig.VERSION_NAME.substringBefore("+")
    AlertDialog(
        dialogState = dialogState,
        title = {
            Text(
                text = stringResource(
                    id = R.string.title_update_available,
                    release?.tagName.orEmpty(),
                )
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .heightIn(max = 260.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(
                        id = R.string.message_update_version_summary,
                        currentVersion,
                        release?.versionName.orEmpty(),
                    ),
                    fontSize = 13.sp,
                    color = ExtendedTheme.colors.textSecondary,
                )
                val notes = release?.releaseNotes.orEmpty()
                if (notes.isNotEmpty()) {
                    Text(
                        text = notes,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = ExtendedTheme.colors.text,
                    )
                }
            }
        },
        buttons = {
            DialogPositiveButton(
                text = stringResource(id = R.string.button_update_download),
                onClick = {
                    release?.let { AppUpdateChecker.openDownload(context, it) }
                },
            )
            DialogNegativeButton(
                text = stringResource(id = R.string.button_update_later),
            )
            if (showIgnoreButton) {
                DialogNegativeButton(
                    text = stringResource(id = R.string.button_update_ignore),
                    onClick = {
                        release?.let { context.appPreferences.ignoredUpdateTag = it.tagName }
                    },
                )
            }
        },
    )
}
