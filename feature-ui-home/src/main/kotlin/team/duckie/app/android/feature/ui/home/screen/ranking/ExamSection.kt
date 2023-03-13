/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

@file:OptIn(ExperimentalLifecycleComposeApi::class)

package team.duckie.app.android.feature.ui.home.screen.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import team.duckie.app.android.feature.ui.home.R
import team.duckie.app.android.feature.ui.home.constants.RankingPage
import team.duckie.app.android.feature.ui.home.screen.ranking.sideeffect.RankingSideEffect
import team.duckie.app.android.feature.ui.home.screen.ranking.viewmodel.RankingViewModel
import team.duckie.app.android.shared.ui.compose.ColumnSpacer
import team.duckie.app.android.shared.ui.compose.DuckExamSmallCoverForColumn
import team.duckie.app.android.shared.ui.compose.DuckTestCoverItem
import team.duckie.app.android.shared.ui.compose.TextTabLayout
import team.duckie.app.android.util.kotlin.copy
import team.duckie.app.android.util.kotlin.fastMap
import team.duckie.quackquack.ui.color.QuackColor
import team.duckie.quackquack.ui.component.QuackSingeLazyRowTag
import team.duckie.quackquack.ui.component.QuackTagType
import team.duckie.quackquack.ui.component.QuackTitle2
import team.duckie.quackquack.ui.textstyle.QuackTextStyle

@Composable
internal fun ExamSection(
    viewModel: RankingViewModel,
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val tagNames = remember(state.examTags) {
        state.examTags.fastMap { it.name }.copy { add(0, context.getString(R.string.total)) }
    }
    val textTabs = remember {
        persistentListOf(
            context.getString(R.string.solve_order),
            context.getString(R.string.wrong_answer_rate_order),
        )
    }
    val lazyGridState = rememberLazyGridState()
    val searchExams = viewModel.searchExams.collectAsLazyPagingItems()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.fetchSearchExams("")
        viewModel.fetchPopularTags()
    }

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect {
            if (it is RankingSideEffect.ListPullUp && it.currentTab == RankingPage.Exam.index) {
                lazyGridState.scrollToItem(0)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp),
    ) {
        // TODO:(EvergreenTree97) 태그의 inner padding이 바뀌어야 함
        QuackSingeLazyRowTag(
            items = tagNames,
            itemSelections = state.tagSelections.toImmutableList(),
            tagType = QuackTagType.Circle(),
            onClick = viewModel::changeSelectedTags,
        )
        ColumnSpacer(28.dp)
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            state = lazyGridState,
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(
                span = { HeaderSpan() }
            ) {
                RankingHeader(
                    titles = textTabs,
                    selectedTabIndex = state.selectedExamOrder,
                    onTabSelected = viewModel::setSelectedExamOrder,
                )
            }
            items(count = searchExams.itemCount) { index ->
                searchExams[index]?.let { item ->
                    RankingItem(
                        modifier = Modifier.padding(bottom = 48.dp),
                        duckTestCoverItem = DuckTestCoverItem(
                            testId = item.id,
                            thumbnailUrl = item.thumbnailUrl,
                            nickname = item.user?.nickname ?: "",
                            title = item.title,
                            solvedCount = item.solvedCount ?: 0,
                        ),
                        onItemClick = viewModel::clickExam,
                        rank = index + 1,
                    )
                }
            }
        }
    }
}

@Stable
private val HeaderSpan = { GridItemSpan(2) }

@Composable
private fun RankingHeader(
    titles: ImmutableList<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        QuackTitle2(text = stringResource(id = R.string.popular_tag_ranking))
        TextTabLayout(
            titles = titles,
            tabStyle = QuackTextStyle.Body2.change(color = QuackColor.Gray1),
            selectedTabStyle = QuackTextStyle.Body2.change(color = QuackColor.DuckieOrange),
            selectedTabIndex = selectedTabIndex,
            onTabSelected = onTabSelected,
            space = 16.dp,
        )
    }
}

@Composable
private fun RankingItem(
    modifier: Modifier = Modifier,
    duckTestCoverItem: DuckTestCoverItem,
    onItemClick: (Int) -> Unit,
    rank: Int,
) {
    Box(modifier = modifier.clip(RoundedCornerShape(8.dp))) {
        DuckExamSmallCoverForColumn(
            duckTestCoverItem = duckTestCoverItem,
            onItemClick = { onItemClick(duckTestCoverItem.testId) },
        )
        RankingEdge(rank = rank)
    }
}

@Composable
private fun RankingEdge(rank: Int) {
    Box(
        modifier = Modifier
            .sizeIn(
                minWidth = 24.dp,
                minHeight = 24.dp,
            )
            .background(
                brush = QuackColor.Black.toBrush(),
                alpha = 0.5f,
            ),
        contentAlignment = Alignment.Center,
    ) {
        QuackTitle2(
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 2.dp,
            ),
            color = QuackColor.White,
            text = rank.toString(),
        )
    }
}
