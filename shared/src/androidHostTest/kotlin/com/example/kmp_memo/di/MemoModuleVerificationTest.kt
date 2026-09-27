package com.example.kmp_memo.di

import com.example.kmp_memo.data.database.MemoDatabase
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify
import kotlin.test.Test

/**
 * Koin 모듈의 모든 객체가 필요한 의존성을 찾을 수 있는지 검증한다.
 * verify는 JVM 전용이므로 commonTest가 아닌 androidHostTest에서 실행한다.
 */
@OptIn(KoinExperimentalAPI::class)
class MemoModuleVerificationTest {

    @Test
    fun 메모_모듈의_모든_의존성이_연결되어_있다() {
        // databaseModule이 제공하는 타입은 메모 모듈 외부 의존성으로 검증 대상에 알려준다.
        memoModule.verify(
            extraTypes = listOf(
                MemoDatabase::class,
                CoroutineDispatcher::class,
            ),
        )
    }
}
