package com.a1573595.parkingdemo.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.a1573595.parkingdemo.ui.theme.Dimens

@Composable
fun BorderCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        border = BorderStroke(
            Dimens.dp1,
            Color.Gray,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        content = content,
    )
}