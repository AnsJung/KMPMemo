package com.example.kmp_memo.ui

import androidx.lifecycle.ViewModelStore
import com.example.kmp_memo.data.model.Memo
import com.example.kmp_memo.data.repository.MemoRepository
import com.example.kmp_memo.ui.list.MemoListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 저장소 데이터가 화면 상태로 변환되는지 검증한다.
 * 실제 저장 기능 대신 제어 가능한 가짜 저장소를 사용해 ViewModel에 집중한다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MemoListViewModelTest {
    @Test
    fun 저장소의_초기_목록과_변경된_목록을_화면_상태에_반영한다() = runTest {
        // viewModelScope가 사용하는 Main을 테스트 스케줄러에 연결한다.
        // 에뮬레이터 없이 실행하며, runCurrent로 코루틴 실행 시점을 제어한다.
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            // 비어 있지 않은 목록으로 검증해야 기본 UI 상태만 반환하는 오류를 잡을 수 있다.
            val memo = Memo(1L, "첫 메모", "본문", 1_000L)
            val repository = FakeMemoRepository(listOf(memo))
            val viewModel = MemoListViewModel(repository)

            // 테스트 종료 시 clear()로 ViewModel과 viewModelScope를 함께 정리한다.
            store.put("memo-list", viewModel)

            // WhileSubscribed는 구독자가 있어야 원본 Flow를 수집한다.
            // value를 읽기만 해서는 구독이 시작되지 않는다.
            // backgroundScope의 구독은 runTest 종료 시 자동 취소된다.
            backgroundScope.launch {
                viewModel.memoListUiState.collect { }
            }

            // 현재 시각에 예약된 구독과 상태 변환을 실행한 뒤 최초 목록을 검증한다.
            runCurrent()
            assertEquals(listOf(memo), viewModel.memoListUiState.value.memos)

            // 가짜 저장소의 데이터만 바꾼다. ViewModel의 상태는 직접 수정하지 않는다.
            val editedMemo = memo.copy(title = "수정된 메모", updatedAt = 2_000L)
            repository.replaceMemos(listOf(editedMemo))
            runCurrent()

            // 최초 값뿐 아니라 이후 변경도 계속 반영되는지 확인한다.
            assertEquals(listOf(editedMemo), viewModel.memoListUiState.value.memos)
        } finally {
            // 검증 실패 시에도 실행된다. 다른 테스트에 Main 설정이나 작업을 남기지 않는다.
            try {
                backgroundScope.coroutineContext.cancelChildren()
                store.clear()
                // 취소 처리도 코루틴 작업이다. Main을 복원하기 전에 완료시킨다.
                runCurrent()
            } finally {
                Dispatchers.resetMain()
            }
        }
    }
}

// 테스트에서만 목록을 제어하기 위한 구현체. 실제 메모리 저장소의 기능을 추가하지 않는다.
private class FakeMemoRepository(initialMemos: List<Memo>) : MemoRepository {
    private val memos = MutableStateFlow(initialMemos)

    override fun observeMemos(): Flow<List<Memo>> = memos.asStateFlow()

    fun replaceMemos(newMemos: List<Memo>) {
        memos.value = newMemos
    }
}
