package com.a1573595.parkingdemo.ui.screen.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a1573595.parkingdemo.R
import com.a1573595.parkingdemo.ui.component.NavigationAppBar
import com.a1573595.parkingdemo.ui.screen.map.bean.ParkingLotCluster
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.clustering.rememberClusterManager
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(
    onBackClick: () -> Unit,
    onParkingLotItemClick: (String) -> Unit,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val uiSettings by remember { mutableStateOf(MapUiSettings()) }
    val cameraPositionState = rememberCameraPositionState {
        val taipeiLatLng = LatLng(
            25.0329694,
            121.56541770000001
        )
        position = CameraPosition.fromLatLngZoom(taipeiLatLng, 15f)
    }

    Scaffold(
        topBar = {
            NavigationAppBar(
                onBackClick = onBackClick,
                title = stringResource(id = R.string.map),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize(),
                uiSettings = uiSettings,
                cameraPositionState = cameraPositionState,
            ) {
                val configuration = LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp
                val screenWidth = configuration.screenWidthDp.dp
                val clusterManager = rememberClusterManager<ParkingLotCluster>()

                val parkingLotList = viewModel.parkingLotClusterListFlow.collectAsState(initial = emptyList())

                SideEffect {
                    clusterManager?.setAlgorithm(
                        NonHierarchicalViewBasedAlgorithm(
                            screenWidth.value.toInt(),
                            screenHeight.value.toInt(),
                        )
                    )

                    clusterManager?.setOnClusterClickListener {
                        val zoom = cameraPositionState.position.zoom
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(it.position, zoom + 1)
                        false
                    }

                    clusterManager?.setOnClusterItemInfoWindowClickListener {
                        onParkingLotItemClick(it.id)
                    }
                }

                val items = remember { mutableStateListOf<ParkingLotCluster>() }
                LaunchedEffect(parkingLotList.value) {
                    items.addAll(parkingLotList.value)
                }

                if (clusterManager != null) {
                    Clustering(
                        items = items,
                        clusterManager = clusterManager,
                    )
                }
            }
        }
    }
}

