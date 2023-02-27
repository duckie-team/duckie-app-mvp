/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

package team.duckie.app.android.shared.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import team.duckie.quackquack.ui.component.QuackBody2
import team.duckie.quackquack.ui.component.QuackTitle2
import team.duckie.quackquack.ui.modifier.quackClickable

data class DuckTestCoverItem(
    val testId: Int,
    val coverImg: String?,
    val nickname: String,
    val title: String,
    val examineeNumber: Int,
)

private const val CoverRatio = 4f / 3f

@Composable
fun DuckTestSmallCover(
    modifier: Modifier = Modifier,
    duckTestCoverItem: DuckTestCoverItem,
    onItemClick: (Int) -> Unit,
) {
    Column(
        modifier = modifier
            .width(158.dp)
            .quackClickable(rippleEnabled = true) {
                onItemClick(duckTestCoverItem.testId)
            },
        horizontalAlignment = Alignment.Start,
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(CoverRatio)
                .clip(RoundedCornerShape(8.dp)),
            model = duckTestCoverItem.coverImg,
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
        )
        QuackBody2(
            modifier = Modifier.padding(top = 8.dp),
            text = duckTestCoverItem.nickname,
        )
        QuackTitle2(
            modifier = Modifier.padding(top = 4.dp),
            text = duckTestCoverItem.title,
        )
        QuackBody2(
            modifier = Modifier.padding(top = 8.dp),
            text = "${stringResource(id = R.string.examinee)} ${duckTestCoverItem.examineeNumber}",
        )
    }
}
