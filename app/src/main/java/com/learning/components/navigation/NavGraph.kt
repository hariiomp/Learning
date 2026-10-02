package com.learning.components.navigation

import android.os.Build
import android.provider.CalendarContract.Attendees.query
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.learning.components.ui.screens.AirQuality
import com.learning.components.ui.screens.ButtonsScreen
import com.learning.components.ui.screens.CardCarousel
import com.learning.components.ui.screens.CardFly
import com.learning.components.ui.screens.CircularProgressScreen
import com.learning.components.ui.screens.ExpandSearch
import com.learning.components.ui.screens.ExpandableItem
import com.learning.components.ui.screens.ExpandableScreen
import com.learning.components.ui.screens.FabricScreen

import com.learning.components.ui.screens.HomeScreen
import com.learning.components.ui.screens.ListScreen
import com.learning.components.ui.screens.MyCardFly
import com.learning.components.ui.screens.OfferScreen
import com.learning.components.ui.screens.SwipeCard

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    var query by remember {
        mutableStateOf("")
    }
    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigate = { screen -> navController.navigate(screen.route) }
            )
        }
        composable(Screen.Buttons.route) {
            ButtonsScreen()
        }
        composable(Screen.CardCarousel.route) {
            CardCarousel()
        }
        composable(Screen.ExpandedSearch.route){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                ExpandSearch(
                    query = query,
                    onQueryChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                )
            }
        }

        composable(Screen.ListScreen.route) { ListScreen() }
        composable (Screen.ExpandableScreen.route)  { ExpandableScreen() }
        composable(Screen.CircularProgressScreen.route) { CircularProgressScreen() }
        composable(Screen.OfferScreen.route) { OfferScreen() }
        composable(Screen.MyCardFly.route) { MyCardFly() }
        composable(Screen.AirQuality.route) { AirQuality() }
        composable(Screen.FabricScreen.route) { FabricScreen() }
        composable(Screen.SwipeCard.route) { SwipeCard() }
}
    }
