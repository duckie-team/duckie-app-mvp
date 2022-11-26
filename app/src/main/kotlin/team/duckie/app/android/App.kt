/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

package team.duckie.app.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import team.duckie.app.android.util.kotlin.seconds
import team.duckie.quackquack.ui.animation.QuackAnimationMillis
import team.duckie.quackquack.ui.modifier.QuackAlwaysShowRipple

@HiltAndroidApp
class App : Application() {
    init {
        // 인앱에서 250 ms 는 너무 길게 느껴짐
        QuackAnimationMillis = 0.15.seconds.toInt()
        QuackAlwaysShowRipple = BuildConfig.ALWAYS_RIPPLE
    }
}
