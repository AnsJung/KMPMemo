package com.example.kmp_memo

import android.app.Application
import com.example.kmp_memo.di.initKoin

class MemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}