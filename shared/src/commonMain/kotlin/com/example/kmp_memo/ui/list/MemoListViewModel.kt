package com.example.kmp_memo.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmp_memo.data.repository.MemoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MemoListViewModel(private val memoRepository: MemoRepository) : ViewModel() {

    val memoListUiState: StateFlow<MemoListUiState> = memoRepository.observeMemos()
        .map { memos -> MemoListUiState(memos) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MemoListUiState()
        )


}