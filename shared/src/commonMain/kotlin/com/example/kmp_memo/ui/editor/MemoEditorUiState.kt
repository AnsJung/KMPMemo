package com.example.kmp_memo.ui.editor

sealed interface MemoEditorResult {
    data object Saved : MemoEditorResult
    data object Deleted : MemoEditorResult
}

data class MemoEditorUiState(
    val currentId: Long? = null,
    val title: String = "",
    val content: String = "",
    val isSaving: Boolean = false,
    val result: MemoEditorResult? = null,
    val isViewMode: Boolean = false,
    // 보기 모드 시간 표현
    val timeMillis: Long? = null,
    val isUpdated: Boolean = false,
    val hasSaveError: Boolean = false,
    val isDeleting: Boolean = false,
    val hasDeleteError: Boolean = false,
) {
    val isSaveEnabled: Boolean
        get() = !isSaving &&
                (title.isNotBlank() || content.isNotBlank())

    val contentLength: Int
        get() = content.length
}
