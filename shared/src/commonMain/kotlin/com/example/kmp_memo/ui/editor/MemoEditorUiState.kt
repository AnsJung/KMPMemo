package com.example.kmp_memo.ui.editor

/**
 * 메모 저장 또는 삭제가 완료된 결과를 화면에 전달한다.
 */
sealed interface MemoEditorResult {

    /** 메모 저장이 완료된 상태다. */
    data object Saved : MemoEditorResult

    /** 메모 삭제가 완료된 상태다. */
    data object Deleted : MemoEditorResult
}

/**
 * 메모 작성, 보기, 저장 및 삭제 화면에 필요한 상태를 관리한다.
 */
data class MemoEditorUiState(

    /** 수정 중인 메모의 ID이며, 새 메모를 작성할 때는 null이다. */
    val currentId: Long? = null,

    /** 사용자가 입력했거나 저장소에서 불러온 메모 제목이다. */
    val title: String = "",

    /** 사용자가 입력했거나 저장소에서 불러온 메모 본문이다. */
    val content: String = "",

    /** 메모 저장 작업이 진행 중인지 나타낸다. */
    val isSaving: Boolean = false,

    /** 저장 또는 삭제 완료 후 화면에서 처리할 결과다. */
    val result: MemoEditorResult? = null,

    /** true이면 메모 보기 화면을, false이면 작성 또는 수정 화면을 표시한다. */
    val isViewMode: Boolean = false,

    /** 보기 화면에 표시할 메모의 생성 또는 수정 시각이다. */
    val timeMillis: Long? = null,

    /** 표시할 시각이 생성 시각이 아닌 수정 시각인지 나타낸다. */
    val isUpdated: Boolean = false,

    /** 메모 저장에 실패해 오류 안내가 필요한지 나타낸다. */
    val hasSaveError: Boolean = false,

    /** 메모 삭제 작업이 진행 중인지 나타낸다. */
    val isDeleting: Boolean = false,

    /** 메모 삭제에 실패해 오류 안내가 필요한지 나타낸다. */
    val hasDeleteError: Boolean = false,
) {

    /** 저장 중이 아니며 제목이나 본문이 입력되어 있을 때 true다. */
    val isSaveEnabled: Boolean
        get() = !isSaving &&
                (title.isNotBlank() || content.isNotBlank())

    /** 현재 메모 본문의 글자 수다. */
    val contentLength: Int
        get() = content.length
}
