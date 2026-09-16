package com.example.kmp_memo.ui.formatter

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

private val memoDateTimeFormat = LocalDateTime.Format {
    year()
    char('.')
    monthNumber()
    char('.')
    day()
    char(' ')
    hour()
    char(':')
    minute()
}

/** epoch milliseconds를 기기의 현재 시간대에 맞는 목록 표시 형식으로 변환한다. */
internal fun Long.toMemoDateTimeText(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String = memoDateTimeFormat.format(
    Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone),
)
