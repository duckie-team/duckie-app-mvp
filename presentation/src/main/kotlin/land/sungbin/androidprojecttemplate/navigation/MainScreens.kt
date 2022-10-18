package land.sungbin.androidprojecttemplate.navigation

sealed class MainScreens(
    val route: String,
) {
    object Home : MainScreens("LIST")
    object Search : MainScreens("SEARCH")
    object Notification : MainScreens("NOTIFICATION")
    object DuckChat : MainScreens("DUCK_CHAT")
}

val homeBottomNavItems = listOf(
    MainScreens.Home.route,
    MainScreens.Search.route,
    MainScreens.Notification.route,
    MainScreens.DuckChat.route,
)