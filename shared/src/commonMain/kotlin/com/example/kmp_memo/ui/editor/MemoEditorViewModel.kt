package com.example.kmp_memo.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.example.kmp_memo.data.repository.MemoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class MemoEditorViewModel(
    private val memoRepository: MemoRepository,
) : ViewModel() {

    private val logger = Logger.withTag("MemoEditorViewModel")
    private val _uiState = MutableStateFlow(MemoEditorUiState())

    val uiState: StateFlow<MemoEditorUiState> =
        _uiState.asStateFlow()

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
        loadMemo(id)
    }

    private fun loadMemo(memoId: Long) {
        viewModelScope.launch {
            val memo = memoRepository.getMemo(memoId)
            _uiState.update { currentState ->
                currentState.copy(
                    title = memo?.title ?: "",
                    content = memo?.content ?: "",
                    timeMillis = memo?.time,
                    isUpdated = memo?.updatedAt != null,
                )
            }
        }
    }

    fun saveMemo() {
        val currentState = _uiState.value

        if (!currentState.isSaveEnabled) return

        _uiState.update { state ->
            state.copy(
                isSaving = true,
                hasSaveError = false,
            )
        }
        val title = currentState.title.trim()
        val content = currentState.content.trim()
        viewModelScope.launch {
            try {
                if (currentState.currentId != null) {
                    memoRepository.updateMemo(
                        currentState.currentId,
                        title,
                        content
                    )
                    _uiState.update { state ->
                        state.copy(
                            savedMemoId = currentState.currentId,
                        )
                    }
                } else {
                    val memoId = memoRepository.createMemo(
                        title = title,
                        content = content,
                    )

                    _uiState.update { state ->
                        state.copy(
                            savedMemoId = memoId,
                        )
                    }
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                logger.e(exception) {
                    "메모 저장에 실패했습니다."
                }
                _uiState.update { state ->
                    state.copy(hasSaveError = true)
                }
            } finally {
                _uiState.update { state ->
                    state.copy(isSaving = false)
                }
            }
        }
    }

    fun resetEditor() {
        _uiState.value = MemoEditorUiState()
    }

    fun consumeSaveResult() {
        _uiState.update { it.copy(savedMemoId = null) }
    }

    fun dismissSaveError() {
        _uiState.update { it.copy(hasSaveError = false) }
    }

    fun deleteMemo() {
        val currentState = _uiState.value
        val memoId = currentState.currentId ?: return

        if (currentState.isDeleting) return

        _uiState.update { state ->
            state.copy(
                isDeleting = true,
                hasDeleteError = false,
            )
        }

        viewModelScope.launch {
            try {
                memoRepository.deleteMemo(memoId)
                _uiState.update { state ->
                    state.copy(isDeleted = true)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                logger.e(exception) {
                    "메모 삭제에 실패했습니다."
                }
                _uiState.update { state ->
                    state.copy(hasDeleteError = true)
                }
            } finally {
                _uiState.update { state ->
                    state.copy(isDeleting = false)
                }
            }
        }
    }

    fun consumeDeleteResult() {
        _uiState.update { it.copy(isDeleted = false) }
    }

    fun dismissDeleteError() {
        _uiState.update { it.copy(hasDeleteError = false) }
    }

}
