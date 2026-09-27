package com.example.kmp_memo.di

import com.example.kmp_memo.data.database.DatabaseDriverFactory
import org.koin.core.context.startKoin

fun initKoin(
    driverFactory: DatabaseDriverFactory,
) {
    startKoin {
        modules(
            databaseModule(driverFactory),
            memoModule,
        )
    }
}