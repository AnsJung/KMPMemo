package com.example.kmp_memo.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.kmp_memo.ui.theme.MemoTheme

@Composable
fun MemoDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = null,
    isDestructive: Boolean = false,
    scrimColor: Color =
        MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f),
    supportingContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        MemoDialogOverlay(
            scrimColor = scrimColor,
            onDismiss = onDismiss,
        ) {
            MemoDialogContent(
                title = title,
                message = message,
                confirmText = confirmText,
                onConfirm = onConfirm,
                onDismiss = onDismiss,
                modifier = modifier,
                dismissText = dismissText,
                isDestructive = isDestructive,
                supportingContent = supportingContent,
            )
        }
    }
}

@Composable
private fun MemoDialogContent(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = null,
    isDestructive: Boolean = false,
    supportingContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val confirmColor = if (isDestructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = modifier
            .widthIn(max = 326.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = 12.dp,
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (supportingContent != null) {
                Spacer(modifier = Modifier.height(18.dp))
                supportingContent()
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (dismissText != null) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    ) {
                        Text(
                            text = dismissText,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                TextButton(
                    onClick = onConfirm,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = confirmColor,
                    ),
                ) {
                    Text(
                        text = confirmText,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun MemoDialogOverlay(
    onDismiss: () -> Unit,
    scrimColor: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f),
    content: @Composable () -> Unit,
) {
    val scrimInteractionSource = remember { MutableInteractionSource() }
    val dialogInteractionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(scrimColor)
            .clickable(
                interactionSource = scrimInteractionSource,
                indication = null,
                onClick = onDismiss,
            )
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.clickable(
                interactionSource = dialogInteractionSource,
                indication = null,
                onClick = {},
            ),
        ) {
            content()
        }
    }
}

@Preview(
    name = "삭제 확인 다이얼로그",
    showBackground = true,
    widthDp = 390,
    heightDp = 844,
)
@Composable
private fun DeleteMemoDialogPreview() {
    MemoTheme {
        MemoDialogOverlay(onDismiss = {}) {
            MemoDialogContent(
                title = "메모를 삭제할까요?",
                message = "이 작업은 되돌릴 수 없어요.",
                confirmText = "삭제",
                dismissText = "취소",
                isDestructive = true,
                onConfirm = {},
                onDismiss = {},
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Text(
                        text = "Koin 필수 개념",
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 13.dp,
                        ),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}

@Preview(
    name = "저장 실패 다이얼로그",
    showBackground = true,
    widthDp = 390,
    heightDp = 844,
)
@Composable
private fun SaveErrorDialogPreview() {
    MemoTheme {
        MemoDialogOverlay(onDismiss = {}) {
            MemoDialogContent(
                title = "메모를 저장하지 못했어요",
                message = "잠시 후 다시 시도해 주세요.",
                confirmText = "확인",
                onConfirm = {},
                onDismiss = {},
            )
        }
    }
}

@Preview(
    name = "커스텀 딤 다이얼로그",
    showBackground = true,
    widthDp = 390,
    heightDp = 844,
)
@Composable
private fun CustomScrimDialogPreview() {
    MemoTheme {
        MemoDialogOverlay(
            scrimColor = Color.Black.copy(alpha = 0.7f),
            onDismiss = {},
        ) {
            MemoDialogContent(
                title = "커스텀 딤 영역",
                message = "원하는 색상과 투명도를 적용할 수 있어요.",
                confirmText = "확인",
                onConfirm = {},
                onDismiss = {},
            )
        }
    }
}