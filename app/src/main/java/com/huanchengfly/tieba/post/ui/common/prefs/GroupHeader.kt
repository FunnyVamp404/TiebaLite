package com.huanchengfly.tieba.post.ui.common.prefs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider as MaterialDivider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme

@Composable
fun GroupHeader(
    title: String,
    color: Color = ExtendedTheme.colors.primary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
        )
        Spacer(Modifier.width(8.dp))
        MaterialDivider(
            modifier = Modifier.weight(1f),
            color = ExtendedTheme.colors.divider,
            thickness = 0.5.dp,
        )
        Spacer(Modifier.width(6.dp))
        Box(Modifier.size(3.dp).background(ExtendedTheme.colors.primary, CircleShape))
    }
}
