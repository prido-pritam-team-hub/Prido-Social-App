package com.example.data.remote

import com.example.data.model.AuthSessionResponse
import com.example.data.model.Comment
import com.example.data.model.DirectMessage
import com.example.data.model.Follow
import com.example.data.model.Post
import com.example.data.model.PostLike
import com.example.data.model.Profile
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {

    @POST("auth/v1/token?grant_type=password")
    @Headers("Content-Type: application/json")
    suspend fun signIn(
        @Body requestBody: RequestBody
    ): Response<AuthSessionResponse>

    @POST("auth/v1/signup")
    @Headers("Content-Type: application/json")
    suspend fun signUp(
        @Body requestBody: RequestBody
    ): Response<AuthSessionResponse>

    @POST("auth/v1/recover")
    @Headers("Content-Type: application/json")
    suspend fun recoverPassword(
        @Body requestBody: RequestBody
    ): Response<ResponseBody>

    @GET("rest/v1/profiles?select=*&order=created_at.desc")
    suspend fun getProfiles(
        @Header("Authorization") authHeader: String? = null
    ): Response<List<Profile>>

    @PATCH("rest/v1/profiles")
    @Headers("Prefer: return=representation")
    suspend fun updateProfile(
        @Query("id") idQuery: String, // e.g. "eq.<id>"
        @Body requestBody: RequestBody,
        @Header("Authorization") authHeader: String? = null
    ): Response<List<Profile>>

    @GET("rest/v1/posts?select=*&order=created_at.desc")
    suspend fun getPosts(
        @Header("Authorization") authHeader: String? = null
    ): Response<List<Post>>

    @POST("rest/v1/posts")
    @Headers("Prefer: return=representation")
    suspend fun createPost(
        @Body requestBody: RequestBody,
        @Header("Authorization") authHeader: String? = null
    ): Response<List<Post>>

    @DELETE("rest/v1/posts")
    suspend fun deletePost(
        @Query("id") idQuery: String, // e.g. "eq.<id>"
        @Header("Authorization") authHeader: String? = null
    ): Response<ResponseBody>

    @GET("rest/v1/comments?select=*&order=created_at.asc")
    suspend fun getComments(
        @Header("Authorization") authHeader: String? = null
    ): Response<List<Comment>>

    @POST("rest/v1/comments")
    @Headers("Prefer: return=representation")
    suspend fun addComment(
        @Body requestBody: RequestBody,
        @Header("Authorization") authHeader: String? = null
    ): Response<List<Comment>>

    @GET("rest/v1/post_likes?select=*")
    suspend fun getLikes(
        @Header("Authorization") authHeader: String? = null
    ): Response<List<PostLike>>

    @POST("rest/v1/post_likes")
    suspend fun addLike(
        @Body requestBody: RequestBody,
        @Header("Authorization") authHeader: String? = null
    ): Response<ResponseBody>

    @DELETE("rest/v1/post_likes")
    suspend fun removeLike(
        @Query("post_id") postIdQuery: String,
        @Query("user_id") userIdQuery: String,
        @Header("Authorization") authHeader: String? = null
    ): Response<ResponseBody>

    @GET("rest/v1/prido_follows?select=*")
    suspend fun getFollows(
        @Header("Authorization") authHeader: String? = null
    ): Response<List<Follow>>

    @POST("rest/v1/prido_follows")
    suspend fun addFollow(
        @Body requestBody: RequestBody,
        @Header("Authorization") authHeader: String? = null
    ): Response<ResponseBody>

    @DELETE("rest/v1/prido_follows")
    suspend fun removeFollow(
        @Query("follower_id") followerQuery: String,
        @Query("following_id") followingQuery: String,
        @Header("Authorization") authHeader: String? = null
    ): Response<ResponseBody>

    @GET("rest/v1/direct_messages?select=*&order=created_at.asc")
    suspend fun getDirectMessages(
        @Header("Authorization") authHeader: String? = null
    ): Response<List<DirectMessage>>

    @POST("rest/v1/direct_messages")
    @Headers("Prefer: return=representation")
    suspend fun sendMessage(
        @Body requestBody: RequestBody,
        @Header("Authorization") authHeader: String? = null
    ): Response<List<DirectMessage>>
}
