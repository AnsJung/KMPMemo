package com.example.kmp_memo.data.repository

import com.example.kmp_memo.data.model.Memo
import kotlinx.coroutines.flow.Flow

/**
 * 현재 저장된 메모들을 관찰하는 저장소
 */
interface MemoRepository {

    fun observeMemos() : Flow<List<Memo>>
}
