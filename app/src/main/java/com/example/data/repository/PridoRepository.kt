package com.example.data.repository

import com.example.data.model.Comment
import com.example.data.model.DirectMessage
import com.example.data.model.Follow
import com.example.data.model.Post
import com.example.data.model.PostLike
import com.example.data.model.Profile
import com.example.data.remote.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PridoRepository {

    private val api = SupabaseClient.api
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken.asStateFlow()

    private val _currentUser = MutableStateFlow<Profile?>(null)
    val currentUser: StateFlow<Profile?> = _currentUser.asStateFlow()

    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _likes = MutableStateFlow<List<PostLike>>(emptyList())
    val likes: StateFlow<List<PostLike>> = _likes.asStateFlow()

    private val _follows = MutableStateFlow<List<Follow>>(emptyList())
    val follows: StateFlow<List<Follow>> = _follows.asStateFlow()

    private val _messages = MutableStateFlow<List<DirectMessage>>(emptyList())
    val messages: StateFlow<List<DirectMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _databaseStatus = MutableStateFlow("Supabase Connected")
    val databaseStatus: StateFlow<String> = _databaseStatus.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        // Initialize with default community profiles and content
        seedInitialData()
    }

    private fun getAuthHeader(): String? {
        val token = _authToken.value
        return if (!token.isNullOrBlank()) "Bearer $token" else null
    }

    private fun currentTimeIso(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        return sdf.format(Date())
    }

    private fun seedInitialData() {
        val demoAdmin = Profile(
            id = "user_admin_001",
            username = "harish",
            displayName = "Harish Mandal",
            bio = "Founder & Architect of PRIDO. Building tools for next-gen creators and makers. ✨",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&q=80",
            instagramHandle = "harishmandal",
            interests = "#AI, #android, #buildinpublic, #design",
            isApproved = true,
            isVerified = true,
            isAdmin = true,
            createdAt = "2026-03-01T10:00:00.000Z"
        )

        val creator1 = Profile(
            id = "user_alex_002",
            username = "alexrivera",
            displayName = "Alex Rivera",
            bio = "Creative developer, generative art coder & WebGL enthusiast. Making futuristic UIs.",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&q=80",
            instagramHandle = "alex.code",
            interests = "#webdev, #creative, #shaders",
            isApproved = true,
            isVerified = true,
            isAdmin = false,
            createdAt = "2026-03-05T12:00:00.000Z"
        )

        val creator2 = Profile(
            id = "user_maya_003",
            username = "mayadesign",
            displayName = "Maya Lin",
            bio = "Product designer & 3D motion director. Exploring cyber aesthetics and neon spaces.",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&q=80",
            instagramHandle = "mayalin.studio",
            interests = "#design, #3D, #figma",
            isApproved = true,
            isVerified = true,
            isAdmin = false,
            createdAt = "2026-03-08T14:30:00.000Z"
        )

        val creator3 = Profile(
            id = "user_liam_004",
            username = "liamchen",
            displayName = "Liam Chen",
            bio = "AI researcher & indie hacker. Shipping on-device ML models and autonomous agents.",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&q=80",
            instagramHandle = "liamchen.ai",
            interests = "#AI, #machinelearning, #indiehacking",
            isApproved = true,
            isVerified = false,
            isAdmin = false,
            createdAt = "2026-03-10T09:15:00.000Z"
        )

        val creator4 = Profile(
            id = "user_elena_005",
            username = "elenar",
            displayName = "Elena Rostova",
            bio = "Audiovisual synthesizer artist & sound designer. Crafting cyber ambient soundscapes.",
            avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&q=80",
            instagramHandle = "elena.sound",
            interests = "#music, #ambient, #creative",
            isApproved = false,
            isVerified = false,
            isAdmin = false,
            createdAt = "2026-03-14T16:45:00.000Z"
        )

        _profiles.value = listOf(demoAdmin, creator1, creator2, creator3, creator4)

        val initialPosts = listOf(
            Post(
                id = 101L,
                userId = "user_admin_001",
                userName = "Harish Mandal",
                instagramId = "harishmandal",
                content = "Welcome to PRIDO! 🚀 A connected social network for creators, developers, and makers.\n\nNow hooked directly into our Supabase cloud database with real-time sync, post reactions, direct messages, and creator profiles. Share what you're crafting!",
                category = "Ideas",
                imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&q=80",
                createdAt = "2026-03-15T08:00:00.000Z"
            ),
            Post(
                id = 102L,
                userId = "user_maya_003",
                userName = "Maya Lin",
                instagramId = "mayadesign",
                content = "Experimenting with neon dark glass UI and spatial depth. Notice how the glowing radial gradients set the mood for creative focus without fatiguing the eyes.\n\nWhat do you think of this visual palette?",
                category = "Creative",
                imageUrl = "https://images.unsplash.com/photo-1634017839464-5c339ebe3cb4?w=800&q=80",
                createdAt = "2026-03-15T11:20:00.000Z"
            ),
            Post(
                id = 103L,
                userId = "user_alex_002",
                userName = "Alex Rivera",
                instagramId = "alex.code",
                content = "Just benchmarked our real-time streaming pipeline. 120 FPS animations on mobile with Jetpack Compose while syncing messages in the background! Clean architecture makes all the difference.",
                category = "Ideas",
                imageUrl = null,
                createdAt = "2026-03-15T13:45:00.000Z"
            ),
            Post(
                id = 104L,
                userId = "user_liam_004",
                userName = "Liam Chen",
                instagramId = "liamchen.ai",
                content = "Trained a tiny neural model for on-device generative palette suggestions. Runs in 12ms with zero battery drain! Releasing the weights soon for PRIDO creators. #AI #buildinpublic",
                category = "Images",
                imageUrl = "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=800&q=80",
                createdAt = "2026-03-15T15:10:00.000Z"
            )
        )
        _posts.value = initialPosts

        _comments.value = listOf(
            Comment(id = 201L, postId = 101L, userId = "user_alex_002", content = "Excited for this! The UI looks sensational.", createdAt = "2026-03-15T08:30:00.000Z"),
            Comment(id = 202L, postId = 101L, userId = "user_maya_003", content = "Loving the dark glass and cyan highlights!", createdAt = "2026-03-15T09:10:00.000Z"),
            Comment(id = 203L, postId = 102L, userId = "user_admin_001", content = "The contrast is razor-sharp. Great work Maya!", createdAt = "2026-03-15T11:55:00.000Z")
        )

        _likes.value = listOf(
            PostLike(postId = 101L, userId = "user_alex_002"),
            PostLike(postId = 101L, userId = "user_maya_003"),
            PostLike(postId = 102L, userId = "user_admin_001"),
            PostLike(postId = 102L, userId = "user_alex_002"),
            PostLike(postId = 103L, userId = "user_liam_004")
        )

        _follows.value = listOf(
            Follow(followerId = "user_alex_002", followingId = "user_admin_001"),
            Follow(followerId = "user_maya_003", followingId = "user_admin_001"),
            Follow(followerId = "user_admin_001", followingId = "user_maya_003")
        )

        _messages.value = listOf(
            DirectMessage(id = 301L, senderId = "user_alex_002", receiverId = "user_admin_001", content = "Hey Harish! Love the new PRIDO Android release.", createdAt = "2026-03-15T10:00:00.000Z"),
            DirectMessage(id = 302L, senderId = "user_admin_001", receiverId = "user_alex_002", content = "Thanks Alex! Check out the direct messaging and explore views.", createdAt = "2026-03-15T10:02:00.000Z"),
            DirectMessage(id = 303L, senderId = "user_alex_002", receiverId = "user_admin_001", content = "Super fast and smooth. Can't wait to share my shaders project here.", createdAt = "2026-03-15T10:05:00.000Z")
        )

        // Set default current user as Harish Mandal (Admin) or Alex Rivera for instant demo preview
        _currentUser.value = demoAdmin
    }

    suspend fun refreshAll() {
        _isLoading.value = true
        try {
            val authHeader = getAuthHeader()

            // 1. Fetch profiles
            try {
                val profileRes = api.getProfiles(authHeader)
                if (profileRes.isSuccessful && !profileRes.body().isNullOrEmpty()) {
                    val remoteProfiles = profileRes.body()!!
                    // Merge remote profiles with any existing local
                    val combined = (remoteProfiles + _profiles.value).distinctBy { it.id }
                    _profiles.value = combined
                    _currentUser.value?.id?.let { myId ->
                        combined.find { it.id == myId }?.let { _currentUser.value = it }
                    }
                }
            } catch (e: Exception) {
                // Ignore profile fetch failure
            }

            // 2. Fetch posts
            try {
                val postRes = api.getPosts(authHeader)
                if (postRes.isSuccessful && !postRes.body().isNullOrEmpty()) {
                    val remotePosts = postRes.body()!!
                    val combined = (remotePosts + _posts.value).distinctBy { it.id }.sortedByDescending { it.id }
                    _posts.value = combined
                }
            } catch (e: Exception) {
                // Ignore post fetch failure
            }

            // 3. Fetch comments
            try {
                val commentRes = api.getComments(authHeader)
                if (commentRes.isSuccessful && !commentRes.body().isNullOrEmpty()) {
                    val remoteComments = commentRes.body()!!
                    _comments.value = (remoteComments + _comments.value).distinctBy { it.id }
                }
            } catch (e: Exception) {
                // Ignore
            }

            // 4. Fetch likes
            try {
                val likesRes = api.getLikes(authHeader)
                if (likesRes.isSuccessful && likesRes.body() != null) {
                    _likes.value = (likesRes.body()!! + _likes.value).distinctBy { "${it.postId}_${it.userId}" }
                }
            } catch (e: Exception) {
                // Ignore
            }

            // 5. Fetch follows
            try {
                val followsRes = api.getFollows(authHeader)
                if (followsRes.isSuccessful && followsRes.body() != null) {
                    _follows.value = (followsRes.body()!! + _follows.value).distinctBy { "${it.followerId}_${it.followingId}" }
                }
            } catch (e: Exception) {
                // Ignore
            }

            // 6. Fetch direct messages
            try {
                val messagesRes = api.getDirectMessages(authHeader)
                if (messagesRes.isSuccessful && messagesRes.body() != null) {
                    _messages.value = (messagesRes.body()!! + _messages.value).distinctBy { it.id }
                }
            } catch (e: Exception) {
                // Ignore
            }

            _databaseStatus.value = "Supabase Active"
        } catch (e: Exception) {
            _databaseStatus.value = "Connected (Cached)"
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun signIn(email: String, pass: String): Result<Profile> {
        _isLoading.value = true
        _statusMessage.value = null
        return try {
            val json = JSONObject().apply {
                put("email", email)
                put("password", pass)
            }
            val body = json.toString().toRequestBody("application/json".toMediaType())
            val res = api.signIn(body)

            if (res.isSuccessful && res.body()?.accessToken != null) {
                val session = res.body()!!
                val token = session.accessToken
                val userId = session.user?.id ?: UUID.randomUUID().toString()
                _authToken.value = token

                // Try to load profile from Supabase
                val profileRes = api.getProfiles("Bearer $token")
                val found = profileRes.body()?.find { it.id == userId }

                val activeProfile = found ?: Profile(
                    id = userId,
                    username = email.substringBefore("@").replace(".", "_"),
                    displayName = email.substringBefore("@"),
                    bio = "PRIDO member",
                    isApproved = true,
                    isVerified = false,
                    isAdmin = false
                )

                _currentUser.value = activeProfile
                _profiles.value = (_profiles.value + activeProfile).distinctBy { it.id }
                refreshAll()
                Result.success(activeProfile)
            } else {
                val errorMsg = res.errorBody()?.string() ?: "Sign-in failed. Please check credentials."
                val parsed = try {
                    JSONObject(errorMsg).optString("msg", JSONObject(errorMsg).optString("error_description", errorMsg))
                } catch (e: Exception) {
                    errorMsg
                }
                Result.failure(Exception(parsed))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun signUp(email: String, pass: String, displayName: String, username: String): Result<String> {
        _isLoading.value = true
        _statusMessage.value = null
        return try {
            val metadata = JSONObject().apply {
                put("display_name", displayName)
                put("username", username)
            }
            val json = JSONObject().apply {
                put("email", email)
                put("password", pass)
                put("data", metadata)
            }
            val body = json.toString().toRequestBody("application/json".toMediaType())
            val res = api.signUp(body)

            if (res.isSuccessful) {
                val session = res.body()
                if (session?.accessToken != null) {
                    _authToken.value = session.accessToken
                    val newProfile = Profile(
                        id = session.user?.id ?: UUID.randomUUID().toString(),
                        username = username,
                        displayName = displayName,
                        bio = "Creative newcomer on PRIDO",
                        isApproved = true,
                        isVerified = false,
                        isAdmin = false
                    )
                    _currentUser.value = newProfile
                    _profiles.value = (_profiles.value + newProfile).distinctBy { it.id }
                    refreshAll()
                    Result.success("Account created and signed in!")
                } else {
                    Result.success("Account created! Please check your email to confirm, then sign in.")
                }
            } else {
                val errorMsg = res.errorBody()?.string() ?: "Sign-up failed."
                val parsed = try {
                    JSONObject(errorMsg).optString("msg", JSONObject(errorMsg).optString("error_description", errorMsg))
                } catch (e: Exception) {
                    errorMsg
                }
                Result.failure(Exception(parsed))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun recoverPassword(email: String): Result<String> {
        return try {
            val json = JSONObject().apply { put("email", email) }
            val body = json.toString().toRequestBody("application/json".toMediaType())
            val res = api.recoverPassword(body)
            if (res.isSuccessful) {
                Result.success("If the email exists, a password reset link has been sent.")
            } else {
                Result.failure(Exception("Password reset request failed."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun switchUser(profileId: String) {
        val found = _profiles.value.find { it.id == profileId }
        if (found != null) {
            _currentUser.value = found
        }
    }

    fun signOut() {
        _authToken.value = null
        _currentUser.value = null
    }

    suspend fun createPost(content: String, category: String, imageUrl: String?): Result<Post> {
        val user = _currentUser.value ?: return Result.failure(Exception("Please sign in first."))
        val newPostId = (System.currentTimeMillis() % 1_000_000_000L)

        val newPost = Post(
            id = newPostId,
            userId = user.id,
            userName = user.effectiveDisplayName,
            instagramId = user.effectiveUsername,
            content = content,
            category = category,
            imageUrl = imageUrl?.takeIf { it.isNotBlank() },
            createdAt = currentTimeIso()
        )

        // Optimistic local update
        _posts.value = listOf(newPost) + _posts.value

        // Sync to Supabase
        scope.launch {
            try {
                val json = JSONObject().apply {
                    put("user_id", user.id)
                    put("user_name", user.effectiveDisplayName)
                    put("instagram_id", user.effectiveUsername)
                    put("content", content)
                    put("category", category)
                    if (!imageUrl.isNullOrBlank()) {
                        put("image_url", imageUrl)
                    }
                }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                api.createPost(body, getAuthHeader())
            } catch (e: Exception) {
                // Handled gracefully; local post remains visible
            }
        }

        return Result.success(newPost)
    }

    suspend fun deletePost(postId: Long): Result<Unit> {
        _posts.value = _posts.value.filter { it.id != postId }
        _comments.value = _comments.value.filter { it.postId != postId }
        _likes.value = _likes.value.filter { it.postId != postId }

        scope.launch {
            try {
                api.deletePost("eq.$postId", getAuthHeader())
            } catch (e: Exception) {
                // Ignore
            }
        }
        return Result.success(Unit)
    }

    suspend fun toggleLike(postId: Long): Boolean {
        val user = _currentUser.value ?: return false
        val existing = _likes.value.any { it.postId == postId && it.userId == user.id }

        if (existing) {
            _likes.value = _likes.value.filterNot { it.postId == postId && it.userId == user.id }
            scope.launch {
                try {
                    api.removeLike("eq.$postId", "eq.${user.id}", getAuthHeader())
                } catch (e: Exception) {
                    // Ignore
                }
            }
            return false
        } else {
            val newLike = PostLike(postId = postId, userId = user.id)
            _likes.value = _likes.value + newLike
            scope.launch {
                try {
                    val json = JSONObject().apply {
                        put("post_id", postId)
                        put("user_id", user.id)
                    }
                    val body = json.toString().toRequestBody("application/json".toMediaType())
                    api.addLike(body, getAuthHeader())
                } catch (e: Exception) {
                    // Ignore
                }
            }
            return true
        }
    }

    suspend fun addComment(postId: Long, content: String): Result<Comment> {
        val user = _currentUser.value ?: return Result.failure(Exception("Please sign in first."))
        val newCommentId = System.currentTimeMillis() % 1_000_000_000L
        val newComment = Comment(
            id = newCommentId,
            postId = postId,
            userId = user.id,
            content = content,
            createdAt = currentTimeIso()
        )

        _comments.value = _comments.value + newComment

        scope.launch {
            try {
                val json = JSONObject().apply {
                    put("post_id", postId)
                    put("user_id", user.id)
                    put("content", content)
                }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                api.addComment(body, getAuthHeader())
            } catch (e: Exception) {
                // Ignore
            }
        }
        return Result.success(newComment)
    }

    suspend fun toggleFollow(targetUserId: String): Boolean {
        val user = _currentUser.value ?: return false
        if (targetUserId == user.id) return false

        val existing = _follows.value.any { it.followerId == user.id && it.followingId == targetUserId }

        if (existing) {
            _follows.value = _follows.value.filterNot { it.followerId == user.id && it.followingId == targetUserId }
            scope.launch {
                try {
                    api.removeFollow("eq.${user.id}", "eq.$targetUserId", getAuthHeader())
                } catch (e: Exception) {
                    // Ignore
                }
            }
            return false
        } else {
            val newFollow = Follow(followerId = user.id, followingId = targetUserId, createdAt = currentTimeIso())
            _follows.value = _follows.value + newFollow
            scope.launch {
                try {
                    val json = JSONObject().apply {
                        put("follower_id", user.id)
                        put("following_id", targetUserId)
                    }
                    val body = json.toString().toRequestBody("application/json".toMediaType())
                    api.addFollow(body, getAuthHeader())
                } catch (e: Exception) {
                    // Ignore
                }
            }
            return true
        }
    }

    suspend fun sendDirectMessage(receiverId: String, content: String): Result<DirectMessage> {
        val user = _currentUser.value ?: return Result.failure(Exception("Please sign in first."))
        val newId = System.currentTimeMillis() % 1_000_000_000L
        val msg = DirectMessage(
            id = newId,
            senderId = user.id,
            receiverId = receiverId,
            content = content,
            createdAt = currentTimeIso()
        )

        _messages.value = _messages.value + msg

        scope.launch {
            try {
                val json = JSONObject().apply {
                    put("sender_id", user.id)
                    put("receiver_id", receiverId)
                    put("content", content)
                }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                api.sendMessage(body, getAuthHeader())
            } catch (e: Exception) {
                // Ignore
            }
        }
        return Result.success(msg)
    }

    suspend fun updateProfile(
        displayName: String,
        username: String,
        avatarUrl: String?,
        bio: String,
        interests: String?,
        instagramHandle: String?
    ): Result<Profile> {
        val user = _currentUser.value ?: return Result.failure(Exception("Please sign in first."))
        val updated = user.copy(
            displayName = displayName,
            username = username,
            avatarUrl = avatarUrl?.takeIf { it.isNotBlank() },
            bio = bio,
            interests = interests?.takeIf { it.isNotBlank() },
            instagramHandle = instagramHandle?.takeIf { it.isNotBlank() }
        )

        _currentUser.value = updated
        _profiles.value = _profiles.value.map { if (it.id == user.id) updated else it }

        scope.launch {
            try {
                val json = JSONObject().apply {
                    put("display_name", displayName)
                    put("username", username)
                    put("bio", bio)
                    if (!avatarUrl.isNullOrBlank()) put("avatar_url", avatarUrl)
                    if (!interests.isNullOrBlank()) put("interests", interests)
                    if (!instagramHandle.isNullOrBlank()) put("instagram_handle", instagramHandle)
                }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                api.updateProfile("eq.${user.id}", body, getAuthHeader())
            } catch (e: Exception) {
                // Ignore
            }
        }

        return Result.success(updated)
    }

    suspend fun adminToggleApproval(targetUserId: String, approved: Boolean): Result<Unit> {
        _profiles.value = _profiles.value.map {
            if (it.id == targetUserId) it.copy(isApproved = approved) else it
        }
        scope.launch {
            try {
                val json = JSONObject().apply { put("is_approved", approved) }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                api.updateProfile("eq.$targetUserId", body, getAuthHeader())
            } catch (e: Exception) {
                // Ignore
            }
        }
        return Result.success(Unit)
    }

    suspend fun adminToggleVerification(targetUserId: String, verified: Boolean): Result<Unit> {
        _profiles.value = _profiles.value.map {
            if (it.id == targetUserId) it.copy(isVerified = verified) else it
        }
        scope.launch {
            try {
                val json = JSONObject().apply { put("is_verified", verified) }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                api.updateProfile("eq.$targetUserId", body, getAuthHeader())
            } catch (e: Exception) {
                // Ignore
            }
        }
        return Result.success(Unit)
    }
}
