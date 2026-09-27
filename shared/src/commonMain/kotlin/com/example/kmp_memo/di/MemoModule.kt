package com.example.kmp_memo.di

import com.example.kmp_memo.data.repository.MemoRepository
import com.example.kmp_memo.data.repository.SqlDelightMemoRepository
import com.example.kmp_memo.ui.editor.MemoEditorViewModel
import com.example.kmp_memo.ui.list.MemoListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * 메모 저장소와 메모 화면에서 사용하는 ViewModel들을 Koin에 등록한다.
 */
val memoModule = module {
    single<MemoRepository> {
        SqlDelightMemoRepository(
            database = get(),
            ioDispatcher = get(),
        )
    }
    viewModel {
        MemoListViewModel(get())
    }

    viewModel {
        MemoEditorViewModel(get())
    }
}
