/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

@file:Suppress("PrivatePropertyName")

package team.duckie.app.android.presentation

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.viewmodel.observe
import team.duckie.app.android.feature.datastore.PreferenceKey
import team.duckie.app.android.feature.datastore.dataStore
import team.duckie.app.android.feature.ui.home.screen.HomeActivity
import team.duckie.app.android.feature.ui.onboard.OnboardActivity
import team.duckie.app.android.presentation.screen.IntroScreen
import team.duckie.app.android.presentation.viewmodel.IntroViewModel
import team.duckie.app.android.presentation.viewmodel.sideeffect.IntroSideEffect
import team.duckie.app.android.util.compose.ToastWrapper
import team.duckie.app.android.util.exception.handling.reporter.reportToCrashlyticsIfNeeded
import team.duckie.app.android.util.kotlin.seconds
import team.duckie.app.android.util.ui.BaseActivity
import team.duckie.app.android.util.ui.changeActivityWithAnimation
import team.duckie.quackquack.ui.theme.QuackTheme

private val SplashScreenExitAnimationDurationMillis = 0.2.seconds
private val SplashScreenFinishDurationMillis = 1.5.seconds

@AndroidEntryPoint
class IntroActivity : BaseActivity() {

    private val vm: IntroViewModel by viewModels()
    private val toast by lazy { ToastWrapper(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                ObjectAnimator.ofFloat(splashScreenView, View.ALPHA, 1f, 0f).run {
                    interpolator = LinearInterpolator()
                    duration = SplashScreenExitAnimationDurationMillis
                    doOnEnd { splashScreenView.remove() }
                    start()
                }
            }
        }

        vm.observe(
            lifecycleOwner = this,
            sideEffect = ::handleSideEffect,
        )

        setContent {
            LaunchedEffect(vm) {
                delay(SplashScreenFinishDurationMillis)
                vm.getUser()
            }

            QuackTheme {
                IntroScreen()
            }
        }
    }

    private suspend fun handleSideEffect(sideEffect: IntroSideEffect) {
        val preference = applicationContext.dataStore.data.first()
        val isOnboardFinsihed = preference[PreferenceKey.Onboard.Finish]
        if (isOnboardFinsihed == null) {
            launchOnboardActivity()
        } else {
            when (sideEffect) {
                is IntroSideEffect.GetUserFinished -> {
                    if (sideEffect.user != null) {
                        launchHomeOrOnboardActivity(isOnboardFinsihed)
                    } else {
                        toast(getString(R.string.expired_access_token_relogin_requried))
                        launchOnboardActivity()
                    }
                }

                is IntroSideEffect.ReportError -> {
                    sideEffect.exception.printStackTrace()
                    sideEffect.exception.reportToCrashlyticsIfNeeded()
                }
            }
        }
    }

    private fun launchOnboardActivity() {
        changeActivityWithAnimation<OnboardActivity>()
    }

    private fun launchHomeActivity() {
        changeActivityWithAnimation<HomeActivity>()
    }

    private fun launchHomeOrOnboardActivity(isOnboardFinish: Boolean) {
        if (isOnboardFinish) {
            launchHomeActivity()
        } else {
            launchOnboardActivity()
        }
    }
}
