package com.example.kmp_memo.ui.main

import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kmp_memo.ui.list.MemoListScreen
import com.example.kmp_memo.ui.theme.MemoTheme

@Composable
fun MemoMainScreen() {
    var selectedDestination by remember { mutableStateOf(MemoDestination.HOME) }

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
            when (selectedDestination) {
                MemoDestination.HOME -> {
                    MemoListScreen(
                        onMemoClick = { /* Handle memo click */ },
                        onSearchClick = { /* Handle search click */ }
                    )
                }

                MemoDestination.WRITE -> {
                    Text("Memo Editor Screen")
//                MemoEditorScreen()
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
