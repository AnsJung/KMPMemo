package com.example.kmp_memo.data.database

import app.cash.sqldelight.db.SqlDriver

internal const val DATABASE_NAME = "memo.db"

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}