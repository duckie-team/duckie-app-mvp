/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

package team.duckie.app.android.data.exam.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import team.duckie.app.android.data.exam.model.ExamRequest
import javax.inject.Inject

class ExamDataSource @Inject constructor(
    private val client: HttpClient
) {
    suspend fun postExams(examRequest: ExamRequest): Boolean {
        val request = client
            .post {
                url("/exams")
                setBody(examRequest)
                //header("authorization", "AT") //TODO(Evergreen): access token 자동화 방안 마련 필요
            }
        return request.body()
    }
}
