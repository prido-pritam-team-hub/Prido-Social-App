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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Profile
import com.example.data.remote.SupabaseClient
import com.example.ui.components.PridoAvatar
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.PridoBorder
import com.example.ui.theme.PridoCyan
import com.example.ui.theme.PridoCyanLight
import com.example.ui.theme.PridoError
import com.example.ui.theme.PridoSurface
import com.example.ui.theme.PridoSurfaceElevated
import com.example.ui.theme.PridoSurfaceVariant
import com.example.ui.theme.PridoTextMuted
import com.example.ui.theme.PridoTextPrimary
import com.example.ui.theme.PridoTextSecondary

@Composable
fun SettingsScreen(
    currentUser: Profile?,
    allProfiles: List<Profile>,
    dbStatus: String,
    onEditProfile: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onSwitchUser: (String) -> Unit,
    onRefreshDatabase: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // User Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PridoSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, PridoBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PridoAvatar(profile = currentUser, size = 52.dp)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentUser?.effectiveDisplayName ?: "Guest",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (currentUser?.isVerified == true) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedBadge()
                        }
                    }
                    Text(
                        text = "@${currentUser?.effectiveUsername ?: "guest"}",
                        color = PridoCyanLight,
                        fontSize = 12.sp
                    )
                    Text(
                        text = if (currentUser?.isAdmin == true) "Administrator Account" else "Approved Member",
                        color = PridoTextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Supabase Database Status Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PridoSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, PridoBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PridoCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = PridoCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Supabase Cloud Database",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Status: $dbStatus",
                            color = PridoCyanLight,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Endpoint: ${SupabaseClient.BASE_URL}",
                    color = PridoTextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = "Real-time tables: posts, comments, post_likes, prido_follows, direct_messages, profiles",
                    color = PridoTextSecondary,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onRefreshDatabase,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PridoSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PridoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = PridoCyanLight, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sync with Supabase Now", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Switch Persona (Demo Testing)
        Text(
            text = "Switch Account / Persona",
            color = PridoTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PridoSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, PridoBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                allProfiles.forEach { p ->
                    val isCurrent = p.id == currentUser?.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isCurrent) PridoCyan.copy(alpha = 0.15f) else PridoSurfaceElevated)
                            .border(1.dp, if (isCurrent) PridoCyan else PridoBorder, RoundedCornerShape(10.dp))
                            .clickable { onSwitchUser(p.id) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PridoAvatar(profile = p, size = 32.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = p.effectiveDisplayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = "@${p.effectiveUsername} ${if (p.isAdmin) "• Admin" else ""}",
                                color = if (isCurrent) PridoCyanLight else PridoTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        if (isCurrent) {
                            Text(text = "Active", color = PridoCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Account Options
        Text(
            text = "Preferences & Actions",
            color = PridoTextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PridoSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, PridoBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                SettingsOptionRow(
                    icon = Icons.Default.Edit,
                    label = "Edit Profile",
                    onClick = onEditProfile
                )
                HorizontalDivider(color = PridoBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

                SettingsOptionRow(
                    icon = Icons.Default.Shield,
                    label = "Admin Panel (Member Approvals & Badges)",
                    highlight = currentUser?.isAdmin == true,
                    onClick = onNavigateToAdmin
                )
                HorizontalDivider(color = PridoBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

                SettingsOptionRow(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    label = "Sign Out",
                    textColor = PridoError,
                    onClick = onSignOut
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "PRIDO Creative Social Network • v1.0.0",
            color = PridoTextMuted,
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SettingsOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    textColor: Color = Color.White,
    highlight: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (highlight) PridoCyan else textColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = PridoTextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}
