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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Follow
import com.example.data.model.PostUiModel
import com.example.data.model.Profile
import com.example.ui.components.FollowToggleButton
import com.example.ui.components.PridoAvatar
import com.example.ui.components.PridoLogoBrush
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.PridoBorder
import com.example.ui.theme.PridoCyan
import com.example.ui.theme.PridoCyanLight
import com.example.ui.theme.PridoIndigo
import com.example.ui.theme.PridoSurface
import com.example.ui.theme.PridoSurfaceElevated
import com.example.ui.theme.PridoTextMuted
import com.example.ui.theme.PridoTextPrimary
import com.example.ui.theme.PridoTextSecondary
import com.example.ui.theme.PridoViolet

@Composable
fun ProfileScreen(
    profileUser: Profile?,
    currentUser: Profile?,
    userPosts: List<PostUiModel>,
    allProfiles: List<Profile>,
    allFollows: List<Follow>,
    onToggleFollow: (String) -> Unit,
    onOpenChat: (String) -> Unit,
    onEditProfile: () -> Unit,
    onToggleLike: (Long) -> Unit,
    onAddComment: (Long, String) -> Unit,
    onDeletePost: (Long) -> Unit,
    onSharePost: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFollowListDialog by remember { mutableStateOf<String?>(null) } // "followers" or "following"
    val profile = profileUser ?: currentUser

    if (profile == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Profile not found.", color = PridoTextSecondary)
        }
        return
    }

    val isOwnProfile = profile.id == currentUser?.id

    val followers = allFollows.filter { it.followingId == profile.id }
    val following = allFollows.filter { it.followerId == profile.id }
    val isFollowing = currentUser != null && allFollows.any {
        it.followerId == currentUser.id && it.followingId == profile.id
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            // Profile Header Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PridoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PridoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Avatar & Actions Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(CircleShape)
                                .border(2.dp, PridoLogoBrush, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            PridoAvatar(profile = profile, size = 68.dp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile.effectiveDisplayName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                if (profile.isVerified) {
                                    Spacer(modifier = Modifier.width(5.dp))
                                    VerifiedBadge(modifier = Modifier.size(16.dp))
                                }
                            }
                            Text(
                                text = "@${profile.effectiveUsername}",
                                color = PridoCyanLight,
                                fontSize = 12.sp
                            )
                            if (profile.isAdmin) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = PridoCyan, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("PRIDO Admin", color = PridoCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (!profile.bio.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = profile.bio,
                            color = PridoTextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }

                    if (!profile.interests.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = profile.interests,
                            color = PridoCyanLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons (Edit Profile vs Follow / Message)
                    if (isOwnProfile) {
                        Button(
                            onClick = onEditProfile,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("edit_profile_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PridoSurfaceElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PridoBorder)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = PridoCyanLight, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FollowToggleButton(
                                isFollowing = isFollowing,
                                onClick = { onToggleFollow(profile.id) },
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { onOpenChat(profile.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PridoCyan.copy(alpha = 0.15f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PridoCyan.copy(alpha = 0.4f))
                            ) {
                                Icon(Icons.Default.Mail, contentDescription = null, tint = PridoCyanLight, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Message", color = PridoCyanLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Row: Posts, Followers, Following, Status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PridoSurfaceElevated)
                            .border(0.5.dp, PridoBorder, RoundedCornerShape(12.dp))
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatItem(count = userPosts.size.toString(), label = "Posts")
                        ProfileStatItem(
                            count = followers.size.toString(),
                            label = "Followers",
                            clickable = true,
                            onClick = { showFollowListDialog = "followers" }
                        )
                        ProfileStatItem(
                            count = following.size.toString(),
                            label = "Following",
                            clickable = true,
                            onClick = { showFollowListDialog = "following" }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Posts by ${profile.effectiveDisplayName} (${userPosts.size})",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (userPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PridoSurface)
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No posts from this creator yet.",
                        color = PridoTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(userPosts, key = { it.post.id }) { postModel ->
                PostCard(
                    postModel = postModel,
                    currentUser = currentUser,
                    onToggleLike = { onToggleLike(postModel.post.id) },
                    onAddComment = { onAddComment(postModel.post.id, it) },
                    onDelete = { onDeletePost(postModel.post.id) },
                    onViewProfile = {},
                    onShare = { onSharePost("Post by ${postModel.author.effectiveDisplayName}") }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showFollowListDialog != null) {
        val kind = showFollowListDialog!!
        val memberIds = if (kind == "followers") followers.map { it.followerId } else following.map { it.followingId }
        val members = memberIds.mapNotNull { id -> allProfiles.find { it.id == id } }

        FollowListDialog(
            title = if (kind == "followers") "Followers" else "Following",
            members = members,
            currentUser = currentUser,
            allFollows = allFollows,
            onToggleFollow = onToggleFollow,
            onDismiss = { showFollowListDialog = null }
        )
    }
}

@Composable
private fun ProfileStatItem(
    count: String,
    label: String,
    clickable: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (clickable) Modifier.clickable(onClick = onClick) else Modifier
    ) {
        Text(text = count, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = label, color = PridoTextSecondary, fontSize = 11.sp)
    }
}

@Composable
private fun FollowListDialog(
    title: String,
    members: List<Profile>,
    currentUser: Profile?,
    allFollows: List<Follow>,
    onToggleFollow: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = PridoSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, PridoBorder),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PridoTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (members.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No $title to display yet.", color = PridoTextSecondary, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(members) { m ->
                            val isFollowing = currentUser != null && allFollows.any {
                                it.followerId == currentUser.id && it.followingId == m.id
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PridoSurfaceElevated)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PridoAvatar(profile = m, size = 36.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = m.effectiveDisplayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "@${m.effectiveUsername}", color = PridoCyanLight, fontSize = 11.sp)
                                }
                                if (currentUser?.id != m.id) {
                                    FollowToggleButton(
                                        isFollowing = isFollowing,
                                        onClick = { onToggleFollow(m.id) },
                                        compact = true
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
