package com.tarikturkdil.photoProject.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tarikturkdil.photoProject.domain.model.Board
import com.tarikturkdil.photoProject.domain.model.Pin
import com.tarikturkdil.photoProject.domain.model.UserProfile
import com.tarikturkdil.photoProject.ui.common.uriToMultipartPart
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onPinClick: (Pin) -> Unit,
    onBoardClick: (Board) -> Unit,
    onAddBoardClick: () -> Unit,
    onLogout: () -> Unit,
    onPinDeleted: () -> Unit,
    refreshKey: Boolean = false,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isUploadingAvatar by viewModel.isUploadingAvatar.collectAsStateWithLifecycle()
    val loggedOut by viewModel.loggedOut.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(loggedOut) {
        if (loggedOut) onLogout()
    }

    val avatarPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val part = context.uriToMultipartPart(it, "image")
            viewModel.updateAvatar(part)
        }
    }

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProfileUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message)
                }
            }
            is ProfileUiState.Success -> {
                androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                    isRefreshing = false,
                    onRefresh = { viewModel.refresh() }
                ) {
                    ProfileContent(
                        profile = state.profile,
                        pins = state.pins,
                        boards = state.boards,
                        isUploadingAvatar = isUploadingAvatar,
                        onAvatarClick = {
                            avatarPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onLogoutClick = { viewModel.logout() },
                        onPinClick = onPinClick,
                        onPinDeleteRequest = { pin ->
                            viewModel.deletePin(pin, onPinDeleted)
                        },
                        onBoardClick = onBoardClick,
                        onAddBoardClick = onAddBoardClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    pins: List<Pin>,
    boards: List<Board>,
    isUploadingAvatar: Boolean,
    onAvatarClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onPinClick: (Pin) -> Unit,
    onPinDeleteRequest: (Pin) -> Unit,
    onBoardClick: (Board) -> Unit,
    onAddBoardClick: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            androidx.compose.material3.IconButton(onClick = onLogoutClick) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Çıkış Yap"
                )
            }
        }

        ProfileHeader(
            profile = profile,
            pinCount = pins.size,
            isUploadingAvatar = isUploadingAvatar,
            onAvatarClick = onAvatarClick
        )

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Pinler") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Panolar") })
        }

        if (selectedTab == 0) {
            PinsGrid(pins = pins, onPinClick = onPinClick, onPinDeleteRequest = onPinDeleteRequest)
        } else {
            BoardsList(boards = boards, onBoardClick = onBoardClick, onAddBoardClick = onAddBoardClick)
        }
    }
}

@Composable
private fun PinsGrid(
    pins: List<Pin>,
    onPinClick: (Pin) -> Unit,
    onPinDeleteRequest: (Pin) -> Unit
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 16.dp, bottom = 96.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pins, key = { it.id }) { pin ->
            ProfilePinCard(
                pin = pin,
                onClick = { onPinClick(pin) },
                onDeleteClick = { onPinDeleteRequest(pin) }
            )
        }
    }
}

@Composable
private fun BoardsList(
    boards: List<Board>,
    onBoardClick: (Board) -> Unit,
    onAddBoardClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp)
    ) {
        item {
            BoardRow(
                icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
                title = "Yeni Pano Oluştur",
                subtitle = null,
                onClick = onAddBoardClick
            )
        }

        items(boards, key = { it.id }) { board ->
            BoardRow(
                icon = {
                    Icon(
                        imageVector = if (board.isSecret) Icons.Filled.Lock else Icons.Filled.Dashboard,
                        contentDescription = null
                    )
                },
                title = board.name,
                subtitle = board.description,
                onClick = { onBoardClick(board) }
            )
        }
    }
}

@Composable
private fun BoardRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String?,
    onClick: () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            subtitle?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    profile: UserProfile,
    pinCount: Int,
    isUploadingAvatar: Boolean,
    onAvatarClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(96.dp)) {
            if (profile.avatarUrl != null) {
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = "Profil fotoğrafı",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profil fotoğrafı yok",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            if (isUploadingAvatar) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(28.dp))
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onAvatarClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Profil fotoğrafını değiştir",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = profile.username,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 12.dp)
        )

        profile.bio?.let {
            Text(text = it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
        }

        Text(
            text = "$pinCount Pin",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun ProfilePinCard(pin: Pin, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }

    Box {
        AsyncImage(
            model = pin.imageUrl,
            contentDescription = pin.title,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
        )

        androidx.compose.material3.IconButton(
            onClick = { showConfirm = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(28.dp)
                .background(Color.Black.copy(alpha = 0.4f), androidx.compose.foundation.shape.CircleShape)
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Filled.Delete,
                contentDescription = "Sil",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }

    if (showConfirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Pini Sil") },
            text = { Text("Bu pini kalıcı olarak silmek istiyor musun? Bu işlem geri alınamaz.") },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showConfirm = false
                    onDeleteClick()
                }) { Text("Sil") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showConfirm = false }) { Text("Vazgeç") }
            }
        )
    }
}