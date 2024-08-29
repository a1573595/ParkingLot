package com.a1573595.parkingdemo.ui.screen.favorite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.a1573595.parkingdemo.R
import com.a1573595.parkingdemo.ui.component.NavigationAppBar
import com.a1573595.parkingdemo.ui.component.ParkingLotLazyColumn
import com.a1573595.parkingdemo.ui.theme.Dimens

@Composable
fun FavoriteScreen(
    onBackClick: () -> Unit,
    onParkingLotItemClick: (String) -> Unit,
    viewModel: FavoriteViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = {
            NavigationAppBar(
                onBackClick = onBackClick,
                title = stringResource(id = R.string.favorite),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(vertical = Dimens.dp16)
        ) {
            val scrollToTop = remember { mutableStateOf(false) }
            val lazyListState: LazyListState = rememberLazyListState()

            LaunchedEffect(key1 = scrollToTop.value) {
                if (scrollToTop.value) {
                    scrollToTop.value = false
                    lazyListState.scrollToItem(0)
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val parkingLotList = viewModel.parkingLotListFlow.collectAsState(initial = emptyList())
                ParkingLotLazyColumn(
                    lazyListState, parkingLotList.value,
                    onDelete = { viewModel.deleteById(it) },
                    onClick = onParkingLotItemClick,
                )
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(Dimens.dp32),
                    onClick = {
                        scrollToTop.value = true
                    },
                ) {
                    Icon(
                        Icons.Filled.VerticalAlignTop,
                        modifier = Modifier.size(Dimens.dp32),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}