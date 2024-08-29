package com.a1573595.parkingdemo.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.a1573595.parkingdemo.domain.model.ParkingLot
import com.a1573595.parkingdemo.ui.theme.Dimens

@Composable
fun ParkingLotLazyColumn(
    lazyListState: LazyListState,
    parkingLotList: LazyPagingItems<ParkingLot>,
    onParkingLotItemClick: (String) -> Unit,
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = Dimens.dp16)
            .padding(horizontal = Dimens.dp16),
    ) {
        items(
            count = parkingLotList.itemCount,
            key = parkingLotList.itemKey { it.id }
        ) { index ->
            parkingLotList[index]?.let {
                ParkingLotItem(
                    it,
                    onClick = onParkingLotItemClick,
                )
            }
        }
        parkingLotList.loadState.apply {
            when {
                append is LoadState.Loading -> item { LoadMoreFooter() }
                refresh is LoadState.NotLoading && append is LoadState.NotLoading -> item { NoMoreFooter() }
            }
        }
    }
}

@Composable
fun ParkingLotLazyColumn(
    lazyListState: LazyListState,
    parkingLotList: List<ParkingLot>,
    onDelete: (String) -> Unit,
    onClick: (String) -> Unit,
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.dp16),
    ) {
        items(
            count = parkingLotList.size,
            key = { index -> parkingLotList[index].id }
        ) { index ->
            SwipeToDismissContainer(
                onDelete = { onDelete(parkingLotList[index].id) },
            ) {
                ParkingLotItem(
                    parkingLotList[index],
                    onClick = onClick,
                )
            }
        }
    }
}