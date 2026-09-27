package com.example.kmp_memo.di

import app.cash.sqldelight.db.SqlDriver
import com.example.kmp_memo.data.database.DatabaseDriverFactory
import com.example.kmp_memo.data.database.MemoDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

/**
 * SQLDelight 데이터베이스 사용에 필요한 객체들을 Koin에 등록한다.
 */
fun databaseModule(
    driverFactory: DatabaseDriverFactory,
) = module {
    single<SqlDriver> {
        driverFactory.createDriver()
    }

    single<MemoDatabase> {
        MemoDatabase(driver = get())
    }

    single<CoroutineDispatcher> {
        Dispatchers.IO
    }
}
