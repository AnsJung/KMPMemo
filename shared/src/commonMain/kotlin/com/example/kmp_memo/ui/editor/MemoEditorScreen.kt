package com.example.kmp_memo.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kmp_memo.ui.dialog.MemoDialog
import com.example.kmp_memo.ui.formatter.toMemoDateTimeText
import com.example.kmp_memo.ui.theme.MemoBodyText
import com.example.kmp_memo.ui.theme.MemoTheme
import kmpmemo.shared.generated.resources.Res
import kmpmemo.shared.generated.resources.memo_created_at
import kmpmemo.shared.generated.resources.memo_editor_content_label
import kmpmemo.shared.generated.resources.memo_editor_content_length
import kmpmemo.shared.generated.resources.memo_editor_content_placeholder
import kmpmemo.shared.generated.resources.memo_editor_edit
import kmpmemo.shared.generated.resources.memo_editor_new_title
import kmpmemo.shared.generated.resources.memo_editor_save
import kmpmemo.shared.generated.resources.memo_editor_save_error_message
import kmpmemo.shared.generated.resources.memo_editor_save_error_title
import kmpmemo.shared.generated.resources.memo_editor_saved_title
import kmpmemo.shared.generated.resources.memo_editor_title_label
import kmpmemo.shared.generated.resources.memo_editor_title_placeholder
import kmpmemo.shared.generated.resources.memo_list_untitled
import kmpmemo.shared.generated.resources.memo_updated_at
import kmpmemo.shared.generated.resources.common_confirm
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MemoEditorScreen(
    currentId: Long? = null,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MemoEditorViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(currentId) {
        if (currentId != null) {
            viewModel.openMemo(currentId)
        } else {
            viewModel.resetEditor()
        }
    }
    val savedMemoId = uiState.savedMemoId

    LaunchedEffect(savedMemoId) {
        if (savedMemoId != null) {
            // 화면 이동으로 이 Composable이 사라지기 전에 저장 결과를 먼저 소비한다.
            viewModel.consumeSaveResult()
            onSaved()
        }
    }
    val displayState = when (currentId) {
        uiState.currentId -> {
            uiState
        }

        null -> {
            // 새 글 작성으로 이동 중
            MemoEditorUiState()
        }

        else -> {
            // 기존 메모 보기로 이동 중
            MemoEditorUiState(
                currentId = currentId,
                isViewMode = true,
            )
        }
    }
    MemoEditorContent(
        uiState = displayState,
        onTitleChanged = viewModel::onTitleChanged,
        onContentChanged = viewModel::onContentChanged,
        onSaveClick = viewModel::saveMemo,
        modifier = modifier,
        onEditClick = { viewModel.setViewMode(isViewMode = false) }
    )

    if (displayState.hasSaveError) {
        MemoDialog(
            title = stringResource(Res.string.memo_editor_save_error_title),
            message = stringResource(Res.string.memo_editor_save_error_message),
            confirmText = stringResource(Res.string.common_confirm),
            onConfirm = viewModel::dismissSaveError,
            onDismiss = viewModel::dismissSaveError,
        )
    }
}

@Composable
private fun MemoEditorContent(
    uiState: MemoEditorUiState,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
    ) {
        if (uiState.isViewMode) {
            MemoViewContent(
                uiState = uiState,
                onEditClick = onEditClick
            )
        } else {
            MemoEditContent(
                uiState = uiState,
                onTitleChanged = onTitleChanged,
                onContentChanged = onContentChanged,
                onSaveClick = onSaveClick,
            )
        }
    }
}

