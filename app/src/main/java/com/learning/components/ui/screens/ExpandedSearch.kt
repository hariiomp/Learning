package com.learning.components.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ExpandSearch(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search..."
) {

    var isExpanded by remember {
        mutableStateOf(false)
    }

    val focusRequester = remember {
        FocusRequester()
    }

    val keyboardController =
        LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (isExpanded) {

                SearchField(
                    modifier = Modifier.weight(1f),
                    query = query,
                    placeholder = placeholder,
                    focusRequester = focusRequester,
                    onQueryChange = onQueryChange
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )
            }

            SearchActionButton(
                isExpanded = isExpanded,
                onClick = {
                    if (isExpanded) {
                        isExpanded = false
                        onQueryChange("")

                    } else {
                        isExpanded = true
                    }
                }
            )
        }

        LaunchedEffect(isExpanded) {

            if (isExpanded) {
                focusRequester.requestFocus()
                keyboardController?.show()

            } else {
                keyboardController?.hide()
            }
        }
    }
}


@Composable
private fun SearchField(
    modifier: Modifier = Modifier,
    query: String,
    placeholder: String,
    focusRequester: FocusRequester,
    onQueryChange: (String) -> Unit
) {

    Box(
        modifier = modifier
            .height(48.dp)
            .background(
                Color(0xFFE8EFFC),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.CenterStart
    ) {

        ExpandedSearchField(
            query = query,
            placeholder = placeholder,
            focusRequester = focusRequester,
            onQueryChange = onQueryChange
        )
    }
}


@Composable
private fun ExpandedSearchField(
    query: String,
    placeholder: String,
    focusRequester: FocusRequester,
    onQueryChange: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,

            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
                .focusRequester(focusRequester),

            textStyle = TextStyle(
                fontSize = 16.sp
            ),

            decorationBox = { innerTextField ->

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    if (query.isEmpty()) {

                        Text(
                            text = placeholder,
                            color = Color.Gray
                        )
                    }

                    innerTextField()
                }
            }
        )
    }
}


@Composable
private fun SearchActionButton(
    isExpanded: Boolean,
    onClick: () -> Unit
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = if (isPressed) {
                    Color(0xFFD8E4F6)
                } else {
                    Color(0xFFE8EFFC)
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .semantics {
                contentDescription =
                    if (isExpanded) {
                        "Close search"
                    } else {
                        "Open search"
                    }
            },
        contentAlignment = Alignment.Center
    ) {

        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(150)
                ) togetherWith fadeOut(
                    animationSpec = tween(50)
                )
            },
            label = "search_action_icon"
        ) { expanded ->

            Icon(
                imageVector = if (expanded) {
                    Icons.Default.Close
                } else {
                    Icons.Default.Search
                },
                contentDescription = null,
                tint = if (isPressed) {
                    Color.White
                } else {
                    Color.Black
                }
            )
        }
    }
}