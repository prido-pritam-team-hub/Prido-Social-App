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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.DirectMessage
import com.example.data.model.Profile
import com.example.ui.components.PridoAvatar
import com.example.ui.components.VerifiedBadge
import com.example.ui.components.formatRelativeTime
import com.example.ui.theme.PridoBorder
import com.example.ui.theme.PridoCyan
import com.example.ui.theme.PridoCyanLight
import com.example.ui.theme.PridoIndigo
import com.example.ui.theme.PridoSurface
import com.example.ui.theme.PridoSurfaceElevated
import com.example.ui.theme.PridoSurfaceVariant
import com.example.ui.theme.PridoTextMuted
import com.example.ui.theme.PridoTextPrimary
import com.example.ui.theme.PridoTextSecondary

@Composable
fun MessagesScreen(
    profiles: List<Profile>,
    messages: List<DirectMessage>,
    currentUser: Profile?,
    activeChatUserId: String?,
    onSelectChatUser: (String) -> Unit,
    onSendMessage: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchPeople by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }

    val otherProfiles = profiles.filter { it.id != currentUser?.id }
    val filteredProfiles = otherProfiles.filter {
        searchPeople.isBlank() ||
            it.effectiveDisplayName.contains(searchPeople, ignoreCase = true) ||
            it.effectiveUsername.contains(searchPeople, ignoreCase = true)
    }

    val selectedUser = otherProfiles.find { it.id == activeChatUserId }
        ?: otherProfiles.firstOrNull()

    val chatMessages = if (selectedUser != null && currentUser != null) {
        messages.filter {
            (it.senderId == currentUser.id && it.receiverId == selectedUser.id) ||
                (it.senderId == selectedUser.id && it.receiverId == currentUser.id)
        }
    } else emptyList()

    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PridoSurfaceVariant.copy(alpha = 0.2f))
    ) {
        // Horizontal Creator Heads Carousel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PridoSurface)
                .border(1.dp, PridoBorder)
                .padding(vertical = 10.dp)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp)
            ) {
                items(filteredProfiles) { person ->
                    val isSelected = person.id == selectedUser?.id
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onSelectChatUser(person.id) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .border(
                                    2.dp,
                                    if (isSelected) PridoCyan else PridoBorder,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            PridoAvatar(profile = person, size = 42.dp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = person.effectiveDisplayName.split(" ").firstOrNull() ?: "Member",
                            color = if (isSelected) PridoCyanLight else PridoTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        if (selectedUser == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No creators available to message.",
                    color = PridoTextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            // Active Chat Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PridoSurfaceElevated)
                    .border(0.5.dp, PridoBorder)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PridoAvatar(profile = selectedUser, size = 36.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedUser.effectiveDisplayName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (selectedUser.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedBadge()
                        }
                    }
                    Text(
                        text = "@${selectedUser.effectiveUsername} • Supabase Real-time",
                        color = PridoCyanLight,
                        fontSize = 10.sp
                    )
                }
            }

            // Message Thread
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (chatMessages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Say hello to ${selectedUser.effectiveDisplayName} 👋",
                                    color = PridoTextSecondary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Start a private conversation.",
                                    color = PridoTextMuted,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(chatMessages, key = { it.id }) { msg ->
                        val isMe = msg.senderId == currentUser?.id

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            if (!isMe) {
                                PridoAvatar(profile = selectedUser, size = 26.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            Column(
                                horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = if (isMe) 16.dp else 4.dp,
                                                bottomEnd = if (isMe) 4.dp else 16.dp
                                            )
                                        )
                                        .background(
                                            if (isMe) PridoCyan.copy(alpha = 0.2f)
                                            else PridoSurfaceElevated
                                        )
                                        .border(
                                            1.dp,
                                            if (isMe) PridoCyan.copy(alpha = 0.4f) else PridoBorder,
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = if (isMe) 16.dp else 4.dp,
                                                bottomEnd = if (isMe) 4.dp else 16.dp
                                            )
                                        )
                                        .padding(horizontal = 14.dp, vertical = 9.dp)
                                ) {
                                    Text(
                                        text = msg.content,
                                        color = PridoTextPrimary,
                                        fontSize = 13.sp,
                                        lineHeight = 19.sp
                                    )
                                }

                                Text(
                                    text = formatRelativeTime(msg.createdAt),
                                    color = PridoTextMuted,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Message Composer Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PridoSurface)
                    .border(0.5.dp, PridoBorder)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Write a message to ${selectedUser.effectiveDisplayName}…", fontSize = 12.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("message_input_field"),
                    colors = pridoTextFieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(selectedUser.id, messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PridoCyan)
                        .testTag("send_message_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
