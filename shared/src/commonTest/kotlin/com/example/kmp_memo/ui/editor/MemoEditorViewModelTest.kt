package com.example.kmp_memo.ui.editor

import androidx.lifecycle.ViewModelStore
import com.example.kmp_memo.data.model.Memo
import com.example.kmp_memo.data.repository.MemoRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 사용자 입력이 Editor 상태에 반영되고 저장 요청으로 변환되는지 검증한다.
 * 실제 저장 기능은 Repository 테스트에서 확인했으므로 여기서는 기록용 가짜 저장소를 사용한다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MemoEditorViewModelTest {

    @Test
    fun 제목과_내용을_입력하면_화면_상태와_파생값이_변경된다() {
        val viewModel = MemoEditorViewModel(RecordingMemoRepository())

        viewModel.onTitleChanged("제목")
        viewModel.onContentChanged("메모 내용")

        val state = viewModel.uiState.value
        assertEquals("제목", state.title)
        assertEquals("메모 내용", state.content)
        assertEquals(5, state.contentLength)
        assertTrue(state.isSaveEnabled)
    }

    @Test
    fun 빈_메모는_저장하지_않는다() {
        val repository = RecordingMemoRepository()
        val viewModel = MemoEditorViewModel(repository)

        viewModel.saveMemo()

        // 저장 불가능 상태에서는 코루틴을 시작하기 전에 반환해야 한다.
        assertEquals(0, repository.createCallCount)
        assertFalse(viewModel.uiState.value.isSaving)
        assertNull(viewModel.uiState.value.savedMemoId)
    }

    @Test
    fun 메모를_저장하면_공백을_정리해_저장소에_전달하고_생성_ID를_상태에_기록한다() = runTest {
        // viewModelScope가 사용하는 Main을 이 테스트의 스케줄러와 연결한다.
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()

        try {
            val repository = RecordingMemoRepository(createdId = 7L)
            val viewModel = MemoEditorViewModel(repository)

            // ViewModelStore가 테스트 종료 시 viewModelScope도 함께 정리하게 한다.
            store.put("memo-editor", viewModel)
            viewModel.onTitleChanged("  저장할 제목  ")
            viewModel.onContentChanged("  저장할 내용  ")

            viewModel.saveMemo()

            // 저장 코루틴이 실행되기 전에도 중복 클릭을 막기 위해 즉시 true가 되어야 한다.
            assertTrue(viewModel.uiState.value.isSaving)

            // 예약된 저장 코루틴을 끝까지 실행한다.
            runCurrent()

            assertEquals(1, repository.createCallCount)
            assertEquals("저장할 제목", repository.createdTitle)
            assertEquals("저장할 내용", repository.createdContent)
            assertFalse(viewModel.uiState.value.isSaving)
            assertEquals(7L, viewModel.uiState.value.savedMemoId)

            // 화면이 저장 결과를 처리하면 savedMemoId만 비운다.
            viewModel.consumeSaveResult()
            val consumedState = viewModel.uiState.value
            assertNull(consumedState.savedMemoId)
            assertEquals("  저장할 제목  ", consumedState.title)
            assertEquals("  저장할 내용  ", consumedState.content)
        } finally {
            // 실패한 테스트도 Main 설정과 ViewModel 작업을 다음 테스트에 남기지 않는다.
            try {
                store.clear()
                runCurrent()
            } finally {
                Dispatchers.resetMain()
            }
        }
    }

    @Test
    fun 기존_메모를_수정하면_공백을_정리해_저장소에_전달한다() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()

        try {
            val existingMemo = Memo(
                id = 3L,
                title = "기존 제목",
                content = "기존 내용",
                createdAt = 1_000L,
            )
            val repository = RecordingMemoRepository(memoToReturn = existingMemo)
            val viewModel = MemoEditorViewModel(repository)
            store.put("memo-editor", viewModel)

            // 실제 화면과 동일하게 기존 메모를 먼저 불러온 후 편집 상태를 만든다.
            viewModel.openMemo(existingMemo.id)
            runCurrent()
            viewModel.setViewMode(isViewMode = false)
            viewModel.onTitleChanged("  수정한 제목  ")
            viewModel.onContentChanged("\n수정한 내용\n")

            viewModel.saveMemo()
            runCurrent()

            // 기존 메모이므로 create가 아니라 update가 같은 ID로 한 번 호출되어야 한다.
            assertEquals(0, repository.createCallCount)
            assertEquals(1, repository.updateCallCount)
            assertEquals(existingMemo.id, repository.updatedId)
            assertEquals("수정한 제목", repository.updatedTitle)
            assertEquals("수정한 내용", repository.updatedContent)
            assertFalse(viewModel.uiState.value.isSaving)
            assertEquals(existingMemo.id, viewModel.uiState.value.savedMemoId)
        } finally {
            try {
                store.clear()
                runCurrent()
            } finally {
                Dispatchers.resetMain()
            }
        }
    }

    @Test
    fun 저장_중에_다시_저장해도_저장소는_한_번만_호출된다() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        val saveGate = CompletableDeferred<Unit>()

        try {
            // saveGate가 완료될 때까지 createMemo가 끝나지 않는 저장소를 사용한다.
            val repository = RecordingMemoRepository(saveGate = saveGate)
            val viewModel = MemoEditorViewModel(repository)
            store.put("memo-editor", viewModel)
            viewModel.onTitleChanged("한 번만 저장")

            viewModel.saveMemo()
            viewModel.saveMemo()
            runCurrent()

            // 첫 호출이 대기 중인 동안 두 번째 호출은 isSaveEnabled 검사에서 차단된다.
            assertEquals(1, repository.createCallCount)
            assertTrue(viewModel.uiState.value.isSaving)

            // 대기 중인 저장을 완료시키고 남은 상태 변경을 실행한다.
            saveGate.complete(Unit)
            runCurrent()
            assertFalse(viewModel.uiState.value.isSaving)
        } finally {
            try {
                // 저장 코루틴이 남아 있어도 테스트 종료 전에 취소하고 정리한다.
                saveGate.complete(Unit)
                store.clear()
                coroutineContext.cancelChildren()
                runCurrent()
            } finally {
                Dispatchers.resetMain()
            }
        }
    }
}

