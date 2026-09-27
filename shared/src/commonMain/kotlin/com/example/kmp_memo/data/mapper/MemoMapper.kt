package com.example.kmp_memo.data.mapper
import com.example.kmp_memo.data.database.Memo as MemoRow
import com.example.kmp_memo.data.model.Memo

/**
 * SQLDelight가 생성한 데이터베이스 레코드를 앱에서 사용하는 메모 모델로 변환한다.
 *
 * 데이터베이스 모델이 저장소 외부로 노출되지 않도록 저장소 구현에서 사용한다.
 */
internal fun MemoRow.toMemo(): Memo {
    return Memo(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}