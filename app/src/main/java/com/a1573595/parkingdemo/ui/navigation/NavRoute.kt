package com.a1573595.parkingdemo.ui.navigation

sealed class NavRoute(val route: String) {
    companion object {
        const val KEY_ID = "id"
    }

    data object Home : NavRoute("home")
    data object Map : NavRoute("map")
    data object Search : NavRoute("search")
    data object Favorite : NavRoute("favorite")
    data object History : NavRoute("history")
    data object Detail : NavRoute("detail/{$KEY_ID}") {
        fun passParkingLotId(id: String): String {
            return this.route.replace(
                oldValue = "{$KEY_ID}", newValue = id
            )
        }
    }
}