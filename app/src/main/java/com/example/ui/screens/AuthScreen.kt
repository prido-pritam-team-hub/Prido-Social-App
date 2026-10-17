package com.example.ui.screens

import androidx.compose.foundation.background
import coil.compose.AsyncImage
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PridoAvatar
import com.example.ui.components.PridoLogoBrush
import com.example.ui.theme.PridoBackground
import com.example.ui.theme.PridoBorder
import com.example.ui.theme.PridoCyan
import com.example.ui.theme.PridoCyanLight
import com.example.ui.theme.PridoError
import com.example.ui.theme.PridoIndigo
import com.example.ui.theme.PridoSurface
import com.example.ui.theme.PridoSurfaceElevated
import com.example.ui.theme.PridoSurfaceVariant
import com.example.ui.theme.PridoTextMuted
import com.example.ui.theme.PridoTextPrimary
import com.example.ui.theme.PridoTextSecondary
import com.example.ui.theme.PridoViolet

@Composable
fun AuthScreen(
    isLoading: Boolean,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String, String, String) -> Unit,
    onRecoverPassword: (String) -> Unit,
    onQuickDemoSignIn: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Sign Up

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var agreeTerms by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PridoBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Hero Logo
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(PridoLogoBrush),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "PRI",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp
            )
            Text(
                text = "DO",
                color = PridoCyanLight,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp
            )
        }

        Text(
            text = "CREATIVE SOCIAL NETWORK",
            color = PridoTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "CREATE • SHARE • CONNECT",
            color = PridoCyanLight,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Your ideas deserve a network.",
            color = PridoTextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Three Feature Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FeatureBadge(title = "Secure Auth", icon = Icons.Default.Security, modifier = Modifier.weight(1f))
            FeatureBadge(title = "Live Feed", icon = Icons.Default.Bolt, modifier = Modifier.weight(1f))
            FeatureBadge(title = "Supabase DB", icon = Icons.Default.Check, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Auth Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PridoSurface)
                .border(1.dp, PridoBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = PridoCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = PridoCyan
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0; localError = null },
                        text = {
                            Text(
                                text = "Sign In",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 0) PridoCyanLight else PridoTextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1; localError = null },
                        text = {
                            Text(
                                text = "Create Account",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 1) PridoCyanLight else PridoTextSecondary
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (selectedTab == 1) {
                    // Sign Up Fields
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Display Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PridoTextSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_name_input"),
                        colors = pridoTextFieldColors(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it.lowercase().replace(" ", "_") },
                        label = { Text("Username (handle)") },
                        leadingIcon = { Text("@", color = PridoTextSecondary, fontSize = 16.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_username_input"),
                        colors = pridoTextFieldColors(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PridoTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_input"),
                    colors = pridoTextFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PridoTextSecondary) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password",
                                tint = PridoTextSecondary
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    colors = pridoTextFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    singleLine = true
                )

                if (selectedTab == 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = agreeTerms,
                            onCheckedChange = { agreeTerms = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PridoCyan,
                                checkmarkColor = Color.Black
                            )
                        )
                        Text(
                            text = "I agree to PRIDO terms and privacy policy",
                            color = PridoTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                if (localError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = localError!!,
                        color = PridoError,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        localError = null
                        if (email.isBlank() || password.isBlank()) {
                            localError = "Please fill in email and password."
                            return@Button
                        }
                        if (selectedTab == 0) {
                            onSignIn(email.trim(), password)
                        } else {
                            if (displayName.isBlank() || username.isBlank()) {
                                localError = "Please enter your name and username."
                                return@Button
                            }
                            if (!agreeTerms) {
                                localError = "Please accept terms."
                                return@Button
                            }
                            onSignUp(email.trim(), password, displayName.trim(), username.trim())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("auth_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PridoCyan)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                    } else {
                        Text(
                            text = if (selectedTab == 0) "Sign In" else "Create Account",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                if (selectedTab == 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { selectedTab = 1 }) {
                            Text("Create account", color = PridoCyanLight, fontSize = 12.sp)
                        }
                        TextButton(onClick = {
                            if (email.isNotBlank()) onRecoverPassword(email)
                            else localError = "Enter your email to send reset instructions."
                        }) {
                            Text("Forgot password?", color = PridoTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Demo Accounts Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(PridoSurfaceVariant.copy(alpha = 0.5f))
                .border(1.dp, PridoBorder, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = PridoCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Instant 1-Tap Demo Access",
                        color = PridoCyanLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = "Explore the network immediately with pre-configured creator personas:",
                    color = PridoTextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DemoPersonaRow(
                        name = "Harish Mandal",
                        role = "Founder & Admin (Full Access)",
                        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&q=80",
                        onClick = { onQuickDemoSignIn("user_admin_001") }
                    )
                    DemoPersonaRow(
                        name = "Alex Rivera",
                        role = "Creative Developer & 3D Coder",
                        avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&q=80",
                        onClick = { onQuickDemoSignIn("user_alex_002") }
                    )
                    DemoPersonaRow(
                        name = "Maya Lin",
                        role = "UI/UX & Product Designer",
                        avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&q=80",
                        onClick = { onQuickDemoSignIn("user_maya_003") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun FeatureBadge(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PridoSurface)
            .border(1.dp, PridoBorder, RoundedCornerShape(12.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = PridoCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, color = PridoTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DemoPersonaRow(
    name: String,
    role: String,
    avatarUrl: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(PridoSurfaceElevated)
            .border(0.5.dp, PridoBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = role, color = PridoCyanLight, fontSize = 10.sp)
        }
        Text(
            text = "Enter →",
            color = PridoCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
fun pridoTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PridoCyan,
    unfocusedBorderColor = PridoBorder,
    focusedLabelColor = PridoCyanLight,
    unfocusedLabelColor = PridoTextSecondary,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = PridoCyan
)
