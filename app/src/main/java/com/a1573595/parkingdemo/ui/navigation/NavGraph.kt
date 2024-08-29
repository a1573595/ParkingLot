package com.a1573595.parkingdemo.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.a1573595.parkingdemo.ui.screen.HomeScreen
import com.a1573595.parkingdemo.ui.screen.detail.DetailScreen
import com.a1573595.parkingdemo.ui.screen.favorite.FavoriteScreen
import com.a1573595.parkingdemo.ui.screen.history.HistoryScreen
import com.a1573595.parkingdemo.ui.screen.map.MapScreen
import com.a1573595.parkingdemo.ui.screen.search.SearchScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            navController = navController,
            startDestination = NavRoute.Home.route,
        ) {
            val onParkingLotItemClick: (String) -> Unit = {
                navController.navigate(NavRoute.Detail.passParkingLotId(it))
            }

            composable(NavRoute.Home.route) {
                HomeScreen(
                    onMapClick = { navController.navigate(NavRoute.Map.route) },
                    onSearchClick = { navController.navigate(NavRoute.Search.route) },
                    onFavoriteClick = { navController.navigate(NavRoute.Favorite.route) },
                    onHistoryClick = { navController.navigate(NavRoute.History.route) },
                )
            }
            composable(NavRoute.Map.route) {
                MapScreen(
                    onBackClick = { navController.popBackStack() },
                    onParkingLotItemClick = onParkingLotItemClick,
                )
            }
            composable(NavRoute.Search.route) {
                SearchScreen(
                    onBackClick = { navController.popBackStack() },
                    onParkingLotItemClick = onParkingLotItemClick,
                )
            }
            composable(NavRoute.Favorite.route) {
                FavoriteScreen(
                    onBackClick = { navController.popBackStack() },
                    onParkingLotItemClick = onParkingLotItemClick,
                )
            }
            composable(NavRoute.History.route) {
                HistoryScreen(
                    onBackClick = { navController.popBackStack() },
                    onParkingLotItemClick = onParkingLotItemClick,
                )
            }
            composable(NavRoute.Detail.route) {
                DetailScreen(
                    onBackClick = { navController.popBackStack() },
                )
            }
        }
    }
}