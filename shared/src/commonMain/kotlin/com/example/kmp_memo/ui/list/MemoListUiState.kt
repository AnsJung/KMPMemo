package com.example.kmp_memo.ui.list

import com.example.kmp_memo.data.model.Memo

/**
 * 동시에 존재할 수 없는 메모 목록 화면 상태를 표현한다.
 */
sealed interface MemoListUiState {

    data object Loading : MemoListUiState

    data object Empty : MemoListUiState

    data class Content(
        val memos: List<Memo>,
    ) : MemoListUiState
}