/**
 * 생성과 수정 호출 정보를 기록하는 Editor ViewModel 전용 가짜 저장소다.
 */
private class RecordingMemoRepository(
    private val createdId: Long = 1L,
    private val saveGate: CompletableDeferred<Unit>? = null,
    private val memoToReturn: Memo? = null,
) : MemoRepository {

    var createCallCount: Int = 0
        private set

    var createdTitle: String? = null
        private set

    var createdContent: String? = null
        private set

    var updateCallCount: Int = 0
        private set

    var updatedId: Long? = null
        private set

    var updatedTitle: String? = null
        private set

    var updatedContent: String? = null
        private set

    override fun observeMemos(): Flow<List<Memo>> = flowOf(emptyList())

    override suspend fun getMemo(id: Long): Memo? =
        memoToReturn?.takeIf { memo -> memo.id == id }

    override suspend fun createMemo(title: String, content: String): Long {
        // 호출 정보를 먼저 기록해 저장이 대기 중인 상태에서도 횟수를 검증할 수 있게 한다.
        createCallCount += 1
        createdTitle = title
        createdContent = content

        // 중복 저장 테스트에서만 외부 신호가 올 때까지 저장 완료를 지연한다.
        saveGate?.await()
        return createdId
    }

    override suspend fun updateMemo(id: Long, title: String, content: String) {
        updateCallCount += 1
        updatedId = id
        updatedTitle = title
        updatedContent = content
    }

    override suspend fun deleteMemo(id: Long) {
        error("이 테스트에서는 메모를 삭제하지 않는다.")
    }
}
