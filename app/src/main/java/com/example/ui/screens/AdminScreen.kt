package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Profile
import com.example.ui.components.PridoAvatar
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.PridoBorder
import com.example.ui.theme.PridoCyan
import com.example.ui.theme.PridoCyanLight
import com.example.ui.theme.PridoSuccess
import com.example.ui.theme.PridoSurface
import com.example.ui.theme.PridoSurfaceElevated
import com.example.ui.theme.PridoTextMuted
import com.example.ui.theme.PridoTextSecondary
import com.example.ui.theme.PridoWarning

@Composable
fun AdminScreen(
    currentUser: Profile?,
    profiles: List<Profile>,
    onToggleApproval: (String, Boolean) -> Unit,
    onToggleVerification: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        item {
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
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PridoCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = PridoCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Admin Governance Panel",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Manage member access, moderation & verification",
                                color = PridoTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Total Registered Members: ${profiles.size}",
                        color = PridoCyanLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Members Directory",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(profiles, key = { it.id }) { person ->
            val isMe = person.id == currentUser?.id

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PridoSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PridoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PridoAvatar(profile = person, size = 42.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = person.effectiveDisplayName,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                if (person.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    VerifiedBadge()
                                }
                            }
                            Text(
                                text = "@${person.effectiveUsername} • ${if (person.isApproved) "Approved" else "Pending"}",
                                color = if (person.isApproved) PridoSuccess else PridoWarning,
                                fontSize = 11.sp
                            )
                        }
                        if (person.isAdmin) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PridoCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "ADMIN", color = PridoCyanLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Admin Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Approval Toggle
                        Button(
                            onClick = { onToggleApproval(person.id, person.isApproved) },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (person.isApproved) PridoWarning.copy(alpha = 0.15f) else PridoSuccess.copy(alpha = 0.15f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (person.isApproved) PridoWarning.copy(alpha = 0.4f) else PridoSuccess.copy(alpha = 0.4f)
                            )
                        ) {
                            Text(
                                text = if (person.isApproved) "Pause Access" else "Approve",
                                color = if (person.isApproved) PridoWarning else PridoSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Verified Badge Toggle
                        Button(
                            onClick = { onToggleVerification(person.id, person.isVerified) },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (person.isVerified) PridoSurfaceElevated else PridoCyan.copy(alpha = 0.15f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (person.isVerified) PridoBorder else PridoCyan.copy(alpha = 0.4f)
                            )
                        ) {
                            Text(
                                text = if (person.isVerified) "Remove Badge" else "Grant Badge",
                                color = if (person.isVerified) PridoTextSecondary else PridoCyanLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
