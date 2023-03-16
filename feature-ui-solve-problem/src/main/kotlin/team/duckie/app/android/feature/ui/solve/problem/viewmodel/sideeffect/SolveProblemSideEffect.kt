/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

package team.duckie.app.android.feature.ui.solve.problem.viewmodel.sideeffect

internal sealed class SolveProblemSideEffect {
    class FinishSolveProblem(
        val examId: Int,
        val answers: List<String>,
    ) : SolveProblemSideEffect()

    class ReportError(val exception: Throwable) : SolveProblemSideEffect()

    object NavigatePreviousScreen : SolveProblemSideEffect()
}
