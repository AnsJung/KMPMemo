package com.example.kmp_memo.di

import com.example.kmp_memo.data.database.DatabaseDriverFactory
import org.koin.core.context.startKoin

/**
 * 플랫폼에서 전달받은 데이터베이스 드라이버 팩토리와 앱 모듈로 Koin을 시작한다.
 */
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
