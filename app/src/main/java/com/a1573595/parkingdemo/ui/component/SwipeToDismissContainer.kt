package com.a1573595.parkingdemo.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.a1573595.parkingdemo.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissContainer(
    onDelete: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { newState ->
            newState == SwipeToDismissBoxValue.EndToStart
        },
    )

    val icon: ImageVector
    val alignment: Alignment
    val color: Color

    when (swipeState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> {
            icon = Icons.Outlined.Delete
            alignment = Alignment.CenterEnd
            color = Color.Red
        }

        SwipeToDismissBoxValue.StartToEnd -> {
            icon = Icons.Outlined.Edit
            alignment = Alignment.CenterStart
            color = Color.Green.copy(alpha = 0.3f)
        }

        SwipeToDismissBoxValue.Settled -> {
            icon = Icons.Outlined.Delete
            alignment = Alignment.CenterEnd
//            color = MaterialTheme.colorScheme.errorContainer
            color = Color.Transparent
        }
    }

    when (swipeState.currentValue) {
        SwipeToDismissBoxValue.EndToStart -> {
            onDelete()
        }

        SwipeToDismissBoxValue.StartToEnd -> {
//            LaunchedEffect(swipeState) {
//                onEdit()
//                swipeState.snapTo(SwipeToDismissBoxValue.Settled)
//            }
        }

        SwipeToDismissBoxValue.Settled -> {}
    }

    SwipeToDismissBox(
        state = swipeState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        modifier = Modifier.animateContentSize(),
        backgroundContent = {
            Box(
                modifier = Modifier
                    .padding(Dimens.dp8)
                    .fillMaxSize()
                    .background(color),
                contentAlignment = alignment,
            ) {
                Icon(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .size(Dimens.dp32),
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        },
        content = content,
    )
}