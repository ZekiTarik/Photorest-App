package com.tarikturkdil.photoProject.ui.feed

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.tarikturkdil.photoProject.domain.model.Pin
import com.tarikturkdil.photoProject.ui.common.clickableWithoutRipple
import com.tarikturkdil.photoProject.ui.common.graphicsLayerScale
import com.tarikturkdil.photoProject.ui.common.shimmerEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onOpenMessages: () -> Unit,
    onPinClick: (Pin) -> Unit,
    refreshKey: Boolean = false,
    externalLikeUpdate: Pair<Long, Boolean>? = null,
    messageBadgeRefreshKey: Boolean = false,
    viewModel: FeedViewModel = hiltViewModel(),
    badgeViewModel: com.tarikturkdil.photoProject.ui.chat.MessageBadgeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val unreadMessages by badgeViewModel.unreadCount.collectAsStateWithLifecycle()

    LaunchedEffect(refreshKey) {
        if (refreshKey) viewModel.refresh()
    }
    LaunchedEffect(messageBadgeRefreshKey) {
        if (messageBadgeRefreshKey) badgeViewModel.refresh()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Size Özel", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    androidx.compose.material3.BadgedBox(
                        badge = {
                            if (unreadMessages > 0) {
                                androidx.compose.material3.Badge {
                                    Text(unreadMessages.toString())
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = onOpenMessages) {
                            Icon(
                                imageVector = Icons.Filled.MailOutline,
                                contentDescription = "Mesajlar"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is FeedUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is FeedUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message)
                }
            }

            is FeedUiState.Success -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    modifier = Modifier.padding(innerPadding)
                ) {
                    PinGrid(
                        pins = state.pins,
                        onPinClick = onPinClick,
                        onLikeClick = { pin -> viewModel.toggleLike(pin) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PinGrid(
    pins: List<Pin>,
    onPinClick: (Pin) -> Unit,
    onLikeClick: (Pin) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 96.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pins, key = { it.id }) { pin ->
            PinCard(
                pin = pin,
                onClick = { onPinClick(pin) },
                onLikeClick = { onLikeClick(pin) }
            )
        }
    }
}

@Composable
private fun PinCard(pin: Pin, onClick: () -> Unit, onLikeClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "pinCardScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(Modifier.graphicsLayerScale(scale))
            .clickableWithoutRipple(interactionSource, onClick)
    ) {
        SubcomposeAsyncImage(
            model = pin.imageUrl,
            contentDescription = pin.title,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().aspectRatio(3f / 4f).shimmerEffect()
                    )
                }
                is AsyncImagePainter.State.Error -> {
                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(3f / 4f))
                }
                else -> SubcomposeAsyncImageContent()
            }
        }

        IconButton(
            onClick = onLikeClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(32.dp)
                .background(Color.Black.copy(alpha = 0.35f), shape = CircleShape)
        ) {
            Icon(
                imageVector = if (pin.isLikedByMe) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Beğen",
                tint = if (pin.isLikedByMe) Color.Red else Color.White
            )
        }
    }
}