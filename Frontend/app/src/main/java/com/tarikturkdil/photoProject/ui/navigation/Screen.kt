package com.tarikturkdil.photoProject.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Main : Screen("main")

    data object Splash : Screen("splash")

    // Bottom navigation ekranları
    data object Feed : Screen("feed")
    data object Search : Screen("search")
    data object AddPin : Screen("add_pin")
    data object CreatePin : Screen("create_pin")
    data object Notifications : Screen("notifications")
    data object Profile : Screen("profile")

    // Bottom bar'da YER ALMAYAN, iç navigasyona ait ekstra ekranlar
    data object Conversations : Screen("conversations")
    data object Chat : Screen("chat/{conversationId}") {
        fun createRoute(conversationId: Long) = "chat/$conversationId"
    }

    data object PinDetail : Screen("pin_detail/{pinId}") {
        fun createRoute(pinId: Long) = "pin_detail/$pinId"
    }

    data object CreateBoard : Screen("create_board")
    data object BoardDetail : Screen("board_detail/{boardId}") {
        fun createRoute(boardId: Long) = "board_detail/$boardId"
    }
}