/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

package team.duckie.app.android.data.search.mapper

import team.duckie.app.android.data.exam.mapper.toDomain
import team.duckie.app.android.data.exam.model.ExamData
import team.duckie.app.android.data.search.model.SearchData
import team.duckie.app.android.data.tag.mapper.toDomain
import team.duckie.app.android.data.tag.model.TagData
import team.duckie.app.android.data.user.mapper.toDomain
import team.duckie.app.android.data.user.model.UserResponse
import team.duckie.app.android.domain.search.model.Search
import team.duckie.app.android.util.kotlin.duckieResponseFieldNpe
import team.duckie.app.android.util.kotlin.fastMap

internal fun SearchData.toDomain() = when (this) {
    is SearchData.ExamSearchData -> Search.ExamSearch(
        exams = result?.fastMap(ExamData::toDomain)
            ?: duckieResponseFieldNpe("${this::class.java.simpleName}.result"),
    )

    is SearchData.UserSearchData -> Search.UserSearch(
        users = result?.fastMap(UserResponse::toDomain)
            ?: duckieResponseFieldNpe("${this::class.java.simpleName}.result"),
    )

    is SearchData.TagSearchData -> Search.TagSearch(
        tags = result?.fastMap(TagData::toDomain)
            ?: duckieResponseFieldNpe("${this::class.java.simpleName}.result"),
    )
}
