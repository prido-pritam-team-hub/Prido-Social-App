package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.Profile
import com.example.ui.components.PridoAvatar
import com.example.ui.theme.PridoBorder
import com.example.ui.theme.PridoCyan
import com.example.ui.theme.PridoCyanLight
import com.example.ui.theme.PridoSurface
import com.example.ui.theme.PridoSurfaceElevated
import com.example.ui.theme.PridoTextSecondary

@Composable
fun CreatePostDialog(
    currentUser: Profile?,
    initialCategory: String = "Ideas",
    onDismiss: () -> Unit,
    onSubmit: (content: String, category: String, imageUrl: String?) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var imageUrl by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Ideas", "Creative", "Images", "Videos")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PridoSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, PridoBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PridoAvatar(profile = currentUser, size = 38.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Create a Post",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = currentUser?.effectiveDisplayName ?: "Creator",
                                color = PridoCyanLight,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = PridoTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category selector
                Text(
                    text = "Category",
                    color = PridoTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        val icon = when (cat) {
                            "Ideas" -> Icons.Default.Lightbulb
                            "Creative" -> Icons.Default.Palette
                            "Images" -> Icons.Default.Image
                            else -> Icons.Default.Videocam
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PridoCyan.copy(alpha = 0.2f) else PridoSurfaceElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) PridoCyan else PridoBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) PridoCyanLight else PridoTextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = cat,
                                    color = if (isSelected) PridoCyanLight else PridoTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Post Content Input
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("What are you building, imagining or creating?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("post_content_input"),
                    colors = pridoTextFieldColors()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Media URL Input
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    placeholder = { Text("Direct image or media URL (optional)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = PridoTextSecondary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("post_media_url_input"),
                    colors = pridoTextFieldColors(),
                    singleLine = true
                )

                if (imageUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 120.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, PridoBorder, RoundedCornerShape(10.dp))
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Media Preview",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorText!!,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (content.isBlank()) {
                            errorText = "Please write something before publishing."
                            return@Button
                        }
                        onSubmit(content.trim(), selectedCategory, imageUrl.trim().takeIf { it.isNotBlank() })
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("publish_post_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PridoCyan)
                ) {
                    Text(
                        text = "Publish to Network",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
