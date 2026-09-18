package com.example.kmp_memo.di

import com.example.kmp_memo.data.repository.MemoRepository
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertSame

/**
 * 메모 모듈에 등록한 객체의 생성 범위가 의도대로 동작하는지 검증한다.
 */
class MemoModuleTest {

    @Test
    fun 같은_코인_컨테이너에서_저장소는_하나의_인스턴스를_사용한다() {
        // 전역 startKoin 대신 이 테스트 안에서만 사용하는 독립적인 Koin 컨테이너를 만든다.
        val application = koinApplication {
            modules(memoModule)
        }

        try {
            // MemoRepository 타입으로 첫 번째 저장소 인스턴스를 요청한다.
            val firstRepository = application.koin.get<MemoRepository>()

            // 같은 컨테이너에 동일한 타입을 다시 요청한다.
            val secondRepository = application.koin.get<MemoRepository>()

            // single 정의라면 두 요청은 값만 같은 것이 아니라 같은 객체를 반환해야 한다.
            assertSame(
                expected = firstRepository,
                actual = secondRepository,
                message = "single로 등록한 저장소는 같은 Koin 컨테이너에서 재사용되어야 한다.",
            )
        } finally {
            // 테스트가 실패해도 로컬 컨테이너를 닫아 다음 테스트에 상태를 남기지 않는다.
            application.close()
        }
    }
}
