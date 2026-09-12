package com.learning.components.ui.screens

import androidx.compose.foundation.background
import com.learning.components.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

data class ListItems(
    val id: Int,
    val name: String,
    var imageUrl: String,
)

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
) {
    val items = remember {
        listOf(
            ListItems(1, "Learn Kotlin", imageUrl ="https://example.com/kotlin.jpg"),
            ListItems(2, "Learn Python", imageUrl =""),
            ListItems(3, "Learn DS", imageUrl =""),
            ListItems(4, "Learn Design", imageUrl =""),
            ListItems(5, "Learn Android", imageUrl =""),
            ListItems(6, "Learn Jetpack Compose", imageUrl =""),
            ListItems(7, "Learn Coroutines", imageUrl =""),
            ListItems(8, "Learn Git", imageUrl =""),
            ListItems(9, "Learn Firebase", imageUrl =""),
            ListItems(10, "Learn Clean Architecture", imageUrl ="")
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(
            items = items,
            key = { it.id }
        ) { item ->

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEBEBF8))

            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp,
                            top = 24.dp,
                            bottom = 24.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    Text(
                        text = item.id.toString()
                    )

                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.image_placeholder),
                        error = painterResource(R.drawable.image_placeholder)
                    )

                    Text(
                        text = item.name
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {
    ListScreen()
}