package com.tarikturkdil.photoProject.ui.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinDetailScreen(
    onBackClick: () -> Unit,
    onLikeStateChanged: (pinId: Long, isLiked: Boolean) -> Unit = { _, _ -> },
    viewModel: PinDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val boards by viewModel.boards.collectAsStateWithLifecycle()
    val showBoardPicker by viewModel.showBoardPicker.collectAsStateWithLifecycle()
    val saveStatus by viewModel.saveStatus.collectAsStateWithLifecycle()
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    val commentInput by viewModel.commentInput.collectAsStateWithLifecycle()
    val isPostingComment by viewModel.isPostingComment.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(saveStatus) {
        saveStatus?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeSaveStatus()
        }
    }

    val currentPinIsSaved = (uiState as? PinDetailUiState.Success)?.pin?.isSavedByMe ?: false

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pin Detayı") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.openBoardPicker() }) {
                        Icon(
                            imageVector = if (currentPinIsSaved) {
                                Icons.Filled.Bookmark
                            } else {
                                Icons.Filled.BookmarkBorder
                            },
                            contentDescription = "Panoya Kaydet",
                            tint = if (currentPinIsSaved) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                LocalContentColor.current
                            }
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 96.dp)
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is PinDetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is PinDetailUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message)
                }
            }

            is PinDetailUiState.Success -> {
                val pin = state.pin

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    AsyncImage(
                        model = pin.imageUrl,
                        contentDescription = pin.title,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            val newLikedState = !pin.isLikedByMe
                            viewModel.toggleLike()
                            onLikeStateChanged(pin.id, newLikedState)
                        }) {
                            Icon(
                                imageVector = if (pin.isLikedByMe) {
                                    Icons.Filled.Favorite
                                } else {
                                    Icons.Filled.FavoriteBorder
                                },
                                contentDescription = "Beğen",
                                tint = if (pin.isLikedByMe) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = pin.title, style = MaterialTheme.typography.headlineSmall)

                        pin.description?.let { description ->
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        pin.ownerUsername?.let { username ->
                            Text(
                                text = "Paylaşan: $username",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    Text(
                        text = "Yorumlar",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentInput,
                            onValueChange = { viewModel.onCommentInputChange(it) },
                            label = { Text("Yorum yaz...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(
                            onClick = { viewModel.postComment() },
                            enabled = !isPostingComment && commentInput.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Gönder"
                            )
                        }
                    }

                    comments.forEach { comment ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = comment.username ?: "Bilinmeyen kullanıcı",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = comment.text,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(96.dp))
                }
            }
        }
    }

    if (showBoardPicker) {
        ModalBottomSheet(onDismissRequest = { viewModel.closeBoardPicker() }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Panoya Kaydet", style = MaterialTheme.typography.titleLarge)

                if (boards.isEmpty()) {
                    Text(
                        text = "Henüz hiç panon yok.",
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    boards.forEach { board ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.saveToBoard(board.id) }
                                .padding(vertical = 12.dp)
                        ) {
                            Text(board.name)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}