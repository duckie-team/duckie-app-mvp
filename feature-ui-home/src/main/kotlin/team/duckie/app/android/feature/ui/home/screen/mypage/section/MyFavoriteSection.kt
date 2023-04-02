/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

package team.duckie.app.android.feature.ui.home.screen.mypage.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import team.duckie.app.android.domain.tag.model.Tag
import team.duckie.quackquack.ui.component.QuackSingeLazyRowTag
import team.duckie.quackquack.ui.component.QuackTagType
import team.duckie.quackquack.ui.component.QuackTitle2

@Composable
internal fun MyFavoriteTagSection(
    title: String,
    tags: ImmutableList<Tag>,
) {
    val tagList = remember(tags) { tags.map { it.name }.toList() }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        QuackTitle2(text = title)
        QuackSingeLazyRowTag(items = tagList, tagType = QuackTagType.Circle(), onClick = {})
    }
}
