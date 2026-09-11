package com.example.kmp_memo.data.model

data class Memo(
    val id : Long,
    val title : String,
    val content : String,
    val createdAt : Long, //ms
    val updatedAt : Long //ms
)
