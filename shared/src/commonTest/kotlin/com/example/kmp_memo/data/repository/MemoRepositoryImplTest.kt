package com.example.kmp_memo.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class MemoRepositoryImplTest {
    @Test
    fun 새_저장소는_빈_메모_목록을_제공한다() = runTest {
        val repository: MemoRepository = MemoRepositoryImpl()

        val actual = repository.observeMemos().first()

        assertTrue(actual.isEmpty(), "새 저장소는 빈 메모 목록을 제공해야 한다.")
    }
}
