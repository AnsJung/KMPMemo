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
            when (val current = selectedDestination) {
                MemoDestination.Home -> {
                    MemoListScreen(
                        onMemoClick = { memoId ->
                            selectedDestination = MemoDestination.Write(memoId)
                        },
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
                    )
                }
            }

            MemoFloatingToolBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(26.dp),
                selectedDestination = selectedDestination,
                onDestinationClick = { destination ->
                    selectedDestination = destination
                }
            )
        }
    }
}

