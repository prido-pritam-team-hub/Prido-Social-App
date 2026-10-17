package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Profile
import com.example.ui.theme.PridoBorder
import com.example.ui.theme.PridoCyan
import com.example.ui.theme.PridoCyanLight
import com.example.ui.theme.PridoIndigo
import com.example.ui.theme.PridoSurface
import com.example.ui.theme.PridoSurfaceElevated
import com.example.ui.theme.PridoTextPrimary
import com.example.ui.theme.PridoTextSecondary
import com.example.ui.theme.PridoViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val PridoLogoBrush = Brush.linearGradient(
    colors = listOf(PridoCyanLight, PridoIndigo, PridoViolet)
)

val PridoGlowBorderBrush = Brush.linearGradient(
    colors = listOf(PridoCyan.copy(alpha = 0.5f), PridoViolet.copy(alpha = 0.5f))
)

@Composable
fun PridoTopBar(
    title: String,
    subtitle: String? = null,
    dbStatus: String,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(PridoSurface.copy(alpha = 0.95f))
            .border(width = 1.dp, color = PridoBorder)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // PRIDO Brand Emblem
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PridoLogoBrush),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "PRIDO Logo",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            ColumnHeader(title = title, subtitle = subtitle, modifier = Modifier.weight(1f))

            // Database Live Status Tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PridoCyan.copy(alpha = 0.12f))
                    .border(0.5.dp, PridoCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = null,
                    tint = PridoCyan,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Supabase",
                    color = PridoCyanLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("refresh_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = PridoCyan,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = PridoTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnHeader(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "PRI",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp
            )
            Text(
                text = "DO",
                color = PridoCyanLight,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "• $title",
                color = PridoTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                color = PridoTextSecondary,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PridoAvatar(
    profile: Profile?,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    val avatarUrl = profile?.effectiveAvatarUrl
        ?: "https://api.dicebear.com/9.x/initials/svg?seed=PRIDO"

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(PridoSurfaceElevated)
            .border(1.dp, PridoBorder, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar of ${profile?.effectiveDisplayName}",
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "Verified Member",
        tint = PridoCyan,
        modifier = modifier.size(15.dp)
    )
}

@Composable
fun FollowToggleButton(
    isFollowing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val bgColor by animateColorAsState(
        targetValue = if (isFollowing) PridoSurfaceElevated else PridoCyan.copy(alpha = 0.15f),
        label = "followBg"
    )
    val textColor = if (isFollowing) PridoTextSecondary else PridoCyanLight
    val borderColor = if (isFollowing) PridoBorder else PridoCyan.copy(alpha = 0.4f)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(
                horizontal = if (compact) 10.dp else 14.dp,
                vertical = if (compact) 6.dp else 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isFollowing) Icons.Default.PersonRemove else Icons.Default.PersonAdd,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = if (isFollowing) "Following" else "Follow",
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = if (compact) 11.sp else 12.sp
        )
    }
}

fun formatRelativeTime(isoString: String?): String {
    if (isoString.isNullOrBlank()) return "just now"
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val clean = if (isoString.contains(".")) isoString.substringBefore(".") else isoString.replace("Z", "")
        val date = sdf.parse(clean) ?: return "recently"
        val diffSec = (Date().time - date.time) / 1000

        when {
            diffSec < 60 -> "now"
            diffSec < 3600 -> "${diffSec / 60}m"
            diffSec < 86400 -> "${diffSec / 3600}h"
            diffSec < 604800 -> "${diffSec / 86400}d"
            else -> SimpleDateFormat("MMM d", Locale.US).format(date)
        }
    } catch (e: Exception) {
        "recently"
    }
}
