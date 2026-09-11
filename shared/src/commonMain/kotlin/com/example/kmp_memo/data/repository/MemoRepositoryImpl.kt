package com.example.kmp_memo.data.repository

import com.example.kmp_memo.data.model.Memo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MemoRepositoryImpl : MemoRepository {
    private val memos = MutableStateFlow<List<Memo>>(emptyList())
    override fun observeMemos() = memos.asStateFlow()
}
