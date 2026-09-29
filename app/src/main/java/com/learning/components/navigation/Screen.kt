package com.learning.components.navigation

sealed class Screen(val route: String, val title: String) {
    data object Home : Screen("home", "Components")
    data object Buttons : Screen("buttons", "Buttons")
    data object CardCarousel: Screen("cardcarousel", "Card Carousel")
    data object ExpandedSearch: Screen("expandedsearch", "Expanded Search")
    data object ListScreen: Screen("listscreen", "List")
    data object ExpandableScreen: Screen("expandablescreen", "Expandable FAQ")
    data object CircularProgressScreen: Screen("circularprogressscreen", "Circular Progress")
    data object OfferScreen: Screen("offerscreen", "Offer")
    data object MyCardFly: Screen("mycardfly", "My Card Fly")
    data object FabricScreen: Screen("fabricscreen", "")
    data object SwipeCard: Screen("swipecard", "Swipe Card")
}

val allScreens = listOf(
    Screen.Buttons,
    Screen.CardCarousel,
    Screen.ExpandedSearch,
    Screen.ListScreen,
    Screen.ExpandableScreen,
    Screen.CircularProgressScreen,
    Screen.OfferScreen,
    Screen.MyCardFly,
    Screen.FabricScreen,
    Screen.SwipeCard
)
