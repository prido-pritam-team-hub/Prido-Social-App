package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CommentUiModel
import com.example.data.model.DirectMessage
import com.example.data.model.Follow
import com.example.data.model.PostUiModel
import com.example.data.model.Profile
import com.example.data.repository.PridoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PridoViewModel(
    private val repository: PridoRepository = PridoRepository()
) : ViewModel() {

    val currentUser = repository.currentUser
    val isLoading = repository.isLoading
    val databaseStatus = repository.databaseStatus

    private val _currentScreen = MutableStateFlow("feed")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _categoryFilter = MutableStateFlow("All")
    val categoryFilter: StateFlow<String> = _categoryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeChatUserId = MutableStateFlow<String?>(null)
    val activeChatUserId: StateFlow<String?> = _activeChatUserId.asStateFlow()

    private val _viewingProfileUserId = MutableStateFlow<String?>(null)
    val viewingProfileUserId: StateFlow<String?> = _viewingProfileUserId.asStateFlow()

    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    // Computed Posts list with author, like status, comments
    val postUiModels: StateFlow<List<PostUiModel>> = combine(
        repository.posts,
        repository.profiles,
        repository.likes,
        repository.comments,
        repository.currentUser
    ) { posts, profiles, likes, comments, user ->
        val profileMap = profiles.associateBy { it.id }

        posts.map { post ->
            val author = profileMap[post.userId] ?: Profile(
                id = post.userId,
                username = post.instagramId ?: "member",
                displayName = post.userName ?: "PRIDO Member"
            )

            val postLikes = likes.filter { it.postId == post.id }
            val isLiked = user != null && postLikes.any { it.userId == user.id }

            val postComments = comments.filter { it.postId == post.id }.map { c ->
                CommentUiModel(
                    comment = c,
                    author = profileMap[c.userId] ?: Profile(id = c.userId, displayName = "Member")
                )
            }

            PostUiModel(
                post = post,
                author = author,
                likesCount = postLikes.size,
                isLiked = isLiked,
                commentsCount = postComments.size,
                comments = postComments
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProfiles: StateFlow<List<Profile>> = repository.profiles
    val allFollows: StateFlow<List<Follow>> = repository.follows
    val allMessages: StateFlow<List<DirectMessage>> = repository.messages

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun setCategoryFilter(filter: String) {
        _categoryFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openChatWith(userId: String) {
        _activeChatUserId.value = userId
        _currentScreen.value = "messages"
    }

    fun viewProfile(userId: String) {
        _viewingProfileUserId.value = userId
        _currentScreen.value = "profile"
    }

    fun viewMyProfile() {
        _viewingProfileUserId.value = currentUser.value?.id
        _currentScreen.value = "profile"
    }

    fun showMessage(msg: String) {
        _snackBarMessage.value = msg
    }

    fun clearSnackBar() {
        _snackBarMessage.value = null
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshAll()
            showMessage("Database refreshed from Supabase")
        }
    }

    fun createPost(content: String, category: String, imageUrl: String?) {
        viewModelScope.launch {
            val result = repository.createPost(content, category, imageUrl)
            if (result.isSuccess) {
                showMessage("Post published to PRIDO feed")
                _currentScreen.value = "feed"
            } else {
                showMessage("Failed to create post: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun deletePost(postId: Long) {
        viewModelScope.launch {
            repository.deletePost(postId)
            showMessage("Post removed")
        }
    }

    fun toggleLike(postId: Long) {
        viewModelScope.launch {
            repository.toggleLike(postId)
        }
    }

    fun addComment(postId: Long, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val result = repository.addComment(postId, content.trim())
            if (result.isSuccess) {
                showMessage("Comment added")
            }
        }
    }

    fun toggleFollow(targetUserId: String) {
        viewModelScope.launch {
            val nowFollowing = repository.toggleFollow(targetUserId)
            showMessage(if (nowFollowing) "Now following creator" else "Unfollowed creator")
        }
    }

    fun sendDirectMessage(receiverId: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val result = repository.sendDirectMessage(receiverId, content.trim())
            if (result.isFailure) {
                showMessage("Message delivery failed")
            }
        }
    }

    fun updateProfile(
        displayName: String,
        username: String,
        avatarUrl: String?,
        bio: String,
        interests: String?,
        instagramHandle: String?
    ) {
        viewModelScope.launch {
            val res = repository.updateProfile(displayName, username, avatarUrl, bio, interests, instagramHandle)
            if (res.isSuccess) {
                showMessage("Profile updated successfully")
            } else {
                showMessage("Failed to update profile")
            }
        }
    }

    fun adminToggleApproval(userId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.adminToggleApproval(userId, !currentStatus)
            showMessage("Member status updated")
        }
    }

    fun adminToggleVerification(userId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.adminToggleVerification(userId, !currentStatus)
            showMessage("Verification badge updated")
        }
    }

    fun signIn(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.signIn(email, pass)
            if (result.isSuccess) {
                showMessage("Welcome back, ${result.getOrNull()?.effectiveDisplayName}!")
                onResult(true, null)
            } else {
                val err = result.exceptionOrNull()?.message ?: "Sign in failed"
                showMessage(err)
                onResult(false, err)
            }
        }
    }

    fun signUp(email: String, pass: String, displayName: String, username: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.signUp(email, pass, displayName, username)
            if (result.isSuccess) {
                showMessage(result.getOrNull() ?: "Account created!")
                onResult(true, null)
            } else {
                val err = result.exceptionOrNull()?.message ?: "Sign up failed"
                showMessage(err)
                onResult(false, err)
            }
        }
    }

    fun recoverPassword(email: String) {
        viewModelScope.launch {
            val res = repository.recoverPassword(email)
            showMessage(res.getOrNull() ?: "Password reset sent")
        }
    }

    fun quickSignInAs(profileId: String) {
        repository.switchUser(profileId)
        showMessage("Switched demo account")
    }

    fun signOut() {
        repository.signOut()
        showMessage("Signed out")
    }
}
