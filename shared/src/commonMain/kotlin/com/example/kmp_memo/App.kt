package com.example.kmp_memo

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.kmp_memo.ui.main.MemoMainScreen
import com.example.kmp_memo.ui.theme.MemoTheme

@Composable
@Preview
fun App() {
    MemoTheme {
        MemoMainScreen()
    }
}
