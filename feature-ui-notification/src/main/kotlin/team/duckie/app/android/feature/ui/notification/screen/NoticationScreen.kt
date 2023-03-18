/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

@file:OptIn(ExperimentalLifecycleComposeApi::class)

package team.duckie.app.android.feature.ui.notification.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import team.duckie.app.android.feature.ui.notification.R
import team.duckie.app.android.feature.ui.notification.viewmodel.NotificationViewModel
import team.duckie.app.android.shared.ui.compose.skeleton
import team.duckie.app.android.util.compose.activityViewModel
import team.duckie.app.android.util.kotlin.getDiffDayFromToday
import team.duckie.quackquack.ui.component.QuackBody2
import team.duckie.quackquack.ui.component.QuackBody3
import team.duckie.quackquack.ui.component.QuackImage
import team.duckie.quackquack.ui.component.QuackTopAppBar
import team.duckie.quackquack.ui.icon.QuackIcon
import team.duckie.quackquack.ui.modifier.quackClickable
import team.duckie.quackquack.ui.shape.SquircleShape
import team.duckie.quackquack.ui.util.DpSize

@Composable
internal fun NotificationScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = activityViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getNotifications()
    }

    Column(modifier = modifier) {
        QuackTopAppBar(
            leadingIcon = QuackIcon.ArrowBack,
            leadingText = stringResource(id = R.string.notification),
            onLeadingIconClick = viewModel::clickBackPress,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            items(
                items = state.notifications,
                key = { it.id },
            ) { notification ->
                with(notification) {
                    NotificationItem(
                        thumbnailUrl = thumbnailUrl,
                        body = body,
                        createdAt = { createdAt.getDiffDayFromToday() },
                        isLoading = state.isLoading,
                        onClick = { viewModel.clickNotification(notification.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    thumbnailUrl: String,
    body: String,
    createdAt: () -> String,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .quackClickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        QuackImage(
            modifier = Modifier.skeleton(
                visible = isLoading,
                shape = SquircleShape,
            ),
            src = thumbnailUrl,
            size = DpSize(all = 36.dp),
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            QuackBody2(
                modifier = Modifier.skeleton(visible = isLoading),
                text = body,
            )
            QuackBody3(
                modifier = Modifier.skeleton(visible = isLoading),
                text = createdAt(),
            )
        }
    }
}
