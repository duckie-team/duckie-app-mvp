/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

package team.duckie.app.android.data.exam.repository

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import javax.inject.Inject
import team.duckie.app.android.data._datasource.client
import team.duckie.app.android.data._exception.util.responseCatching
import team.duckie.app.android.data._util.toJsonObject
import team.duckie.app.android.data._util.toStringJsonMap
import team.duckie.app.android.data.exam.mapper.toData
import team.duckie.app.android.data.exam.mapper.toDomain
import team.duckie.app.android.data.exam.model.ExamData
import team.duckie.app.android.data.exam.model.ExamInstanceSubmitData
import team.duckie.app.android.domain.exam.model.Exam
import team.duckie.app.android.domain.exam.model.ExamBody
import team.duckie.app.android.domain.exam.model.ExamInstanceBody
import team.duckie.app.android.domain.exam.model.ExamInstanceSubmit
import team.duckie.app.android.domain.exam.model.ExamInstanceSubmitBody
import team.duckie.app.android.domain.exam.model.ExamThumbnailBody
import team.duckie.app.android.domain.exam.repository.ExamRepository
import team.duckie.app.android.util.kotlin.OutOfDateApi
import team.duckie.app.android.util.kotlin.duckieResponseFieldNpe

class ExamRepositoryImpl @Inject constructor() : ExamRepository {
    @OutOfDateApi
    override suspend fun makeExam(exam: ExamBody): Boolean {
        val response = client.post {
            url("/exams")
            setBody(exam.toData())
        }
        return responseCatching(response.bodyAsText()) { body ->
            val json = body.toStringJsonMap()
            json["success"]?.toBoolean() ?: duckieResponseFieldNpe("success")
        }
    }

    @OutOfDateApi
    override suspend fun getExam(id: Int): Exam {
        val response = client.get {
            url("/exams/$id")
        }
        return responseCatching(response, ExamData::toDomain)
    }

    @OutOfDateApi
    override suspend fun getExamThumbnail(examThumbnailBody: ExamThumbnailBody): String {
        val response = client.post {
            url("/exams/thumbnail")
            setBody(examThumbnailBody.toData())
        }
        return responseCatching(response.bodyAsText()) { body ->
            val json = body.toStringJsonMap()
            json["url"] ?: duckieResponseFieldNpe("url")
        }
    }

    @OutOfDateApi
    override suspend fun postExamInstance(examInstanceBody: ExamInstanceBody): Boolean {
        val response = client.post {
            url("/exam-instance")
            setBody(examInstanceBody.toData())
        }
        return responseCatching(response.bodyAsText()) { body ->
            val json = body.toStringJsonMap()
            json["success"]?.toBoolean() ?: duckieResponseFieldNpe("success")
        }
    }

    @OutOfDateApi
    override suspend fun postExamInstanceSubmit(
        id: Int,
        examInstanceSubmitBody: ExamInstanceSubmitBody,
    ): ExamInstanceSubmit {
        val response = client.post {
            url("/exam-instance/$id/submit")
            setBody(examInstanceSubmitBody.toData())
        }
        return responseCatching(response.bodyAsText()) { body ->
            body.toJsonObject<ExamInstanceSubmitData>().toDomain()
        }
    }
}
