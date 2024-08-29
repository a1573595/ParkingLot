package com.a1573595.parkingdemo.ui.screen

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a1573595.parkingdemo.R
import com.a1573595.parkingdemo.ui.component.ErrorBody
import com.a1573595.parkingdemo.ui.component.LoadingBody
import com.a1573595.parkingdemo.ui.theme.Dimens

@Composable
fun HomeScreen(
    onMapClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onHistoryClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.value

    Scaffold(
        topBar = {
            HomeAppBar(
                isRefreshAble = uiState.isSuccess,
                onRefreshClick = {
                    viewModel.refreshData()
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = Dimens.dp16)
        ) {
            when {
                uiState.isLoading -> LoadingBody()
                uiState.isError -> ErrorBody(throwable = uiState.requireError)
                else -> HomeBody(
                    uiState = uiState.requireValue,
                    onMapClick = onMapClick,
                    onSearchClick = onSearchClick,
                    onFavoriteClick = onFavoriteClick,
                    onHistoryClick = onHistoryClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    isRefreshAble: Boolean,
    onRefreshClick: () -> Unit,
) {
    TopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        title = {
            Text(
                stringResource(R.string.parking_lot),
                style = MaterialTheme.typography.headlineMedium,
            )
        },
        actions = {
            if (isRefreshAble) {
                IconButton(onClick = { onRefreshClick() }) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                    )
                }
            }
        },
    )
}

@Composable
fun HomeBody(
    uiState: HomeUiState,
    onMapClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onHistoryClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.dp16)
    ) {
        Text(
            text = stringResource(
                R.string.total_of_parking_lots_download_at,
                uiState.numberOfParkingLots,
                uiState.lastUpTime
            ),
            style = MaterialTheme.typography.titleLarge,
        )
        Image(
            painterResource(R.mipmap.ic_launcher_foreground),
            contentDescription = "",
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        ImageElevatedButton(
            onClick = onMapClick,
            backgroundColor = android.R.color.holo_green_light,
            drawableId = R.drawable.ic_map,
            title = stringResource(R.string.map)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.dp16),
        )
        ImageElevatedButton(
            onClick = onSearchClick,
            backgroundColor = android.R.color.holo_blue_light,
            drawableId = R.drawable.ic_list,
            title = stringResource(R.string.search)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.dp16),
        )
        ImageElevatedButton(
            onClick = onFavoriteClick,
            backgroundColor = android.R.color.holo_red_light,
            drawableId = R.drawable.ic_favorite,
            title = stringResource(R.string.favorite)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.dp16),
        )
        ImageElevatedButton(
            onClick = onHistoryClick,
            backgroundColor = android.R.color.holo_orange_light,
            drawableId = R.drawable.ic_history,
            title = stringResource(R.string.history)
        )
    }
}

@Composable
fun ImageElevatedButton(
    onClick: () -> Unit,
    @ColorRes backgroundColor: Int,
    @DrawableRes drawableId: Int,
    title: String,
) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Dimens.dp4,
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = backgroundColor),
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Image(
                modifier = Modifier.height(32.dp),
                painter = painterResource(id = drawableId),
                contentDescription = title,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageElevatedButtonPreview() {
    ImageElevatedButton(
        onClick = { },
        backgroundColor = android.R.color.holo_green_light,
        drawableId = R.drawable.ic_map,
        title = "Map Mode",
    )
}