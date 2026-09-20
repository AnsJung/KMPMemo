package com.example.kmp_memo.ui.main

import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kmp_memo.ui.editor.MemoEditorScreen
import com.example.kmp_memo.ui.list.MemoListScreen
import com.example.kmp_memo.ui.theme.MemoTheme

@Composable
fun MemoMainScreen() {
    var selectedDestination by remember {
        mutableStateOf<MemoDestination>(MemoDestination.Home)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            when (val current = selectedDestination) { // TODO: selectedDestination를 current로 바꾸는 이유와 memoId 캐스팅 확인
                MemoDestination.Home -> {
                    MemoListScreen(
                        onMemoClick = { memoId ->
                            selectedDestination = MemoDestination.Write(memoId)
                        },
                        onSearchClick = { /* Handle search click */ }
                    )
                }

                is MemoDestination.Write -> {
                    MemoEditorScreen(
                        currentId = current.memoId,
                        onSaved = {
                            selectedDestination = MemoDestination.Home
                        },
                        onDeleted = {
                            selectedDestination = MemoDestination.Home
                        },
                        modifier = Modifier.padding(bottom = 104.dp),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(26.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MemoFloatingToolBar(
                    selectedDestination = selectedDestination,
                    onDestinationClick = { destination ->
                        selectedDestination = destination
                    }
                )
            }
        }
    }
}
