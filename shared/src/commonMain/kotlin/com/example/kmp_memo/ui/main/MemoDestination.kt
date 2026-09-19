package com.example.kmp_memo.ui.main

sealed interface MemoDestination {
    data object Home : MemoDestination

    data class Write(
        val memoId: Long? = null
    ) : MemoDestination
}