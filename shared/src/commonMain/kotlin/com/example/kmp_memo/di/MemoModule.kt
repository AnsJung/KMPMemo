package com.example.kmp_memo.di

import com.example.kmp_memo.data.repository.MemoRepository
import com.example.kmp_memo.data.repository.MemoRepositoryImpl
import com.example.kmp_memo.ui.list.MemoListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val memoModule = module {
    single<MemoRepository> {
        MemoRepositoryImpl()
    }
    viewModel {
        MemoListViewModel(get())
    }
}
