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

    /**
     * 메모 보기 모드와 작성 또는 수정 모드를 전환한다.
     */
    fun setViewMode(isViewMode: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(isViewMode = isViewMode)
        }
    }

    /**
     * 사용자가 입력한 제목을 화면 상태에 반영한다.
     */
    fun onTitleChanged(title: String) {
        _uiState.update { currentState ->
            currentState.copy(title = title)
        }
    }

    /**
     * 사용자가 입력한 본문을 화면 상태에 반영한다.
     */
    fun onContentChanged(content: String) {
        _uiState.update { currentState ->
            currentState.copy(content = content)
        }
    }

    /**
     * 기존 메모를 보기 위한 초기 상태를 만들고 해당 메모를 불러온다.
     */
    fun openMemo(id: Long) {
        _uiState.value = MemoEditorUiState(
            currentId = id,
            isViewMode = true,
        )
        loadMemo(id)
    }

    /**
     * 저장소에서 메모를 조회해 제목, 본문 및 시간 정보를 화면 상태에 반영한다.
     */
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

    /**
     * 현재 입력값으로 새 메모를 생성하거나 기존 메모를 수정한다.
     */
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
                } else {
                    memoRepository.createMemo(
                        title = title,
                        content = content,
                    )
                }
                _uiState.update { state ->
                    state.copy(result = MemoEditorResult.Saved)
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

    /**
     * 새 메모를 작성할 수 있도록 편집기 상태를 초기값으로 되돌린다.
     */
    fun resetEditor() {
        _uiState.value = MemoEditorUiState()
    }

    /**
     * 화면에서 처리한 저장 또는 삭제 완료 결과를 제거한다.
     */
    fun consumeResult() {
        _uiState.update { it.copy(result = null) }
    }

    /**
     * 저장 실패 안내를 닫고 오류 상태를 해제한다.
     */
    fun dismissSaveError() {
        _uiState.update { it.copy(hasSaveError = false) }
    }

    /**
     * 현재 열려 있는 메모를 저장소에서 삭제한다.
     */
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
                    state.copy(result = MemoEditorResult.Deleted)
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

    /**
     * 삭제 실패 안내를 닫고 오류 상태를 해제한다.
     */
    fun dismissDeleteError() {
        _uiState.update { it.copy(hasDeleteError = false) }
    }

}
