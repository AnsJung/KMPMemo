package com.example.kmp_memo.data.repository

import com.example.kmp_memo.data.model.Memo
import kotlinx.coroutines.flow.Flow

/**
 * 현재 저장된 메모들을 관찰하는 저장소
 */
interface MemoRepository {

    fun observeMemos() : Flow<List<Memo>>

    suspend fun getMemo(id: Long): Memo?

    suspend fun createMemo(
        title: String,
        content: String,
    ): Long

    suspend fun updateMemo(
        id: Long,
        title: String,
        content: String,
    )

    suspend fun deleteMemo(id: Long)
}
