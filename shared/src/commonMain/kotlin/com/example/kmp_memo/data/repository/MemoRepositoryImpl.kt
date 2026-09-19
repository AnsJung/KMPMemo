package com.example.kmp_memo.data.repository

import com.example.kmp_memo.data.model.Memo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Clock

class MemoRepositoryImpl(
    private val currentTimeMillis: () -> Long = {
        Clock.System.now()
            .toEpochMilliseconds()
    },
) : MemoRepository {

    private val memos = MutableStateFlow<List<Memo>>(emptyList())
    private var nextId = 1L

    override fun observeMemos() = memos.asStateFlow()

    override suspend fun getMemo(id: Long): Memo? {
        return memos.value.find { memo -> memo.id == id }
    }

    override suspend fun createMemo(title: String, content: String): Long {
        val newMemo = Memo(
            id = nextId++,
            title = title,
            content = content,
            createdAt = currentTimeMillis(),
            updatedAt = null
        )
        memos.update { currentMemos ->
            (currentMemos + newMemo)
                .sortedByDescending { memo -> memo.time }
        }
        return newMemo.id
    }

    override suspend fun updateMemo(id: Long, title: String, content: String) {
        val memo = memos.value.find { it.id == id } ?: return
        val updatedMemo = memo.copy(
            title = title,
            content = content,
            updatedAt = currentTimeMillis()
        )
        memos.update { currentMemos ->
            currentMemos
                .map { memo ->
                    if (memo.id == id) updatedMemo else memo
                }
                .sortedByDescending { memo -> memo.time }
        }
    }

    override suspend fun deleteMemo(id: Long) {
        memos.update { currentMemos ->
            currentMemos.filterNot { memo -> memo.id == id }
        }
    }
}
