/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

package team.duckie.app.android.feature.ui.home.screen.mypage.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import team.duckie.app.android.feature.ui.home.R
import team.duckie.app.android.shared.ui.compose.Spacer
import team.duckie.quackquack.ui.border.QuackBorder
import team.duckie.quackquack.ui.color.QuackColor
import team.duckie.quackquack.ui.component.QuackSurface
import team.duckie.quackquack.ui.component.internal.QuackText
import team.duckie.quackquack.ui.textstyle.QuackTextStyle

@Composable
internal fun EditSection(
    onClickEditProfile: () -> Unit,
    onClickEditTag: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        EditButton(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.edit_profile),
            onClick = onClickEditProfile,
            plainTextColor = QuackColor.Black,
        )
        Spacer(space = 8.dp)
        EditButton(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.edit_tag),
            onClick = onClickEditTag,
            plainTextColor = QuackColor.Black,
        )
    }
}

@Composable
internal fun FollowSection(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    EditButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(id = R.string.edit_tag),
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
private fun EditButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = false,
    plainTextColor: QuackColor = QuackColor.Gray1,
) {
    QuackSurface(
        modifier = modifier,
        backgroundColor = if (enabled) {
            QuackColor.White
        } else {
            QuackColor.Gray4
        },
        border = if (enabled) {
            QuackBorder(
                width = 1.dp,
                color = QuackColor.DuckieOrange,
            )
        } else {
            null
        },
        shape = RoundedCornerShape(size = 8.dp),
        onClick = onClick,
    ) {
        QuackText(
            modifier = Modifier.padding(
                vertical = 8.dp,
                horizontal = 12.dp,
            ),
            text = text,
            style = QuackTextStyle.Body1.change(
                color = if (enabled) {
                    QuackColor.DuckieOrange
                } else {
                    plainTextColor
                },
                textAlign = TextAlign.Center,
            ),
            singleLine = true,
        )
    }
}
