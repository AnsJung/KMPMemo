package com.example.kmp_memo.ui.list

import com.example.kmp_memo.data.model.Memo

/**
 * 메모 리스트 화면 UI 상태 저장 클래스
 */
data class MemoListUiState(
    val memos: List<Memo> = emptyList()
)
