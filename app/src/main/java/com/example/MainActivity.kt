package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.PridoViewModel
import com.example.ui.components.PridoTopBar
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CreatePostDialog
import com.example.ui.screens.EditProfileDialog
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.MessagesScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.PridoBackground
import com.example.ui.theme.PridoBorder
import com.example.ui.theme.PridoCyan
import com.example.ui.theme.PridoCyanLight
import com.example.ui.theme.PridoSurface
import com.example.ui.theme.PridoTextSecondary
import com.example.ui.theme.PridoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PridoTheme {
                PridoAppRoot()
            }
        }
    }
}

@Composable
fun PridoAppRoot(viewModel: PridoViewModel = viewModel()) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val dbStatus by viewModel.databaseStatus.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val categoryFilter by viewModel.categoryFilter.collectAsState()
    val postUiModels by viewModel.postUiModels.collectAsState()
    val allProfiles by viewModel.allProfiles.collectAsState()
    val allFollows by viewModel.allFollows.collectAsState()
    val allMessages by viewModel.allMessages.collectAsState()
    val activeChatUserId by viewModel.activeChatUserId.collectAsState()
    val viewingProfileUserId by viewModel.viewingProfileUserId.collectAsState()
    val snackBarMessage by viewModel.snackBarMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var initialCategoryForDialog by remember { mutableStateOf("Ideas") }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    // Handle back button when inside secondary screens
    if (currentUser != null && currentScreen != "feed") {
        BackHandler {
            viewModel.navigateTo("feed")
        }
    }

    LaunchedEffect(snackBarMessage) {
        snackBarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackBar()
        }
    }

    if (currentUser == null) {
        AuthScreen(
            isLoading = isLoading,
            onSignIn = { email, pass ->
                viewModel.signIn(email, pass) { _, _ -> }
            },
            onSignUp = { email, pass, name, username ->
                viewModel.signUp(email, pass, name, username) { _, _ -> }
            },
            onRecoverPassword = { email ->
                viewModel.recoverPassword(email)
            },
            onQuickDemoSignIn = { profileId ->
                viewModel.quickSignInAs(profileId)
            },
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = PridoBackground,
            topBar = {
                val screenTitle = when (currentScreen) {
                    "feed" -> "Feed"
                    "explore" -> "Explore"
                    "messages" -> "Messages"
                    "profile" -> "Profile"
                    "settings" -> "Settings"
                    "admin" -> "Admin"
                    else -> "PRIDO"
                }
                val subtitle = when (currentScreen) {
                    "feed" -> "Ideas from your creative network"
                    "explore" -> "Discover creators and trending ideas"
                    "messages" -> "Direct private conversations"
                    "profile" -> currentUser?.effectiveDisplayName
                    "settings" -> "Account & Supabase sync"
                    "admin" -> "Member verification & approval"
                    else -> null
                }
                PridoTopBar(
                    title = screenTitle,
                    subtitle = subtitle,
                    dbStatus = dbStatus,
                    isLoading = isLoading,
                    onRefresh = { viewModel.refresh() }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = PridoSurface,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.border(width = 0.5.dp, color = PridoBorder)
                ) {
                    NavigationBarItem(
                        selected = currentScreen == "feed",
                        onClick = { viewModel.navigateTo("feed") },
                        icon = { Icon(Icons.AutoMirrored.Filled.Feed, contentDescription = "Feed") },
                        label = { Text("Feed", fontSize = 11.sp) },
                        colors = pridoNavBarItemColors(),
                        modifier = Modifier.testTag("nav_feed")
                    )

                    NavigationBarItem(
                        selected = currentScreen == "explore",
                        onClick = { viewModel.navigateTo("explore") },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                        label = { Text("Explore", fontSize = 11.sp) },
                        colors = pridoNavBarItemColors(),
                        modifier = Modifier.testTag("nav_explore")
                    )

                    NavigationBarItem(
                        selected = currentScreen == "messages",
                        onClick = { viewModel.navigateTo("messages") },
                        icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Messages") },
                        label = { Text("Messages", fontSize = 11.sp) },
                        colors = pridoNavBarItemColors(),
                        modifier = Modifier.testTag("nav_messages")
                    )

                    NavigationBarItem(
                        selected = currentScreen == "profile" && viewingProfileUserId == currentUser?.id,
                        onClick = { viewModel.viewMyProfile() },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp) },
                        colors = pridoNavBarItemColors(),
                        modifier = Modifier.testTag("nav_profile")
                    )

                    NavigationBarItem(
                        selected = currentScreen == "settings",
                        onClick = { viewModel.navigateTo("settings") },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings", fontSize = 11.sp) },
                        colors = pridoNavBarItemColors(),
                        modifier = Modifier.testTag("nav_settings")
                    )
                }
            },
            floatingActionButton = {
                if (currentScreen == "feed") {
                    FloatingActionButton(
                        onClick = {
                            initialCategoryForDialog = "Ideas"
                            showCreatePostDialog = true
                        },
                        containerColor = PridoCyan,
                        contentColor = Color.Black,
                        shape = CircleShape,
                        modifier = Modifier.testTag("fab_create_post")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create Post")
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(PridoBackground)
            ) {
                Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                    when (screen) {
                        "feed" -> FeedScreen(
                            posts = postUiModels,
                            currentUser = currentUser,
                            currentFilter = categoryFilter,
                            onFilterChange = { viewModel.setCategoryFilter(it) },
                            onOpenComposer = { cat ->
                                initialCategoryForDialog = cat
                                showCreatePostDialog = true
                            },
                            onToggleLike = { viewModel.toggleLike(it) },
                            onAddComment = { postId, text -> viewModel.addComment(postId, text) },
                            onDeletePost = { viewModel.deletePost(it) },
                            onViewProfile = { viewModel.viewProfile(it) },
                            onSharePost = { viewModel.showMessage("Post link copied to clipboard") }
                        )

                        "explore" -> ExploreScreen(
                            profiles = allProfiles,
                            follows = allFollows,
                            posts = postUiModels,
                            currentUser = currentUser,
                            onViewProfile = { viewModel.viewProfile(it) },
                            onOpenChat = { viewModel.openChatWith(it) },
                            onToggleFollow = { viewModel.toggleFollow(it) },
                            onToggleLike = { viewModel.toggleLike(it) },
                            onAddComment = { postId, text -> viewModel.addComment(postId, text) },
                            onDeletePost = { viewModel.deletePost(it) },
                            onSharePost = { viewModel.showMessage("Post link copied") }
                        )

                        "messages" -> MessagesScreen(
                            profiles = allProfiles,
                            messages = allMessages,
                            currentUser = currentUser,
                            activeChatUserId = activeChatUserId,
                            onSelectChatUser = { viewModel.openChatWith(it) },
                            onSendMessage = { receiverId, text -> viewModel.sendDirectMessage(receiverId, text) }
                        )

                        "profile" -> {
                            val targetId = viewingProfileUserId ?: currentUser?.id
                            val profileUser = allProfiles.find { it.id == targetId } ?: currentUser
                            val userPosts = postUiModels.filter { it.post.userId == targetId }

                            ProfileScreen(
                                profileUser = profileUser,
                                currentUser = currentUser,
                                userPosts = userPosts,
                                allProfiles = allProfiles,
                                allFollows = allFollows,
                                onToggleFollow = { viewModel.toggleFollow(it) },
                                onOpenChat = { viewModel.openChatWith(it) },
                                onEditProfile = { showEditProfileDialog = true },
                                onToggleLike = { viewModel.toggleLike(it) },
                                onAddComment = { postId, text -> viewModel.addComment(postId, text) },
                                onDeletePost = { viewModel.deletePost(it) },
                                onSharePost = { viewModel.showMessage("Profile post shared") }
                            )
                        }

                        "settings" -> SettingsScreen(
                            currentUser = currentUser,
                            allProfiles = allProfiles,
                            dbStatus = dbStatus,
                            onEditProfile = { showEditProfileDialog = true },
                            onNavigateToAdmin = { viewModel.navigateTo("admin") },
                            onSwitchUser = { viewModel.quickSignInAs(it) },
                            onRefreshDatabase = { viewModel.refresh() },
                            onSignOut = { viewModel.signOut() }
                        )

                        "admin" -> AdminScreen(
                            currentUser = currentUser,
                            profiles = allProfiles,
                            onToggleApproval = { id, approved -> viewModel.adminToggleApproval(id, approved) },
                            onToggleVerification = { id, verified -> viewModel.adminToggleVerification(id, verified) }
                        )
                    }
                }
            }
        }

        if (showCreatePostDialog) {
            CreatePostDialog(
                currentUser = currentUser,
                initialCategory = initialCategoryForDialog,
                onDismiss = { showCreatePostDialog = false },
                onSubmit = { content, cat, imgUrl ->
                    viewModel.createPost(content, cat, imgUrl)
                }
            )
        }

        if (showEditProfileDialog && currentUser != null) {
            EditProfileDialog(
                profile = currentUser!!,
                onDismiss = { showEditProfileDialog = false },
                onSave = { name, user, avatar, bio, interests, instagram ->
                    viewModel.updateProfile(name, user, avatar, bio, interests, instagram)
                }
            )
        }
    }
}

@Composable
private fun pridoNavBarItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Color.Black,
    selectedTextColor = PridoCyanLight,
    indicatorColor = PridoCyan,
    unselectedIconColor = PridoTextSecondary,
    unselectedTextColor = PridoTextSecondary
)
