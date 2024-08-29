package com.a1573595.parkingdemo.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.a1573595.parkingdemo.ui.theme.Dimens

@Composable
fun LoadMoreFooter() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.dp16),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}