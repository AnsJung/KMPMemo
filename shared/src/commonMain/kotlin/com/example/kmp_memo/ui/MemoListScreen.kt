package com.example.kmp_memo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kmp_memo.data.model.Memo
import com.example.kmp_memo.ui.theme.MemoTheme
import kmpmemo.shared.generated.resources.Res
import kmpmemo.shared.generated.resources.ic_note
import kmpmemo.shared.generated.resources.ic_search
import kmpmemo.shared.generated.resources.memo_list_count
import kmpmemo.shared.generated.resources.memo_list_empty_memo_1
import kmpmemo.shared.generated.resources.memo_list_empty_memo_2
import kmpmemo.shared.generated.resources.memo_list_search_content_description
import kmpmemo.shared.generated.resources.memo_list_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MemoListScreen(
    uiState: MemoListUiState,
    onMemoClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 40.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.memo_list_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.weight(1f))
            if (uiState.memos.isNotEmpty()) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(40.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_search),
                        contentDescription = stringResource(
                            Res.string.memo_list_search_content_description,
                        ),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        if (uiState.memos.isNotEmpty()) {
            Text(
                text = stringResource(Res.string.memo_list_count, uiState.memos.size),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 24.dp, top = 16.dp),
            )
        }

        if (uiState.memos.isEmpty()) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_note),
                        contentDescription = null,
                        modifier = Modifier
                            .size(38.dp)
                            .align(Alignment.Center),
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(Res.string.memo_list_empty_memo_1),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(Res.string.memo_list_empty_memo_2),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = 24.dp,
                    vertical = 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(uiState.memos.size) { index ->
                    val memo = uiState.memos[index]
                    MemoListItem(
                        memo = memo,
                        onClick = { onMemoClick(memo.id) }
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun EmptyMemoListPreview() {
    MemoTheme {
        MemoListScreen(
            uiState = MemoListUiState(memos = emptyList()),
            onMemoClick = {},
            onSearchClick = {},
            modifier = Modifier,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MemoListPreview() {
    MemoTheme {
        MemoListScreen(
            uiState = MemoListUiState(
                memos = listOf(
                    Memo(
                        id = 0,
                        title = "Koin 필수 개념",
                        content = "의존성 주입은 객체가 필요한 의존성을 외부에서 전달받는 방식이다.",
                        createdAt = 1_788_924_600_000L,
                        updatedAt = 1_789_010_520_000L
                    )
                )
            ),
            onMemoClick = {},
            onSearchClick = {},
            modifier = Modifier,
        )
    }
}
