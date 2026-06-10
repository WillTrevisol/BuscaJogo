package com.trevisol.buscajogo.presentation.details

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.trevisol.buscajogo.R
import com.trevisol.buscajogo.domain.model.GameDetails
import com.trevisol.buscajogo.domain.model.Offer
import kotlin.math.min

@Composable
fun GameDetailsScreen(
    state: GameDetailsUiState,
    onBack: () -> Unit
) {
    val lavender = colorResource(id = R.color.primary)
    val darkSurface = colorResource(id = R.color.background)
    val scrollState = rememberLazyListState()

    Scaffold(
        containerColor = darkSurface,
        bottomBar = {
            if (state is GameDetailsUiState.Success) {
                BottomActionBar(lavender)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (state) {
                is GameDetailsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = lavender)
                    }
                }
                is GameDetailsUiState.Success -> {
                    GameDetailsContent(state.details, innerPadding, onBack, lavender, scrollState)
                    
                    // Fixed Top Overlay (Back and Favorite buttons + Animated Title)
                    val density = LocalDensity.current
                    val headerHeightPx = with(density) { 300.dp.toPx() }
                    
                    // Threshold for when the title should appear in TopBar
                    // Roughly when the main title scrolls out of view
                    val topBarAlpha by remember {
                        derivedStateOf {
                            if (scrollState.firstVisibleItemIndex > 0) 1f
                            else (scrollState.firstVisibleItemScrollOffset / headerHeightPx).coerceIn(0f, 1f)
                        }
                    }

                    val showTitleInTopBar by remember {
                        derivedStateOf {
                            // index 1 is usually where the title/content starts
                            scrollState.firstVisibleItemIndex >= 1 || 
                            (scrollState.firstVisibleItemIndex == 0 && scrollState.firstVisibleItemScrollOffset > headerHeightPx * 0.8)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = darkSurface.copy(alpha = topBarAlpha)
                            )
                            .statusBarsPadding()
                            .height(64.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        // Back Button
                        Surface(
                            color = Color.Black.copy(alpha = 0.4f * (1f - topBarAlpha)),
                            shape = CircleShape,
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White)
                            }
                        }
                        
                        // Animated Title - Aligned horizontally
                        AnimatedVisibility(
                            visible = showTitleInTopBar,
                            enter = fadeIn() + slideInVertically { it / 2 },
                            exit = fadeOut() + slideOutVertically { it / 2 },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text(
                                text = state.details.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 48.dp)
                            )
                        }

                        // Favorite Button
                        Surface(
                            color = Color.Black.copy(alpha = 0.4f * (1f - topBarAlpha)),
                            shape = CircleShape,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            IconButton(onClick = { /* Favoritar */ }) {
                                Icon(Icons.Default.FavoriteBorder, "Favoritar", tint = Color.White)
                            }
                        }
                    }
                }
                is GameDetailsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomActionBar(lavender: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        ExtendedFloatingActionButton(
            onClick = { /* Lógica de coleção */ },
            containerColor = lavender,
            contentColor = colorResource(id = R.color.on_primary),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Icon(Icons.Default.CreateNewFolder, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Adicionar à Coleção", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailsContent(
    game: GameDetails,
    innerPadding: PaddingValues,
    onBack: () -> Unit,
    lavender: Color,
    scrollState: androidx.compose.foundation.lazy.LazyListState
) {
    val cyanScore = colorResource(id = R.color.secondary)
    val cardOutline = colorResource(id = R.color.tag_background)
    val darkSurface = colorResource(id = R.color.background)
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding())
    ) {
        // ... previous items ...
        item {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
            ) {
                AsyncImage(
                    model = game.bannerUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationY = scrollState.firstVisibleItemScrollOffset / 2f
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        val alpha = if (scrollState.firstVisibleItemIndex == 0) {
                            1f
                        } else if (scrollState.firstVisibleItemIndex == 1) {
                            (1f - (scrollState.firstVisibleItemScrollOffset / 200f)).coerceIn(0f, 1f)
                        } else {
                            0f
                        }
                        this.alpha = alpha
                    }
                    .background(
                        color = darkSurface,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (game.score != null) {
                        Surface(
                            color = cyanScore,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Text(
                                text = game.score.toString(),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    
                    game.genres.forEach { genre ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(genre, color = Color.Gray) },
                            shape = CircleShape,
                            modifier = Modifier.padding(end = 8.dp),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                borderColor = cardOutline,
                                enabled = true
                            )
                        )
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(darkSurface)
                .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    "Sobre",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = game.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    lineHeight = 22.sp
                )
            }
        }

        item {
            Text(
                "Onde Comprar",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(darkSurface)
                    .padding(16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (game.offers.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(darkSurface)
                        .padding(horizontal = 16.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma oferta encontrada no momento.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(game.offers) { offer ->
                Box(modifier = Modifier.fillMaxWidth().background(darkSurface)) {
                    StoreOfferItem(
                        offer = offer,
                        lavender = lavender,
                        cardOutline = cardOutline,
                        onViewStore = {
                            if (offer.storeUrl.isNotEmpty()) {
                                uriHandler.openUri(offer.storeUrl)
                            }
                        }
                    )
                }
            }
        }
        
        item { 
            Spacer(Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(darkSurface)
            ) 
        }
    }
}

@Composable
fun StoreOfferItem(
    offer: Offer,
    lavender: Color,
    cardOutline: Color,
    onViewStore: () -> Unit
) {
    val storeIcon = when {
        offer.storeName.contains("Steam", ignoreCase = true) -> Icons.Default.SportsEsports
        offer.storeName.contains("Epic", ignoreCase = true) -> Icons.Default.ShoppingBag
        else -> Icons.Default.Computer
    }
    
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, cardOutline),
        colors = CardDefaults.outlinedCardColors(containerColor = colorResource(id = R.color.surface))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(cardOutline),
                contentAlignment = Alignment.Center
            ) {
                Icon(storeIcon, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(offer.storeName, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    offer.price ?: "--",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                
                OutlinedButton(
                    onClick = onViewStore,
                    border = BorderStroke(1.dp, Color.Gray),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Ver Loja", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}
