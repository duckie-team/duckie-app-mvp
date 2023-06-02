/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-android/blob/develop/LICENSE
 */

@file:OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class,
)

package team.duckie.app.android.common.compose.ui.domain

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch
import team.duckie.app.android.common.compose.R
import team.duckie.app.android.common.compose.invisible
import team.duckie.app.android.common.compose.rememberToast
import team.duckie.app.android.common.compose.systemBarPaddings
import team.duckie.app.android.common.compose.ui.ImeSpacer
import team.duckie.app.android.common.kotlin.fastForEachIndexed
import team.duckie.app.android.domain.tag.model.Tag
import team.duckie.quackquack.ui.color.QuackColor
import team.duckie.quackquack.ui.component.QuackBasic2TextField
import team.duckie.quackquack.ui.component.QuackCircleTag
import team.duckie.quackquack.ui.component.QuackSubtitle
import team.duckie.quackquack.ui.component.QuackTitle2
import team.duckie.quackquack.ui.icon.QuackIcon

/**
 * 태그를 추가할 수 있는 [ModalBottomSheetLayout]
 * primitive type 인 [String] 대신 [Tag] 를 직접 사용한다
 *
 * 사유: API 연동을 위해
 */
@Composable
fun DuckieTagAddBottomSheet(
    prevTags: List<Tag> = listOf(),
    sheetState: ModalBottomSheetState,
    onDismissRequest: (addedTags: List<Tag>, clearAction: () -> Unit) -> Unit,
    requestAddTag: suspend (String) -> Tag?,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        modifier = Modifier.fillMaxSize(),
        sheetState = sheetState,
        sheetBackgroundColor = QuackColor.White.composeColor,
        scrimColor = QuackColor.Dimmed.composeColor,
        sheetShape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
        ),
        sheetContent = {
            DuckieTagAddBottomSheetContent(
                prevTags = prevTags,
                onDismissRequest = onDismissRequest,
                requestAddTag = requestAddTag,
            )
        },
    ) {
        content()
    }
}

/**
 * 태그를 추가할 수 있는 바텀 시트 content
 *
 * @param prevTags 해당 바텀 시트를 호출한 화면에서 가지고 있는 태그 목록
 * @param onDismissRequest 해당 바텀 시트 content 종료 시 실행되는 함수
 * @param requestAddTag 태그 추가 요청하는 로직. 대체로 API 로직이다.
 */
@Composable
private fun DuckieTagAddBottomSheetContent(
    prevTags: List<Tag>,
    onDismissRequest: (addedTags: List<Tag>, clearAction: () -> Unit) -> Unit,
    requestAddTag: suspend (String) -> Tag?,
) {
    val coroutineScope = rememberCoroutineScope()
    val toast = rememberToast()
    val context = LocalContext.current

    val inputtedTags = remember { mutableStateListOf<Tag>() }
    var tagInput by remember { mutableStateOf("") }

    val keyboard = LocalSoftwareKeyboardController.current

    fun updateTagInput() {
        if (tagInput.isNotBlank()) {
            if ((prevTags.map { it.name } + inputtedTags).contains(tagInput)) {
                toast(context.getString(R.string.tag_toast_already_added))
                return
            }

            coroutineScope.launch {
                val newTag = requestAddTag(tagInput)

                newTag?.let {
                    inputtedTags.add(newTag)
                    tagInput = ""
                } ?: toast(context.getString(R.string.tag_toast_network_error))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = systemBarPaddings.calculateBottomPadding()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 16.dp,
                    horizontal = 16.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            QuackTitle2(text = stringResource(R.string.tag_added_tag))
            QuackSubtitle(
                text = stringResource(R.string.tag_edit_button_done),
                color = QuackColor.DuckieOrange,
                padding = PaddingValues(vertical = 4.dp),
                onClick = {
                    onDismissRequest(inputtedTags) {
                        inputtedTags.clear()
                        keyboard?.hide()
                    }
                },
            )
        }
        if (inputtedTags.isNotEmpty()) {
            Box(modifier = Modifier.padding()) {
                QuackCircleTag(
                    modifier = Modifier
                        .zIndex(0f)
                        .invisible(),
                    text = "",
                    isSelected = false,
                )

                // TODO(sungbin): 애니메이션 출처 밝히기
                FlowRow(
                    modifier = Modifier
                        .zIndex(1f)
                        .padding(
                            vertical = 12.dp,
                            horizontal = 16.dp,
                        ),
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp,
                ) {
                    inputtedTags.fastForEachIndexed { index, tag ->
                        QuackCircleTag(
                            text = tag.name,
                            isSelected = false,
                            trailingIcon = QuackIcon.Close,
                        ) {
                            inputtedTags.remove(inputtedTags[index])
                        }
                    }
                }
            }
        }
        QuackBasic2TextField(
            text = tagInput,
            onTextChanged = { tagInput = it },
            placeholderText = stringResource(R.string.tag_add_manual_placeholder),
            leadingStartPadding = 16.dp,
            trailingEndPadding = 10.dp,
            trailingIcon = QuackIcon.ArrowSend,
            trailingIconOnClick = ::updateTagInput,
            keyboardActions = KeyboardActions { updateTagInput() },
        )
        ImeSpacer()
    }
}
