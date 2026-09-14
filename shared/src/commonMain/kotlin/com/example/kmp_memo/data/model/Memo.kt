package com.example.kmp_memo.data.model

data class Memo(
    val id : Long,
    val title : String,
    val content : String,
    val createdAt : Long, //ms
    val updatedAt : Long? = null //ms
) {
    // 수정 이력이 없으면 생성 시각, 있으면 가장 최근 수정 시각을 사용한다.
    val time: Long
        get() = updatedAt ?: createdAt
}
