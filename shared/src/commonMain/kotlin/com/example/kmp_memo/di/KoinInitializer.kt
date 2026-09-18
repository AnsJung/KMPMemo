package com.example.kmp_memo.di

import org.koin.core.context.startKoin


fun initKoin() {
    startKoin {
        modules(memoModule)
    }
}