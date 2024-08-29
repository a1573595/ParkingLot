package com.a1573595.parkingdemo.ui.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.a1573595.parkingdemo.common.AsyncValue
import com.a1573595.parkingdemo.domain.model.ParkingLot
import com.a1573595.parkingdemo.ui.component.BorderCard
import com.a1573595.parkingdemo.ui.component.ErrorBody
import com.a1573595.parkingdemo.ui.component.LoadingBody
import com.a1573595.parkingdemo.ui.theme.Dimens

@Composable
fun DetailScreen(
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.value

    Scaffold(
        topBar = {
            DetailAppBar(
                uiState = uiState,
                onBackClick = onBackClick,
                onFavoriteClick = { viewModel.updateFavorite() },
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> LoadingBody()
                uiState.isError -> ErrorBody(throwable = uiState.requireError)
                else -> DetailBody(uiState.requireValue.parkingLot)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailAppBar(
    uiState: AsyncValue<DetailUiState>,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    TopAppBar(
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        navigationIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "back",
                )
            }
        },
        title = {
            if (uiState.isSuccess)
                Text(
                    text = uiState.requireValue.parkingLot.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
        },
        actions = {
            if (uiState.isSuccess)
                IconButton(onClick = { onFavoriteClick() }) {
                    Icon(
                        imageVector = if (uiState.requireValue.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "favorite",
                        tint = Color.Red,
                    )
                }
        },
    )
}

@Composable
fun DetailBody(parkingLot: ParkingLot) {
    Column(
        modifier = Modifier.padding(Dimens.dp16),
    ) {
        Text(
            text = parkingLot.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = "${parkingLot.area} ${parkingLot.address}",
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(Dimens.dp16))
        BorderCard {
            Column(
                modifier = Modifier.padding(
                    horizontal = Dimens.dp8,
                    vertical = Dimens.dp16,
                ),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    Icon(
                        imageVector = Icons.Filled.DirectionsBus,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.dp48),
                    )
                    Icon(
                        imageVector = Icons.Filled.DirectionsCar,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.dp48),
                    )
                    Icon(
                        imageVector = Icons.Filled.Motorcycle,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.dp48),
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.dp48),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "${parkingLot.totalBus}",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "${parkingLot.totalCar}",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "${parkingLot.totalMotor}",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "${parkingLot.totalBike}",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(Dimens.dp32))
        Row {
            Icon(
                imageVector = Icons.Filled.Phone,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(Dimens.dp16))
            Text(
                text = parkingLot.tel,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(modifier = Modifier.height(Dimens.dp32))
        Row {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(Dimens.dp16))
            Text(
                text = parkingLot.summary,
                style = MaterialTheme.typography.titleSmall,
            )
        }
        Spacer(modifier = Modifier.height(Dimens.dp32))
        Row {
            Icon(
                imageVector = Icons.Filled.Money,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(Dimens.dp16))
            Text(
                text = parkingLot.payEx,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}