package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Follow
import com.example.data.model.PostUiModel
import com.example.data.model.Profile
import com.example.ui.components.FollowToggleButton
import com.example.ui.components.PridoAvatar
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.PridoBorder
import com.example.ui.theme.PridoCyan
import com.example.ui.theme.PridoCyanLight
import com.example.ui.theme.PridoSurface
import com.example.ui.theme.PridoSurfaceElevated
import com.example.ui.theme.PridoTextMuted
import com.example.ui.theme.PridoTextSecondary

@Composable
fun ExploreScreen(
    profiles: List<Profile>,
    follows: List<Follow>,
    posts: List<PostUiModel>,
    currentUser: Profile?,
    onViewProfile: (String) -> Unit,
    onOpenChat: (String) -> Unit,
    onToggleFollow: (String) -> Unit,
    onToggleLike: (Long) -> Unit,
    onAddComment: (Long, String) -> Unit,
    onDeletePost: (Long) -> Unit,
    onSharePost: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val trendingTags = listOf("#webdev", "#AI", "#design", "#buildinpublic", "#creative", "#android")

    val matchingProfiles = profiles.filter { p ->
        p.id != currentUser?.id && (
            searchQuery.isBlank() ||
                p.effectiveDisplayName.contains(searchQuery, ignoreCase = true) ||
                p.effectiveUsername.contains(searchQuery, ignoreCase = true) ||
                p.bio?.contains(searchQuery, ignoreCase = true) == true ||
                p.interests?.contains(searchQuery, ignoreCase = true) == true
        )
    }

    val matchingPosts = posts.filter { postModel ->
        searchQuery.isBlank() ||
            postModel.post.content.contains(searchQuery, ignoreCase = true) ||
            postModel.author.effectiveDisplayName.contains(searchQuery, ignoreCase = true) ||
            postModel.post.category?.contains(searchQuery, ignoreCase = true) == true
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search creators, tags, or topics…") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = PridoCyanLight
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = PridoTextSecondary
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_search_input"),
                colors = pridoTextFieldColors(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Trending Tags
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = PridoCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Trending Tags",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(trendingTags) { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(PridoSurfaceElevated)
                            .border(1.dp, PridoBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                searchQuery = if (searchQuery == tag) "" else tag
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tag,
                            color = if (searchQuery == tag) PridoCyanLight else PridoTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Creators Section Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = null,
                    tint = PridoCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Featured Creators (${matchingProfiles.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (matchingProfiles.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PridoSurface)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No creators found matching '$searchQuery'.",
                        color = PridoTextSecondary,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            items(matchingProfiles, key = { it.id }) { creator ->
                val followersCount = follows.count { it.followingId == creator.id }
                val isFollowing = currentUser != null && follows.any {
                    it.followerId == currentUser.id && it.followingId == creator.id
                }

                CreatorCard(
                    profile = creator,
                    followersCount = followersCount,
                    isFollowing = isFollowing,
                    onView = { onViewProfile(creator.id) },
                    onToggleFollow = { onToggleFollow(creator.id) },
                    onMessage = { onOpenChat(creator.id) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Community Posts matching search
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Community Posts",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(matchingPosts, key = { it.post.id }) { postModel ->
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

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CreatorCard(
    profile: Profile,
    followersCount: Int,
    isFollowing: Boolean,
    onView: () -> Unit,
    onToggleFollow: () -> Unit,
    onMessage: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PridoSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, PridoBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.clickable(onClick = onView)) {
                    PridoAvatar(profile = profile, size = 46.dp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = profile.effectiveDisplayName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable(onClick = onView)
                        )
                        if (profile.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedBadge()
                        }
                    }
                    Text(
                        text = "@${profile.effectiveUsername} • $followersCount followers",
                        color = PridoCyanLight,
                        fontSize = 11.sp
                    )
                }
            }

            if (!profile.bio.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = profile.bio,
                    color = PridoTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }

            if (!profile.interests.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = profile.interests,
                    color = PridoCyan,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PridoSurfaceElevated)
                        .border(1.dp, PridoBorder, RoundedCornerShape(10.dp))
                        .clickable(onClick = onView)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "View Profile",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                FollowToggleButton(
                    isFollowing = isFollowing,
                    onClick = onToggleFollow,
                    compact = true
                )

                IconButton(
                    onClick = onMessage,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PridoCyan.copy(alpha = 0.15f))
                        .border(1.dp, PridoCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Mail,
                        contentDescription = "Message",
                        tint = PridoCyanLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
