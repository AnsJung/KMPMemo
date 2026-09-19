package com.example.kmp_memo.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 메모리 저장소가 MemoRepository의 CRUD 규칙을 지키는지 검증한다.
 * 테스트가 실행되는 실제 시각에 영향을 받지 않도록 제어 가능한 시각을 전달한다.
 */
class MemoRepositoryImplTest {

    @Test
    fun 새_저장소는_빈_메모_목록을_제공한다() = runTest {
        // 구현체를 인터페이스 타입으로 사용해 Repository 계약을 기준으로 검증한다.
        val repository: MemoRepository = MemoRepositoryImpl()

        // StateFlow가 최초에 제공하는 목록을 한 번만 가져온다.
        val actual = repository.observeMemos().first()

        assertTrue(actual.isEmpty(), "새 저장소는 빈 메모 목록을 제공해야 한다.")
    }

    @Test
    fun 메모를_생성하면_ID와_생성_시간을_저장한다() = runTest {
        // 고정된 시각을 반환하게 해 createdAt을 정확한 값으로 검증한다.
        val repository: MemoRepository = MemoRepositoryImpl(
            currentTimeMillis = { 1_000L },
        )

        // ID는 화면이 만들지 않고 저장소가 생성해 반환한다.
        val id = repository.createMemo(
            title = "첫 메모",
            content = "첫 본문",
        )

        // 생성 직후 반환받은 ID로 같은 메모를 조회할 수 있어야 한다.
        val memo = assertNotNull(repository.getMemo(id))
        assertEquals(1L, id)
        assertEquals("첫 메모", memo.title)
        assertEquals("첫 본문", memo.content)
        assertEquals(1_000L, memo.createdAt)

        // 아직 수정되지 않았으므로 수정 시각은 없어야 한다.
        assertNull(memo.updatedAt)
    }

    @Test
    fun 새로_생성한_메모가_목록의_앞에_배치된다() = runTest {
        // 람다가 바라보는 값을 바꾸면 생성 호출마다 서로 다른 시각을 만들 수 있다.
        var currentTime = 1_000L
        val repository: MemoRepository = MemoRepositoryImpl(
            currentTimeMillis = { currentTime },
        )

        val firstId = repository.createMemo("첫 메모", "첫 본문")
        currentTime = 2_000L
        val secondId = repository.createMemo("두 번째 메모", "두 번째 본문")

        // time 내림차순이므로 나중에 생성된 메모가 첫 번째에 있어야 한다.
        val ids = repository.observeMemos().first().map { memo -> memo.id }
        assertEquals(listOf(secondId, firstId), ids)
    }

    @Test
    fun 메모를_수정하면_생성_시간은_유지하고_수정_시간을_기록한다() = runTest {
        var currentTime = 1_000L
        val repository: MemoRepository = MemoRepositoryImpl(
            currentTimeMillis = { currentTime },
        )

        val firstId = repository.createMemo("첫 메모", "첫 본문")
        currentTime = 2_000L
        val secondId = repository.createMemo("두 번째 메모", "두 번째 본문")

        // 첫 메모를 가장 최근 시각에 수정한다.
        currentTime = 3_000L
        repository.updateMemo(
            id = firstId,
            title = "수정된 메모",
            content = "수정된 본문",
        )

        val updatedMemo = assertNotNull(repository.getMemo(firstId))
        assertEquals("수정된 메모", updatedMemo.title)
        assertEquals("수정된 본문", updatedMemo.content)
        assertEquals(1_000L, updatedMemo.createdAt)
        assertEquals(3_000L, updatedMemo.updatedAt)

        // 수정 시각이 가장 최근이므로 첫 메모가 목록의 앞으로 이동해야 한다.
        val ids = repository.observeMemos().first().map { memo -> memo.id }
        assertEquals(listOf(firstId, secondId), ids)
    }

    @Test
    fun 메모를_삭제하면_단일_조회와_목록에서_제거된다() = runTest {
        val repository: MemoRepository = MemoRepositoryImpl(
            currentTimeMillis = { 1_000L },
        )
        val id = repository.createMemo("삭제할 메모", "삭제할 본문")

        repository.deleteMemo(id)

        // 같은 ID를 더 이상 조회할 수 없어야 한다.
        assertNull(repository.getMemo(id))

        // 전체 목록에서도 삭제된 메모가 남아 있지 않아야 한다.
        assertTrue(repository.observeMemos().first().isEmpty())
    }

    @Test
    fun 존재하지_않는_ID를_수정하거나_삭제해도_목록은_변경되지_않는다() = runTest {
        val repository: MemoRepository = MemoRepositoryImpl(
            currentTimeMillis = { 1_000L },
        )
        repository.createMemo("유지할 메모", "유지할 본문")

        // 현재 목록을 저장해 잘못된 ID 작업 전후를 비교한다.
        val before = repository.observeMemos().first()

        repository.updateMemo(999L, "잘못된 수정", "잘못된 본문")
        repository.deleteMemo(999L)

        val after = repository.observeMemos().first()
        assertEquals(before, after)
    }
}
