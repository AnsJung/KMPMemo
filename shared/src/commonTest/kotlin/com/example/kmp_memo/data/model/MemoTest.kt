package com.example.kmp_memo.data.model

import kotlin.test.Test
import kotlin.test.assertEquals

class MemoTest {
    @Test
    fun 수정_시간이_없으면_time은_생성_시간이다() {
        val memo = Memo(
            id = 1L,
            title = "새 메모",
            content = "본문",
            createdAt = 1_000L,
        )

        assertEquals(1_000L, memo.time)
    }

    @Test
    fun 수정_시간이_있으면_time은_수정_시간이다() {
        val memo = Memo(
            id = 1L,
            title = "수정한 메모",
            content = "본문",
            createdAt = 1_000L,
            updatedAt = 2_000L,
        )

        assertEquals(2_000L, memo.time)
    }
}
