package com.example.kmp_memo.ui.list

import com.example.kmp_memo.data.model.Memo

/**
 * 메모 목록을 불러오는 과정과 조회 결과에 따른 화면 상태를 나타낸다.
 */
sealed interface MemoListUiState {

    /** 저장소에서 첫 번째 메모 목록을 불러오는 중인 상태다. */
    data object Loading : MemoListUiState

    /** 목록을 불러왔지만 저장된 메모가 없는 상태다. */
    data object Empty : MemoListUiState

    /** 저장된 메모가 있어 목록을 표시할 수 있는 상태다. */
    data class Content(

        /** 화면에 표시할 메모 목록이다. */
        val memos: List<Memo>,
    ) : MemoListUiState
}
