package com.example.kmp_memo.ui

import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class MemoTimeFormatterTest {
    @Test
    fun 시간을_목록에_사용할_형식으로_변환한다() {
        val time = Instant.parse("2026-09-10T10:42:00Z").toEpochMilliseconds()

        val actual = time.toMemoDateTimeText(TimeZone.UTC)

        assertEquals("2026.09.10 10:42", actual)
    }
}
