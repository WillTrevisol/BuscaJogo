package com.trevisol.buscajogo.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.trevisol.buscajogo.R
import com.trevisol.buscajogo.domain.model.Game

@Composable
fun SearchScreen(
    query: String,
    uiState: SearchUiState,
    trendingGames: List<Game>,
    onQueryChanged: (String) -> Unit,
    onGameClick: (Int) -> Unit
) {
    val darkSurface = colorResource(id = R.color.background)
    val cardBackground = colorResource(id = R.color.surface)
    val lavender = colorResource(id = R.color.primary)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkSurface)
            .statusBarsPadding()
            .padding(top = 16.dp)
    ) {
        // Search Bar
        SearchBar(
            query = query,
            onQueryChanged = onQueryChanged,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (uiState) {
            is SearchUiState.Idle -> {
                TrendingContent(trendingGames, onGameClick)
            }
            is SearchUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = lavender)
                }
            }
            is SearchUiState.Success -> {
                SearchResults(uiState.games, onGameClick)
            }
            is SearchUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.message, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp)),
        placeholder = { Text("Buscar jogos...", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colorResource(id = R.color.surface),
            unfocusedContainerColor = colorResource(id = R.color.surface),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = colorResource(id = R.color.primary),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun TrendingContent(
    trendingGames: List<Game>,
    onGameClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(
                text = "Em Alta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Featured Game
        if (trendingGames.isNotEmpty()) {
            item {
                FeaturedGameItem(trendingGames[0], onGameClick)
            }
        }

        // Other trending games in grid
        item {
            val otherGames = if (trendingGames.size > 1) trendingGames.drop(1) else emptyList()
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 1000.dp) // Fixed height to allow nested scrolling
                    .padding(bottom = 16.dp),
                userScrollEnabled = false // Let parent scroll
            ) {
                items(otherGames) { game ->
                    SearchGameItem(game, onGameClick)
                }
            }
        }
    }
}

@Composable
fun FeaturedGameItem(
    game: Game,
    onGameClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onGameClick(game.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.surface))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = game.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dark overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 100f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "MAIS BUSCADO",
                    color = colorResource(id = R.color.primary),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = game.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun SearchResults(
    games: List<Game>,
    onGameClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(games) { game ->
            SearchGameItem(game, onGameClick)
        }
    }
}

@Composable
fun SearchGameItem(
    game: Game,
    onGameClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(150.dp)
            .clickable { onGameClick(game.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.surface))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = game.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dark overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            Text(
                text = game.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                maxLines = 2
            )
        }
    }
}
