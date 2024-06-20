/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

import DependencyHandler.Extensions.implementations

plugins {
    alias(libs.plugins.duckie.android.library)
    alias(libs.plugins.duckie.android.hilt)
}

android {
    namespace = "team.duckie.app.android.di"
}

dependencies {
    implementations(
        projects.data,
        projects.domain,
    )
}
