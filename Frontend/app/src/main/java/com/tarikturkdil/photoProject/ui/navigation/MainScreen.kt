package com.tarikturkdil.photoProject.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tarikturkdil.photoProject.ui.addpin.AddPinMenuScreen
import com.tarikturkdil.photoProject.ui.addpin.AddPinScreen
import com.tarikturkdil.photoProject.ui.common.bottomNavItems
import com.tarikturkdil.photoProject.ui.feed.FeedScreen
import com.tarikturkdil.photoProject.ui.feed.PinDetailScreen

@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    val innerNavController = rememberNavController()

    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainTabRoutes = remember {
        setOf(
            Screen.Feed.route,
            Screen.Search.route,
            Screen.AddPin.route,
            Screen.Notifications.route,
            Screen.Profile.route
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = innerNavController,
            startDestination = Screen.Feed.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Feed.route) { backStackEntry ->
                val pinCreatedFlow = backStackEntry.savedStateHandle.getStateFlow("pin_created", false)
                val pinCreated by pinCreatedFlow.collectAsState()

                val likedPinIdFlow = backStackEntry.savedStateHandle.getStateFlow<Long?>("liked_pin_id", null)
                val likedPinId by likedPinIdFlow.collectAsState()
                val likedPinValueFlow = backStackEntry.savedStateHandle.getStateFlow("liked_pin_value", false)
                val likedPinValue by likedPinValueFlow.collectAsState()

                val messagesUpdatedFlow = backStackEntry.savedStateHandle.getStateFlow("messages_updated", false)
                val messagesUpdated by messagesUpdatedFlow.collectAsState()

                FeedScreen(
                    onOpenMessages = {
                        innerNavController.navigate(Screen.Conversations.route)
                    },
                    onPinClick = { pin ->
                        innerNavController.navigate(Screen.PinDetail.createRoute(pin.id))
                    },
                    refreshKey = pinCreated,
                    externalLikeUpdate = likedPinId?.let { it to likedPinValue },
                    messageBadgeRefreshKey = messagesUpdated
                )

                LaunchedEffect(pinCreated) {
                    if (pinCreated) {
                        backStackEntry.savedStateHandle["pin_created"] = false
                    }
                }

                LaunchedEffect(likedPinId) {
                    if (likedPinId != null) {
                        backStackEntry.savedStateHandle["liked_pin_id"] = null
                    }
                }

                LaunchedEffect(messagesUpdated) {
                    if (messagesUpdated) {
                        backStackEntry.savedStateHandle["messages_updated"] = false
                    }
                }
            }

            composable(Screen.Search.route) {
                com.tarikturkdil.photoProject.ui.search.SearchScreen(
                    onPinClick = { pin -> innerNavController.navigate(Screen.PinDetail.createRoute(pin.id)) },
                    onStartChat = { conversationId ->
                        innerNavController.navigate(Screen.Chat.createRoute(conversationId))
                    }
                )
            }

            composable(Screen.AddPin.route) {
                AddPinMenuScreen(
                    onClose = { innerNavController.popBackStack() },
                    onCreatePinClick = { innerNavController.navigate(Screen.CreatePin.route) },
                    onCreateBoardClick = { innerNavController.navigate(Screen.CreateBoard.route) }
                )
            }

            composable(Screen.Conversations.route) {
                com.tarikturkdil.photoProject.ui.chat.ConversationsListScreen(
                    onBackClick = { innerNavController.popBackStack() },
                    onConversationClick = { conversation ->
                        innerNavController.navigate(Screen.Chat.createRoute(conversation.id))
                    }
                )
            }

            composable(Screen.CreateBoard.route) {
                com.tarikturkdil.photoProject.ui.board.CreateBoardScreen(
                    onBackClick = { innerNavController.popBackStack() },
                    onBoardCreated = {
                        val profileEntry = runCatching {
                            innerNavController.getBackStackEntry(Screen.Profile.route)
                        }.getOrNull()

                        if (profileEntry != null) {
                            profileEntry.savedStateHandle["board_created"] = true
                            innerNavController.popBackStack(Screen.Profile.route, inclusive = false)
                        } else {
                            innerNavController.navigate(Screen.Profile.route) {
                                popUpTo(Screen.Feed.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            composable(
                route = Screen.BoardDetail.route,
                arguments = listOf(navArgument("boardId") { type = NavType.LongType })
            ) {
                com.tarikturkdil.photoProject.ui.board.BoardDetailScreen(
                    onBackClick = { innerNavController.popBackStack() },
                    onPinClick = { pin -> innerNavController.navigate(Screen.PinDetail.createRoute(pin.id)) }
                )
            }

            composable(Screen.CreatePin.route) {
                AddPinScreen(
                    onBackClick = { innerNavController.popBackStack() },
                    onPinUploaded = {
                        innerNavController.getBackStackEntry(Screen.Feed.route)
                            .savedStateHandle["pin_created"] = true
                        innerNavController.popBackStack(Screen.Feed.route, inclusive = false)
                    }
                )
            }

            composable(Screen.Notifications.route) {
                com.tarikturkdil.photoProject.ui.notifications.NotificationsScreen(
                    onNotificationClick = { notification ->
                        when (notification.type) {
                            com.tarikturkdil.photoProject.domain.model.NotificationType.PIN_LIKED,
                            com.tarikturkdil.photoProject.domain.model.NotificationType.NEW_COMMENT -> {
                                notification.referenceId?.let { pinId ->
                                    innerNavController.navigate(Screen.PinDetail.createRoute(pinId))
                                }
                            }
                            else -> {
                                // NEW_FOLLOWER, NEW_MESSAGE — şimdilik yönlendirme yok
                            }
                        }
                    }
                )
            }

            composable(Screen.Profile.route) { backStackEntry ->
                val boardCreatedFlow = backStackEntry.savedStateHandle.getStateFlow("board_created", false)
                val boardCreated by boardCreatedFlow.collectAsState()

                com.tarikturkdil.photoProject.ui.profile.ProfileScreen(
                    onPinClick = { pin -> innerNavController.navigate(Screen.PinDetail.createRoute(pin.id)) },
                    onBoardClick = { board -> innerNavController.navigate(Screen.BoardDetail.createRoute(board.id)) },
                    onAddBoardClick = { innerNavController.navigate(Screen.CreateBoard.route) },
                    onLogout = onLogout,
                    onPinDeleted = {
                        innerNavController.getBackStackEntry(Screen.Feed.route)
                            .savedStateHandle["pin_created"] = true
                    },
                    refreshKey = boardCreated
                )

                LaunchedEffect(boardCreated) {
                    if (boardCreated) {
                        backStackEntry.savedStateHandle["board_created"] = false
                    }
                }
            }

            composable(
                route = Screen.Chat.route,
                arguments = listOf(navArgument("conversationId") { type = NavType.LongType })
            ) {
                com.tarikturkdil.photoProject.ui.chat.ChatScreen(
                    onBackClick = {
                        runCatching {
                            innerNavController.getBackStackEntry(Screen.Feed.route)
                        }.getOrNull()?.savedStateHandle?.set("messages_updated", true)
                        innerNavController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.PinDetail.route,
                arguments = listOf(navArgument("pinId") { type = NavType.LongType })
            ) {
                PinDetailScreen(
                    onBackClick = { innerNavController.popBackStack() },
                    onLikeStateChanged = { pinId, isLiked ->
                        val feedEntry = innerNavController.getBackStackEntry(Screen.Feed.route)
                        feedEntry.savedStateHandle["liked_pin_id"] = pinId
                        feedEntry.savedStateHandle["liked_pin_value"] = isLiked
                    }
                )
            }
        }

        if (currentRoute in mainTabRoutes) {
            GlassBottomNavigation(
                navController = innerNavController,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun GlassBottomNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route == item.screen.route
            } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = null,
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(16.dp)
    )
}