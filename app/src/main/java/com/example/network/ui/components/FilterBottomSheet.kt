package com.example.network.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.network.domain.model.CatBreed
import com.example.network.domain.model.CatCategory
import com.example.network.ui.state.ImageSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    breeds: List<CatBreed>,
    categories: List<CatCategory>,
    selectedBreed: CatBreed?,
    selectedCategory: CatCategory?,
    selectedImageSize: ImageSize,
    showOnlyWithBreeds: Boolean,
    onBreedSelected: (CatBreed?) -> Unit,
    onCategorySelected: (CatCategory?) -> Unit,
    onImageSizeSelected: (ImageSize) -> Unit,
    onShowOnlyWithBreedsChanged: (Boolean) -> Unit,
    onApplyFilters: () -> Unit,
    onClearFilters: () -> Unit
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss
        ) {
            FilterContent(
                breeds = breeds,
                categories = categories,
                selectedBreed = selectedBreed,
                selectedCategory = selectedCategory,
                selectedImageSize = selectedImageSize,
                showOnlyWithBreeds = showOnlyWithBreeds,
                onBreedSelected = onBreedSelected,
                onCategorySelected = onCategorySelected,
                onImageSizeSelected = onImageSizeSelected,
                onShowOnlyWithBreedsChanged = onShowOnlyWithBreedsChanged,
                onApplyFilters = {
                    onApplyFilters()
                    onDismiss()
                },
                onClearFilters = onClearFilters
            )
        }
    }
}

@Composable
private fun FilterContent(
    breeds: List<CatBreed>,
    categories: List<CatCategory>,
    selectedBreed: CatBreed?,
    selectedCategory: CatCategory?,
    selectedImageSize: ImageSize,
    showOnlyWithBreeds: Boolean,
    onBreedSelected: (CatBreed?) -> Unit,
    onCategorySelected: (CatCategory?) -> Unit,
    onImageSizeSelected: (ImageSize) -> Unit,
    onShowOnlyWithBreedsChanged: (Boolean) -> Unit,
    onApplyFilters: () -> Unit,
    onClearFilters: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredBreeds = remember(breeds, searchQuery) {
        if (searchQuery.isBlank()) {
            breeds
        } else {
            breeds.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Image Size Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Image Size",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(modifier = Modifier.selectableGroup()) {
                        ImageSize.values().forEach { size ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = selectedImageSize == size,
                                        onClick = { onImageSizeSelected(size) },
                                        role = Role.RadioButton
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedImageSize == size,
                                    onClick = null
                                )
                                Text(
                                    text = size.displayName,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Show Only With Breeds Option
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showOnlyWithBreeds,
                        onCheckedChange = onShowOnlyWithBreedsChanged
                    )
                    Text(
                        text = "Show only cats with breed descriptions",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        // Breed Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Filter by Breed",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search breeds") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Clear breed selection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedBreed == null,
                                onClick = { onBreedSelected(null) },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedBreed == null,
                            onClick = null
                        )
                        Text(
                            text = "All breeds",
                            modifier = Modifier.padding(start = 8.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Breed List
        items(filteredBreeds.take(10)) { breed ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedBreed?.id == breed.id,
                        onClick = { onBreedSelected(breed) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedBreed?.id == breed.id,
                    onClick = null
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = breed.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    breed.origin?.let { origin ->
                        Text(
                            text = "Origin: $origin",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Category Selection
        if (categories.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Filter by Category",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Clear category selection
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedCategory == null,
                                    onClick = { onCategorySelected(null) },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedCategory == null,
                                onClick = null
                            )
                            Text(
                                text = "All categories",
                                modifier = Modifier.padding(start = 8.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            items(categories) { category ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedCategory?.id == category.id,
                            onClick = { onCategorySelected(category) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedCategory?.id == category.id,
                        onClick = null
                    )
                    Text(
                        text = category.name,
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = onClearFilters,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear All")
                }
                Button(
                    onClick = onApplyFilters,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply")
                }
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun FilterBottomSheetPreview() {
    FilterBottomSheet(
        isVisible = true,
        onDismiss = {},
        breeds = emptyList(),
        categories = emptyList(),
        selectedBreed = null,
        selectedCategory = null,
        selectedImageSize = ImageSize.MEDIUM,
        showOnlyWithBreeds = false,
        onBreedSelected = {},
        onCategorySelected = {},
        onImageSizeSelected = {},
        onShowOnlyWithBreedsChanged = {},
        onApplyFilters = {},
        onClearFilters = {}
    )
} 