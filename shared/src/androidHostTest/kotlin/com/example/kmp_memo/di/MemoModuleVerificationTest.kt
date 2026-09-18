package com.example.kmp_memo.di

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
        // 객체를 실제 화면에서 요청하기 전에 누락된 Koin 정의가 없는지 검사한다.
        // MemoListViewModel이 요구하는 MemoRepository 등록을 제거하면 이 테스트가 실패한다.
        memoModule.verify()
    }
}
