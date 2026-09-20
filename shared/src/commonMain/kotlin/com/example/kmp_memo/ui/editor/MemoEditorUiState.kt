package com.example.kmp_memo.ui.editor

data class MemoEditorUiState(
    val currentId: Long? = null,
    val title: String = "",
    val content: String = "",
    val isSaving: Boolean = false,
    val savedMemoId: Long? = null,
    val isViewMode: Boolean = false,
    // 보기 모드 시간 표현
    val timeMillis: Long? = null,
    val isUpdated: Boolean = false,
    val hasSaveError: Boolean = false
) {
    val isSaveEnabled: Boolean
        get() = !isSaving &&
                (title.isNotBlank() || content.isNotBlank())

    val contentLength: Int
        get() = content.length
}