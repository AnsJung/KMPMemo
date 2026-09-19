package com.example.kmp_memo.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmp_memo.data.model.Memo
import com.example.kmp_memo.data.repository.MemoRepository
import com.example.kmp_memo.ui.formatter.toMemoDateTimeText
import kmpmemo.shared.generated.resources.Res
import kmpmemo.shared.generated.resources.memo_list_created_at
import kmpmemo.shared.generated.resources.memo_list_updated_at
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

class MemoEditorViewModel(
    private val memoRepository: MemoRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoEditorUiState())

    val uiState: StateFlow<MemoEditorUiState> =
        _uiState.asStateFlow()

    fun setCurrentId(memoId: Long) {
        _uiState.update { currentState ->
            currentState.copy(currentId = memoId)
        }
    }

    fun setViewMode(isViewMode: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isViewMode = isViewMode)
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { currentState ->
            currentState.copy(title = title)
        }
    }

    fun onContentChanged(content: String) {
        _uiState.update { currentState ->
            currentState.copy(content = content)
        }
    }

    fun openMemo(id: Long) {
        _uiState.value = MemoEditorUiState(
            currentId = id,
            isViewMode = true,
        )
        getMemo(id)
    }

    private fun getMemo(memoId: Long) {
        viewModelScope.launch {
            val memo = memoRepository.getMemo(memoId)
            _uiState.update { currentState ->
                currentState.copy(
                    title = memo?.title ?: "",
                    content = memo?.content ?: "",
                    time = setTimeText(memo)
                )
            }
        }
    }

    private fun setTimeText(memo: Memo?): String {
        if (memo != null) {
            val timeLabel = if (memo.updatedAt == null) {
                "생성"
            } else {
                "수정"
            }
            return buildString {
                append(memo.time.toMemoDateTimeText())
                append(" $timeLabel")
            }
        } else {
            return ""
        }
    }

    fun saveMemo() {
        val currentState = _uiState.value

        if (!currentState.isSaveEnabled) return

        _uiState.update { state ->
            state.copy(isSaving = true)
        }

        viewModelScope.launch {
            val memoId = memoRepository.createMemo(
                title = currentState.title.trim(),
                content = currentState.content.trim(),
            )

            _uiState.update { state ->
                state.copy(
                    isSaving = false,
                    savedMemoId = memoId,
                )
            }
        }
    }

    fun resetEditor() {
        _uiState.value = MemoEditorUiState()
    }

    fun consumeSaveResult() {
        _uiState.value = MemoEditorUiState()
    }

    fun discardDraft() {
        _uiState.value = MemoEditorUiState()
    }
}