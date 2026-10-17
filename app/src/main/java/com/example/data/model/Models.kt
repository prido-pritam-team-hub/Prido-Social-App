package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Profile(
    @Json(name = "id") val id: String,
    @Json(name = "username") val username: String? = null,
    @Json(name = "display_name") val displayName: String? = null,
    @Json(name = "bio") val bio: String? = null,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "instagram_handle") val instagramHandle: String? = null,
    @Json(name = "interests") val interests: String? = null,
    @Json(name = "is_approved") val isApproved: Boolean = false,
    @Json(name = "is_verified") val isVerified: Boolean = false,
    @Json(name = "is_admin") val isAdmin: Boolean = false,
    @Json(name = "created_at") val createdAt: String? = null
) {
    val effectiveDisplayName: String
        get() = displayName?.takeIf { it.isNotBlank() } ?: username?.takeIf { it.isNotBlank() } ?: "PRIDO Member"

    val effectiveUsername: String
        get() = username?.takeIf { it.isNotBlank() } ?: "member"

    val effectiveAvatarUrl: String
        get() = avatarUrl?.takeIf { it.isNotBlank() }
            ?: "https://api.dicebear.com/9.x/initials/svg?seed=${effectiveDisplayName.replace(" ", "+")}"
}

@JsonClass(generateAdapter = true)
data class Post(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "user_name") val userName: String? = null,
    @Json(name = "instagram_id") val instagramId: String? = null,
    @Json(name = "content") val content: String = "",
    @Json(name = "category") val category: String? = "Ideas",
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class Comment(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "post_id") val postId: Long = 0L,
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "content") val content: String = "",
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class PostLike(
    @Json(name = "post_id") val postId: Long = 0L,
    @Json(name = "user_id") val userId: String = ""
)

@JsonClass(generateAdapter = true)
data class Follow(
    @Json(name = "follower_id") val followerId: String = "",
    @Json(name = "following_id") val followingId: String = "",
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class DirectMessage(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "sender_id") val senderId: String = "",
    @Json(name = "receiver_id") val receiverId: String = "",
    @Json(name = "content") val content: String = "",
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class AuthSessionResponse(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "token_type") val tokenType: String? = null,
    @Json(name = "expires_in") val expiresIn: Long? = null,
    @Json(name = "user") val user: AuthUser? = null
)

@JsonClass(generateAdapter = true)
data class AuthUser(
    @Json(name = "id") val id: String = "",
    @Json(name = "email") val email: String? = null,
    @Json(name = "user_metadata") val userMetadata: Map<String, Any?>? = null
)

data class PostUiModel(
    val post: Post,
    val author: Profile,
    val likesCount: Int,
    val isLiked: Boolean,
    val commentsCount: Int,
    val comments: List<CommentUiModel>
)

data class CommentUiModel(
    val comment: Comment,
    val author: Profile
)
