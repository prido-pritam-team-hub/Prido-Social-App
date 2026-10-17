package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PostUiModel
import com.example.data.model.Profile
import com.example.ui.components.PridoAvatar
import com.example.ui.components.VerifiedBadge
import com.example.ui.components.formatRelativeTime
import com.example.ui.theme.PridoBorder
import com.example.ui.theme.PridoCyan
import com.example.ui.theme.PridoCyanLight
import com.example.ui.theme.PridoHeart
import com.example.ui.theme.PridoSurface
import com.example.ui.theme.PridoSurfaceElevated
import com.example.ui.theme.PridoSurfaceVariant
import com.example.ui.theme.PridoTextMuted
import com.example.ui.theme.PridoTextPrimary
import com.example.ui.theme.PridoTextSecondary

@Composable
fun FeedScreen(
    posts: List<PostUiModel>,
    currentUser: Profile?,
    currentFilter: String,
    onFilterChange: (String) -> Unit,
    onOpenComposer: (String) -> Unit,
    onToggleLike: (Long) -> Unit,
    onAddComment: (Long, String) -> Unit,
    onDeletePost: (Long) -> Unit,
    onViewProfile: (String) -> Unit,
    onSharePost: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Ideas", "Images", "Videos", "Creative")
    val filteredPosts = posts.filter {
        currentFilter == "All" || it.post.category?.equals(currentFilter, ignoreCase = true) == true
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Composer Banner
            PostComposerBanner(
                currentUser = currentUser,
                onOpen = { onOpenComposer("Ideas") },
                onQuickCategory = { onOpenComposer(it) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = currentFilter == cat
                    val bg by animateColorAsState(
                        targetValue = if (isSelected) PridoCyan.copy(alpha = 0.2f) else PridoSurface,
                        label = "catBg"
                    )
                    val borderColor = if (isSelected) PridoCyan else PridoBorder
                    val textColor = if (isSelected) PridoCyanLight else PridoTextSecondary

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(bg)
                            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                            .clickable { onFilterChange(cat) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (filteredPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PridoSurface)
                        .border(1.dp, PridoBorder, RoundedCornerShape(16.dp))
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = PridoCyanLight,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No posts in $currentFilter yet.",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Be the first creator to share something!",
                            color = PridoTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(filteredPosts, key = { it.post.id }) { postModel ->
                PostCard(
                    postModel = postModel,
                    currentUser = currentUser,
                    onToggleLike = { onToggleLike(postModel.post.id) },
                    onAddComment = { onAddComment(postModel.post.id, it) },
                    onDelete = { onDeletePost(postModel.post.id) },
                    onViewProfile = { onViewProfile(postModel.author.id) },
                    onShare = { onSharePost("Post by ${postModel.author.effectiveDisplayName}") }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PostComposerBanner(
    currentUser: Profile?,
    onOpen: () -> Unit,
    onQuickCategory: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PridoSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, PridoBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                PridoAvatar(profile = currentUser, size = 42.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PridoSurfaceElevated)
                        .border(1.dp, PridoBorder, RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpen)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("open_post_composer_box")
                ) {
                    Text(
                        text = "Share an idea, media or project…",
                        color = PridoTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 52.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickComposerChip("Idea", Icons.Default.Lightbulb) { onQuickCategory("Ideas") }
                QuickComposerChip("Image", Icons.Default.Image) { onQuickCategory("Images") }
                QuickComposerChip("Video", Icons.Default.Videocam) { onQuickCategory("Videos") }
            }
        }
    }
}

@Composable
private fun QuickComposerChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(PridoSurfaceElevated)
            .border(0.5.dp, PridoBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = PridoCyanLight, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = PridoTextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun PostCard(
    postModel: PostUiModel,
    currentUser: Profile?,
    onToggleLike: () -> Unit,
    onAddComment: (String) -> Unit,
    onDelete: () -> Unit,
    onViewProfile: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCommentInput by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    val canDelete = currentUser?.id == postModel.post.userId || currentUser?.isAdmin == true

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PridoSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, PridoBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("post_card_${postModel.post.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.clickable(onClick = onViewProfile)) {
                    PridoAvatar(profile = postModel.author, size = 40.dp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = postModel.author.effectiveDisplayName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable(onClick = onViewProfile)
                        )
                        if (postModel.author.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedBadge()
                        }
                    }
                    Text(
                        text = "@${postModel.author.effectiveUsername} • ${formatRelativeTime(postModel.post.createdAt)}",
                        color = PridoTextMuted,
                        fontSize = 11.sp
                    )
                }

                // Category pill
                postModel.post.category?.let { cat ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PridoCyan.copy(alpha = 0.1f))
                            .border(0.5.dp, PridoCyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = cat,
                            color = PridoCyanLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (canDelete) {
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete post",
                            tint = PridoTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post content text
            Text(
                text = postModel.post.content,
                color = PridoTextPrimary,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            // Post Media if available
            if (!postModel.post.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                val isVideo = postModel.post.category?.equals("Videos", ignoreCase = true) == true

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .border(0.5.dp, PridoBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = postModel.post.imageUrl,
                        contentDescription = "Post media",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                        contentScale = ContentScale.Crop
                    )
                    if (isVideo) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "Play video",
                                tint = PridoCyanLight,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = PridoBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Actions: Like, Comment, Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onToggleLike)
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = if (postModel.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (postModel.isLiked) PridoHeart else PridoTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${postModel.likesCount}",
                        color = if (postModel.isLiked) PridoHeart else PridoTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Comment Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { showCommentInput = !showCommentInput }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ModeComment,
                        contentDescription = "Comments",
                        tint = PridoTextSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${postModel.commentsCount}",
                        color = PridoTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Share Button
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString("https://prido.network/post/${postModel.post.id}"))
                        onShare()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = PridoTextSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            // Recent Comments List
            if (postModel.comments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PridoSurfaceVariant.copy(alpha = 0.4f))
                        .padding(10.dp)
                ) {
                    postModel.comments.takeLast(3).forEach { c ->
                        Row(verticalAlignment = Alignment.Top) {
                            PridoAvatar(profile = c.author, size = 22.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = c.author.effectiveDisplayName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = c.comment.content,
                                    color = PridoTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Inline Comment Field
            if (showCommentInput || postModel.comments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Write a comment…", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("comment_input_${postModel.post.id}"),
                        colors = pridoTextFieldColors(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                onAddComment(commentText)
                                commentText = ""
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PridoCyan.copy(alpha = 0.15f))
                            .border(1.dp, PridoCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Comment",
                            tint = PridoCyanLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
