package com.example.kmp_memo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform