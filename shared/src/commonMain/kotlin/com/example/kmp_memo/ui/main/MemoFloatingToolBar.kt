package com.example.kmp_memo.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kmp_memo.ui.theme.MemoTheme
import kmpmemo.shared.generated.resources.Res
import kmpmemo.shared.generated.resources.ic_home
import kmpmemo.shared.generated.resources.ic_note
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private fun MemoDestination.iconRes(): DrawableResource {
    return when (this) {
        MemoDestination.Home -> Res.drawable.ic_home
        is MemoDestination.Write -> Res.drawable.ic_note
    }
}

@Composable
fun MemoFloatingToolBar(
    selectedDestination: MemoDestination = MemoDestination.Home,
    onDestinationClick: (MemoDestination) -> Unit,
) {
    val toolbarDestinations = listOf(
        MemoDestination.Home,
        MemoDestination.Write()
    )

    Surface(
        shape = RoundedCornerShape(percent = 50),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            toolbarDestinations.forEach { destination ->
                MemoToolbarItem(
                    onClick = {
                        if (destination is MemoDestination.Write) {
                            onDestinationClick(MemoDestination.Write(memoId = null))
                        }else{
                            onDestinationClick(destination)
                        }
                    },
                    selected = when (destination) {
                        MemoDestination.Home ->
                            selectedDestination is MemoDestination.Home

                        is MemoDestination.Write ->
                            selectedDestination is MemoDestination.Write
                    },
                    iconRes = destination.iconRes(),
                    contentDescription = when (destination) {
                        MemoDestination.Home -> "Home"
                        is MemoDestination.Write -> "Write"
                    },
                )
            }
        }
    }
}

@Composable
fun MemoToolbarItem(
    selected: Boolean,
    iconRes: DrawableResource,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(percent = 50))
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            )
            .selectable(
                selected = selected,
                onClick = onClick,
            )
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = if (selected) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewMemoFloatingToolBarSelectedHome() {
    MemoTheme {
        MemoFloatingToolBar(
            onDestinationClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMemoFloatingToolBarSelectedWrite() {
    MemoTheme {
        MemoFloatingToolBar(
            selectedDestination = MemoDestination.Write(),
            onDestinationClick = {},
        )
    }
}

@Preview(
    name = " InteractiveMode Floating toolbar",
    showBackground = true,
)
@Composable
private fun MemoFloatingToolBarPreview() {
    var selectedDestination by remember {
        mutableStateOf<MemoDestination>(MemoDestination.Home)
    }
    MemoTheme {
        Box(
            modifier = Modifier.padding(24.dp),
        ) {
            MemoFloatingToolBar(
                selectedDestination = selectedDestination,
                onDestinationClick = { destination ->
                    selectedDestination = destination
                },
            )
        }
    }
}
