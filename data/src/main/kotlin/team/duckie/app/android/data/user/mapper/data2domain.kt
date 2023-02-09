/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

@file:Suppress("ConstPropertyName", "PrivatePropertyName")

package team.duckie.app.android.data.user.mapper

import kotlin.random.Random
import kotlinx.collections.immutable.toImmutableList
import team.duckie.app.android.data.category.mapper.toDomain
import team.duckie.app.android.data.category.model.CategoryData
import team.duckie.app.android.data.follow.mapper.toDomain
import team.duckie.app.android.data.tag.mapper.toDomain
import team.duckie.app.android.data.tag.model.TagData
import team.duckie.app.android.data.user.model.DuckPowerResponse
import team.duckie.app.android.data.user.model.UserFollowingRecommendationsResponse
import team.duckie.app.android.data.user.model.UserFollowingResponse
import team.duckie.app.android.data.user.model.UserResponse
import team.duckie.app.android.domain.user.model.DuckPower
import team.duckie.app.android.domain.user.model.User
import team.duckie.app.android.domain.user.model.UserFollowing
import team.duckie.app.android.domain.user.model.UserFollowingRecommendations
import team.duckie.app.android.util.kotlin.duckieResponseFieldNpe
import team.duckie.app.android.util.kotlin.fastMap

private const val NicknameSuffixMaxLength = 10_000
private const val NicknameSuffixLength = 4
private val DuckieDefaultNickname: String
    get() {
        val suffix = Random.nextInt(NicknameSuffixMaxLength)
            .toString()
            .padStart(NicknameSuffixLength, '0')
        return "덕키즈_$suffix"
    }

internal fun UserResponse.toDomain() = User(
    id = id ?: duckieResponseFieldNpe("id"),
    nickname = nickName ?: DuckieDefaultNickname,
    profileImageUrl = profileImageUrl,
    status = status,
    duckPower = duckPower?.toDomain(),
    follow = follow?.toDomain(),
    favoriteTags = favoriteTags?.fastMap(TagData::toDomain)?.toImmutableList(),
    favoriteCategories = favoriteCategories?.fastMap(CategoryData::toDomain)?.toImmutableList(),
    permissions = permissions,
)

internal fun UserFollowingRecommendationsResponse.toDomain() = UserFollowingRecommendations(
    category = category?.toDomain()
        ?: duckieResponseFieldNpe("${this::class.java.simpleName}.category"),
    user = user?.fastMap(UserResponse::toDomain)
        ?: duckieResponseFieldNpe("${this::class.java.simpleName}.user"),
)

internal fun UserFollowingResponse.toDomain() = UserFollowing(
    followingRecommendations = followingRecommendations?.fastMap(UserFollowingRecommendationsResponse::toDomain)
        ?: duckieResponseFieldNpe("${this::class.java.simpleName}.followingRecommendations"),
)

internal fun DuckPowerResponse.toDomain(): DuckPower {
    return DuckPower(
        id = id ?: duckieResponseFieldNpe("${this::class.java.simpleName}.id"),
        tier = tier ?: duckieResponseFieldNpe("${this::class.java.simpleName}.tier"),
        tag = tag?.toDomain() ?: duckieResponseFieldNpe("${this::class.java.simpleName}.tag"),
    )
}
