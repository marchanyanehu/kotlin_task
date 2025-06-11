package com.example.network.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.example.network.R
import com.example.network.domain.model.Cat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatCard(
    cat: Cat,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {}
) {
    var imageLoadError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box {
                // Используем SubcomposeAsyncImage для полного контроля над состояниями
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(cat.imageUrl)
                        .crossfade(true) // Плавный переход при загрузке
                        .crossfade(300) // Длительность анимации 300ms
                        .size(Size.ORIGINAL) // Загружать в оригинальном размере
                        .memoryCachePolicy(CachePolicy.ENABLED) // Кэширование в памяти
                        .diskCachePolicy(CachePolicy.ENABLED) // Кэширование на диске
                        .allowHardware(true) // Аппаратное ускорение
                        .listener(
                            onStart = { 
                                imageLoadError = false 
                            },
                            onError = { _, _ -> 
                                imageLoadError = true 
                            }
                        )
                        .build(),
                    contentDescription = "Cat image: ${cat.id}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                ) {
                    when (painter.state) {
                        is AsyncImagePainter.State.Loading -> {
                            // Кастомный индикатор загрузки
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        strokeWidth = 3.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Loading...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        
                        is AsyncImagePainter.State.Error -> {
                            // Кастомное состояние ошибки с возможностью повтора
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.errorContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Retry loading",
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Failed to load",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = "Tap to retry",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                        
                        is AsyncImagePainter.State.Success -> {
                            // Успешная загрузка с плавной анимацией появления
                            val alpha by animateFloatAsState(
                                targetValue = 1f,
                                label = "image_fade_in"
                            )
                            
                            androidx.compose.foundation.Image(
                                painter = painter,
                                contentDescription = contentDescription,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(alpha),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        else -> {
                            // Пустое состояние (обычно не используется)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface)
                            )
                        }
                    }
                }

                // Favorite button overlay
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Индикатор ошибки загрузки в углу
                if (imageLoadError) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.error,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "⚠️",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ID: ${cat.id}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Size: ${cat.width} x ${cat.height}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Показываем URL для демонстрации
                    Text(
                        text = "URL: ${cat.imageUrl.take(30)}...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            cat.breeds.firstOrNull()?.let { breed ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = breed.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                breed.description?.let { description ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3
                    )
                }
                breed.temperament?.let { temperament ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Temperament: $temperament",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                breed.origin?.let { origin ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Origin: $origin",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Show categories if available
            if (cat.categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Categories: ${cat.categories.joinToString(", ") { it.name }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Preview
@Composable
fun CatCardPreview() = CatCard(
    cat = Cat(
        id = "123",
        imageUrl = "https://placekitten.com/200/300",
        width = 200,
        height = 300
    ),
    isFavorite = false
)

@Preview
@Composable
fun CatCardFavoritePreview() = CatCard(
    cat = Cat(
        id = "123",
        imageUrl = "https://placekitten.com/200/300",
        width = 200,
        height = 300
    ),
    isFavorite = true
)