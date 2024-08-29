package com.a1573595.parkingdemo.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.a1573595.parkingdemo.R
import com.a1573595.parkingdemo.ui.theme.Dimens

@Composable
fun NoMoreFooter() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.dp16),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_more),
            modifier = Modifier.align(Alignment.Center),
        )
    }
}