package com.example.kmp_memo.data.repository

import com.example.kmp_memo.data.model.Memo
import kotlinx.coroutines.flow.Flow

/**
 * 메모 데이터의 관찰과 조회, 생성, 수정, 삭제 기능을 정의하는 인터페이스다.
 * 호출부가 메모리나 SQLDelight 같은 구체적인 저장 방식에 의존하지 않도록 한다.
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
