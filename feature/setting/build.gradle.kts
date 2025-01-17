/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

import AppVersionNameProvider.App.VersionCode
import AppVersionNameProvider.App.VersionName
import DependencyHandler.Extensions.implementations

plugins {
    alias(libs.plugins.duckie.android.library)
    alias(libs.plugins.duckie.android.library.compose)
    alias(libs.plugins.duckie.android.hilt)
    alias(libs.plugins.duckie.version.name.provider)
}

android {
    namespace = "team.duckie.app.android.feature.setting"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "APP_VERSION_CODE", "\"$VersionCode\"")
        buildConfigField("String", "APP_VERSION_NAME", "\"$VersionName\"")
    }
}

dependencies {
    implementations(
        platform(libs.firebase.bom),
        projects.di,
        projects.domain,
        projects.navigator,
        projects.common.android,
        projects.common.kotlin,
        projects.common.compose,
        projects.feature.devMode,
        libs.orbit.viewmodel,
        libs.orbit.compose,
        libs.kotlin.collections.immutable,
        libs.quack.v2.ui,
        libs.compose.lifecycle.runtime,
        libs.compose.ui.accompanist.webview,
        libs.firebase.crashlytics,
        libs.ui.oss.license,
        libs.androidx.appcompat,
    )
}
