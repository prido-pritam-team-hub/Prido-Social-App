package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Profile
import com.example.ui.theme.PridoBorder
import com.example.ui.theme.PridoCyan
import com.example.ui.theme.PridoCyanLight
import com.example.ui.theme.PridoSurface
import com.example.ui.theme.PridoTextSecondary

@Composable
fun EditProfileDialog(
    profile: Profile,
    onDismiss: () -> Unit,
    onSave: (displayName: String, username: String, avatarUrl: String?, bio: String, interests: String?, instagram: String?) -> Unit
) {
    var displayName by remember { mutableStateOf(profile.effectiveDisplayName) }
    var username by remember { mutableStateOf(profile.effectiveUsername) }
    var avatarUrl by remember { mutableStateOf(profile.avatarUrl ?: "") }
    var bio by remember { mutableStateOf(profile.bio ?: "") }
    var interests by remember { mutableStateOf(profile.interests ?: "") }
    var instagram by remember { mutableStateOf(profile.instagramHandle ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = PridoSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, PridoBorder),
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Creator Profile",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PridoTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_display_name_input"),
                    colors = pridoTextFieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it.lowercase().replace(" ", "_") },
                    label = { Text("Username") },
                    leadingIcon = { Text("@", color = PridoTextSecondary) },
                    modifier = Modifier.fillMaxWidth().testTag("edit_username_input"),
                    colors = pridoTextFieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = avatarUrl,
                    onValueChange = { avatarUrl = it },
                    label = { Text("Avatar Image URL") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_avatar_url_input"),
                    colors = pridoTextFieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth().height(90.dp).testTag("edit_bio_input"),
                    colors = pridoTextFieldColors()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = interests,
                    onValueChange = { interests = it },
                    label = { Text("Interests & Tags (e.g. #design, #AI)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = pridoTextFieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = instagram,
                    onValueChange = { instagram = it },
                    label = { Text("Instagram handle (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = pridoTextFieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onSave(
                            displayName.trim(),
                            username.trim(),
                            avatarUrl.trim().takeIf { it.isNotBlank() },
                            bio.trim(),
                            interests.trim().takeIf { it.isNotBlank() },
                            instagram.trim().takeIf { it.isNotBlank() }
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("save_profile_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PridoCyan)
                ) {
                    Text(
                        text = "Save Profile",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