@Composable
private fun MemoViewContent(
    uiState: MemoEditorUiState,
    onEditClick: () -> Unit
) {
    val timeText = when {
        uiState.timeMillis == null -> ""

        uiState.isUpdated -> stringResource(
            Res.string.memo_updated_at,
            uiState.timeMillis.toMemoDateTimeText(),
        )

        else -> stringResource(
            Res.string.memo_created_at,
            uiState.timeMillis.toMemoDateTimeText(),
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = uiState.title.ifBlank {
                    stringResource(Res.string.memo_list_untitled)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Button(
                onClick = onEditClick,
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.outline,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            ) {
                Text(
                    text = stringResource(Res.string.memo_editor_edit),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        if (timeText.isNotEmpty()) {
            Text(
                text = timeText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (uiState.content.isNotBlank()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = uiState.content,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
                color = MemoBodyText,
            )
        }
    }
}

@Composable
private fun ColumnScope.MemoEditContent(
    uiState: MemoEditorUiState,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
) {
    EditorHeader(
        isSavedMemo = uiState.currentId != null,
        isSaveEnabled = uiState.isSaveEnabled,
        onSaveClick = onSaveClick,
    )

    Spacer(modifier = Modifier.height(32.dp))
    EditorFieldLabel(
        text = stringResource(Res.string.memo_editor_title_label),
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = uiState.title,
        onValueChange = onTitleChanged,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        enabled = !uiState.isSaving,
        placeholder = {
            Text(
                text = stringResource(Res.string.memo_editor_title_placeholder),
                style = MaterialTheme.typography.titleSmall,
            )
        },
        textStyle = MaterialTheme.typography.titleSmall,
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        colors = memoEditorTextFieldColors(),
    )
    Spacer(modifier = Modifier.height(32.dp))

    EditorFieldLabel(
        text = stringResource(Res.string.memo_editor_content_label),
    )
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(
        value = uiState.content,
        onValueChange = onContentChanged,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .heightIn(min = 240.dp),
        enabled = !uiState.isSaving,
        placeholder = {
            Text(
                text = stringResource(Res.string.memo_editor_content_placeholder),
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = RoundedCornerShape(8.dp),
        colors = memoEditorTextFieldColors(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(
            Res.string.memo_editor_content_length,
            uiState.contentLength,
        ),
        modifier = Modifier.align(Alignment.End),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun EditorHeader(
    isSavedMemo: Boolean,
    isSaveEnabled: Boolean,
    onSaveClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
    ) {
        Text(
            text = if (isSavedMemo) {
                stringResource(Res.string.memo_editor_saved_title)
            } else {
                stringResource(Res.string.memo_editor_new_title)
            },
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Button(
            onClick = onSaveClick,
            enabled = isSaveEnabled,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(72.dp)
                .height(40.dp),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.outline,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) {
            Text(
                text = stringResource(Res.string.memo_editor_save),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun EditorFieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun memoEditorTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    disabledTextColor = MaterialTheme.colorScheme.onSurface,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    disabledContainerColor = MaterialTheme.colorScheme.surface,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    disabledBorderColor = MaterialTheme.colorScheme.outline,
    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

@Preview(name = "새 메모 - 빈 상태", showBackground = true)
@Composable
private fun EmptyMemoEditorPreview() {
    MemoTheme {
        MemoEditorContent(
            uiState = MemoEditorUiState(),
            onTitleChanged = {},
            onContentChanged = {},
            onSaveClick = {},
            onEditClick = { },
        )
    }
}

@Preview(name = "새 메모 - 작성 상태", showBackground = true)
@Composable
private fun FilledMemoEditorPreview() {
    MemoTheme {
        MemoEditorContent(
            uiState = MemoEditorUiState(
                title = "Koin 핵심 개념",
                content = "의존성 주입은 객체가 필요한 의존성을 외부에서 전달받는 방식이다.",
            ),
            onTitleChanged = {},
            onContentChanged = {},
            onSaveClick = {},
            onEditClick = {},
        )
    }
}

@Preview(name = "기존 메모 - 보기 상태", showBackground = true)
@Composable
private fun FilledMemoViewModePreview() {
    MemoTheme {
        MemoEditorContent(
            uiState = MemoEditorUiState(
                title = "Koin 핵심 개념",
                content = "의존성 주입은 객체가 필요한 의존성을 외부에서 전달받는 방식이다.",
                isViewMode = true,
                timeMillis = 1_789_010_520_000L,
                isUpdated = true,
            ),
            onTitleChanged = {},
            onContentChanged = {},
            onSaveClick = {},
            onEditClick = {}
        )
    }
}
