/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

package team.duckie.app.android.feature.ui.home.viewmodel.home

import team.duckie.app.android.util.kotlin.FriendsType

internal sealed class HomeSideEffect {

    /**
     * [HomeViewModel] 의 비즈니스 로직 처리 중에 발생한 예외를 [exception] 으로 받고
     * 해당 exception 을 [FirebaseCrashlytics] 에 제보합니다.
     *
     * @param exception 발생한 예외
     */
    class ReportError(val exception: Throwable) : HomeSideEffect()

    /**
     * [SearchResultActivity] 로 이동하는 SideEffect 입니다.
     */
    class NavigateToSearch(val searchTag: String?) : HomeSideEffect()

    /**
     * [HomeDetailActivity] 로 이동하는 SideEffect 입니다.
     */
    class NavigateToHomeDetail(val examId: Int) : HomeSideEffect()

    /**
     * [SettingActivity] 로 이동하는 SideEffect 입니다.
     */
    object NavigateToSetting : HomeSideEffect()

    /**
     * [CreateProblemActivity] 로 이동하는 SideEffect 입니다.
     */
    object NavigateToCreateProblem : HomeSideEffect()

    object NavigateToNotification : HomeSideEffect()

    object ClickRankingRetry : HomeSideEffect()

    class NavigateToFriends(val friendType: FriendsType, val myUserId: Int) : HomeSideEffect()
}
