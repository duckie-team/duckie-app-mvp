/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

package team.duckie.app.android.domain.exam.repository

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import team.duckie.app.android.domain.exam.model.Exam
import team.duckie.app.android.domain.exam.model.ExamBody
import team.duckie.app.android.domain.exam.model.ExamThumbnailBody

@Immutable
interface ExamRepository {
    suspend fun makeExam(exam: ExamBody): Boolean

    suspend fun getExam(id: Int): Exam

    suspend fun getExamThumbnail(examThumbnailBody: ExamThumbnailBody): String

    suspend fun getExamMeFollowing(page: Int?): List<Exam>

    fun getRecentExam(): ImmutableList<Exam>
}
