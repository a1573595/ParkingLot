package com.a1573595.parkingdemo.ui.screen.search

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.a1573595.parkingdemo.R
import com.a1573595.parkingdemo.ui.component.ErrorBody
import com.a1573595.parkingdemo.ui.component.LoadingBody
import com.a1573595.parkingdemo.ui.component.NavigationAppBar
import com.a1573595.parkingdemo.ui.component.ParkingLotLazyColumn
import com.a1573595.parkingdemo.ui.theme.Dimens

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onParkingLotItemClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = {
            NavigationAppBar(
                onBackClick = onBackClick,
                title = stringResource(id = R.string.search),
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

            val uiState = viewModel.uiState.value

            LaunchedEffect(key1 = scrollToTop.value) {
                if (scrollToTop.value) {
                    scrollToTop.value = false
                    lazyListState.scrollToItem(0)
                }
            }

            TextFieldSearchBar(
                backgroundColor = Color.Transparent,
                value = uiState.keyword,
                onClear = {
                    viewModel.updateKeyword("")
                },
                onValueChange = {
                    viewModel.updateKeyword(it)
                },
            )
            Row(modifier = Modifier.padding(horizontal = Dimens.dp16)) {
                FilterChip(
                    selected = uiState.hasBus,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.updateFilter(FilterType.BUS) },
                    label = {
                        Text(
                            text = stringResource(id = R.string.bus),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                )
                Spacer(modifier = Modifier.width(Dimens.dp16))
                FilterChip(
                    selected = uiState.hasCar,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.updateFilter(FilterType.CAR) },
                    label = {
                        Text(
                            text = stringResource(id = R.string.car),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                )
                Spacer(modifier = Modifier.width(Dimens.dp16))
                FilterChip(
                    selected = uiState.hasMotor,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.updateFilter(FilterType.MOTOR) },
                    label = {
                        Text(
                            text = stringResource(id = R.string.motor),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                )
                Spacer(modifier = Modifier.width(Dimens.dp16))
                FilterChip(
                    selected = uiState.hasBike,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.updateFilter(FilterType.BIKE) },
                    label = {
                        Text(
                            text = stringResource(id = R.string.bike),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                )
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                uiState.parkingLotPagingDataFlow.collectAsLazyPagingItems().let {
                    with(it.loadState.refresh) {
                        if (this is LoadState.Loading) {
                            LoadingBody()
                        } else if (this is LoadState.Error) {
                            ErrorBody(this.error)
                        }
                    }
                    ParkingLotLazyColumn(lazyListState, it, onParkingLotItemClick)
                }
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

@Composable
fun TextFieldSearchBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    value: String,
    onClear: () -> Unit,
    onValueChange: (String) -> Unit,
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    TextField(
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.dp16)
            .border(
                width = Dimens.dp1,
                color = Color.Black,
                shape = MaterialTheme.shapes.medium,
            ),
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            disabledIndicatorColor = backgroundColor,
            errorIndicatorColor = backgroundColor,
            focusedIndicatorColor = backgroundColor,
            unfocusedIndicatorColor = backgroundColor,
        ),
        singleLine = true,
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                modifier = Modifier.size(Dimens.dp32),
                contentDescription = null,
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                Icon(
                    Icons.Filled.Clear,
                    modifier = Modifier
                        .size(Dimens.dp32)
                        .clickable { onClear() },
                    contentDescription = null,
                )
            }
        },
        placeholder = {
            Text(
                text = stringResource(R.string.keyword),
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        value = value,
        onValueChange = onValueChange,
    )
}