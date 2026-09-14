package com.example.kmp_memo.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kmp_memo.data.model.Memo
import com.example.kmp_memo.ui.theme.MemoTheme
import kmpmemo.shared.generated.resources.Res
import kmpmemo.shared.generated.resources.memo_list_created_at
import kmpmemo.shared.generated.resources.memo_list_untitled
import kmpmemo.shared.generated.resources.memo_list_updated_at
import org.jetbrains.compose.resources.stringResource

private val consecutiveWhitespace = Regex("\\s+")

@Composable
fun MemoListItem(
    memo: Memo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val timeLabel = if (memo.updatedAt == null) {
        Res.string.memo_list_created_at
    } else {
        Res.string.memo_list_updated_at
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 124.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
        ) {
            Text(
                text = memo.title.ifBlank { stringResource(Res.string.memo_list_untitled) },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = memo.content.trim()
                    .replace(consecutiveWhitespace, " "),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(timeLabel, memo.time.toMemoDateTimeText()),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun MemoListItemPreview() {
    MemoTheme {

        MemoListItem(
            memo = Memo(
                title = "Koin 필수 개념",
                content = "의존성 주입은 객체가 필요한 의존성을 외부에서 전달받는 방식이다.",
                updatedAt = 1_789_010_520_000L,
                id = 0,
                createdAt = 1_788_924_600_000L,
            ),
            onClick = {}
        )

    }
}
