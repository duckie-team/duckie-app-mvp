/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

package team.duckie.app.android.data.user.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.github.kittinunf.fuel.Fuel
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import team.duckie.app.android.data._datasource.client
import team.duckie.app.android.data._exception.util.responseCatching
import team.duckie.app.android.data._exception.util.responseCatchingFuel
import team.duckie.app.android.data._util.jsonBody
import team.duckie.app.android.data._util.toStringJsonMap
import team.duckie.app.android.data.user.mapper.toDomain
import team.duckie.app.android.data.user.model.UserFollowingsResponse
import team.duckie.app.android.data.user.model.UserResponse
import team.duckie.app.android.data.user.model.UsersResponse
import team.duckie.app.android.domain.auth.usecase.AttachAccessTokenToHeaderUseCase
import team.duckie.app.android.domain.auth.usecase.CheckAccessTokenUseCase
import team.duckie.app.android.domain.category.model.Category
import team.duckie.app.android.domain.tag.model.Tag
import team.duckie.app.android.domain.user.model.User
import team.duckie.app.android.domain.user.model.UserFollowings
import team.duckie.app.android.domain.user.repository.UserRepository
import team.duckie.app.android.feature.datastore.PreferenceKey
import team.duckie.app.android.util.kotlin.AllowMagicNumber
import team.duckie.app.android.util.kotlin.ClientMeIdNull
import team.duckie.app.android.util.kotlin.ClientMeTokenNull
import team.duckie.app.android.util.kotlin.ExperimentalApi
import team.duckie.app.android.util.kotlin.duckieClientLogicProblemException
import team.duckie.app.android.util.kotlin.duckieResponseFieldNpe
import team.duckie.app.android.util.kotlin.fastMap
import team.duckie.app.android.util.kotlin.runtimeCheck
import javax.inject.Inject
import javax.inject.Singleton

// TODO(riflockle7): 결국 User 와 token 은 엮일 수 밖에 없는 요소이다.
//  하지만 기능상 각 Repository 로 분리되는 것도 맞다.
//  그래서 UseCase 로 AuthRepository 를 호출해주었는데 이는 괜찮은 걸까?
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val fuel: Fuel,
    private val checkAccessTokenUseCase: CheckAccessTokenUseCase,
    private val attachAccessTokenToHeaderUseCase: AttachAccessTokenToHeaderUseCase,
    private val dataStore: DataStore<Preferences>,
) : UserRepository {
    private var me: User? = null

    // TODO(riflockle7): 로그인 화면으로 넘겨주는 flow 필요 (throw Error 할지 고민 중)
    override suspend fun getMe(): User? {
        // 1. 토큰 값이 등록되어 있는지 먼저 확인한다 (이게 없으면 유저 정보 가져오는 거 자체가 안됨)
        val meToken = getMeToken()

        // TODO 토큰이 없다면, 로그인 화면으로 보내주어야 한다 (해결책 생각하면 meToken 선언부로 처리 옮길 예정)
        meToken ?: duckieClientLogicProblemException(code = ClientMeTokenNull)

        // 2. 토큰이 있다면 토큰 검증한다.
        val accessTokenValid = checkAccessTokenUseCase(meToken).getOrNull()

        if (accessTokenValid?.passed == true) {
            // 3. id 값이 등록되어 있는지 확인한다.
            val meId = getMeId()

            // TODO id 가 없다면, 로그인 화면으로 보내주어야 한다 (해결책 생각하면 meId 선언부로 처리 옮길 예정)
            meId ?: duckieClientLogicProblemException(code = ClientMeIdNull)

            // 4. me 객체값이 있는지 확인한다
            return me ?: kotlin.run {
                // 5. accessToken 관련 설정
                attachAccessTokenToHeaderUseCase(accessToken = meToken)

                // 6. me 객체가 없다면, id 기반으로 유저 정보를 가져온 후 setMe 를 통해 설정 뒤 반환한다.
                val user = get(meId)
                setMe(user)

                return user
            }
        } else {
            // TODO token 문제가 있다면, 로그인 화면으로 보내주어야 한다 (해결책 생각하면 아래 코드 수정 예정)
            return me
        }
    }

    override suspend fun setMe(newMe: User) {
        me = newMe
    }

    private suspend fun getMeId(): Int? {
        // TODO(riflockle7): 더 좋은 구현 방법이 있을까?
        // ref: https://medium.com/androiddevelopers/datastore-and-synchronous-work-576f3869ec4c
        return dataStore.data.first()[PreferenceKey.User.Id]?.toInt()
    }

    private suspend fun getMeToken(): String? {
        // TODO(riflockle7): 더 좋은 구현 방법이 있을까?
        // ref: https://medium.com/androiddevelopers/datastore-and-synchronous-work-576f3869ec4c
        return dataStore.data.first()[PreferenceKey.Account.AccessToken]
    }

    override suspend fun get(id: Int): User {
        val response = client.get("/users/$id")

        return responseCatching(
            response = response.body(),
            parse = UserResponse::toDomain,
        )
    }

    override suspend fun update(
        id: Int,
        categories: List<Category>?,
        tags: List<Tag>?,
        profileImageUrl: String?,
        nickname: String?,
        status: String?,
    ): User {
        runtimeCheck(
            nickname != null || profileImageUrl != null || categories != null ||
                    tags != null || status != null,
        ) {
            "At least one of the parameters must be non-null"
        }

        val response = client.patch("/users/$id") {
            jsonBody {
                categories?.let { "favoriteCategories" withInts categories.fastMap { it.id } }
                tags?.let { "favoriteTags" withInts tags.fastMap { it.id } }
                profileImageUrl?.let { "profileImageUrl" withString profileImageUrl }
                nickname?.let { "nickname" withString nickname }
                status?.let { "status" withString status }
            }
        }
        return responseCatching(
            response = response.body(),
            parse = UserResponse::toDomain,
        )
    }

    override suspend fun nicknameValidateCheck(nickname: String): Boolean {
        val response = client.get("/users/$nickname/duplicate-check")

        return responseCatching(response.status.value, response.bodyAsText()) { body ->
            val json = body.toStringJsonMap()
            json["success"]?.toBoolean() ?: duckieResponseFieldNpe("success")
        }
    }

    // TODO(riflockle7): GET /users/following API commit (변경점이 많아 TODO 적음)
    @AllowMagicNumber
    @ExperimentalApi
    override suspend fun fetchUserFollowing(userId: Int): UserFollowings =
        withContext(Dispatchers.IO) {
            val (_, response) = fuel
                .get(
                    "/users/following",
                )
                .responseString()

            return@withContext responseCatchingFuel(
                response = response,
                parse = UserFollowingsResponse::toDomain,
            )
        }

    override suspend fun fetchMeFollowers(): List<User> {
        val (_, response) = fuel
            .get("/users/me/followers")
            .responseString()

        return responseCatchingFuel(
            response = response,
            parse = UsersResponse::toDomain,
        )
    }

    override suspend fun fetchMeFollowings(): List<User> {
        val (_, response) = fuel
            .get("/users/me/followings")
            .responseString()

        return responseCatchingFuel(
            response = response,
            parse = UsersResponse::toDomain,
        )
    }
}
