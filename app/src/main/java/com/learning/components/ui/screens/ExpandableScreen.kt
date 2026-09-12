
package com.learning.components.ui.screens

import android.R.color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


data class ExpandableItem(
    val id: Int,
    val title: String,
    val description: String
)

@Composable
fun ExpandableScreen(
    modifier: Modifier = Modifier
) {

    var expandedItems by remember {
        mutableStateOf(setOf<Int>())
    }


    val items = remember {
        listOf(
            ExpandableItem(
                id = 1,
                title = "How does Poker work?",
                description = "Poker is a card game where players compete against each other based on the strength of their cards and the decisions they make."
            ),
            ExpandableItem(
                id = 2,
                title = "What is a poker hand?",
                description = "A poker hand is a combination of cards held by a player. The strength of the hand determines the winner when players reach the showdown."
            ),
            ExpandableItem(
                id = 3,
                title = "What does Call mean?",
                description = "Calling means matching the current bet made by another player. It allows you to continue playing without increasing the amount of the bet."
            ),
            ExpandableItem(
                id = 4,
                title = "What does Raise mean?",
                description = "Raising means increasing the current bet. Other players must either match the new amount, raise again, or fold their hands."
            ),
            ExpandableItem(
                id = 5,
                title = "What does Fold mean?",
                description = "Folding means giving up your hand and leaving the current round. You won't win the pot, but you also don't have to put more chips into the hand."
            ),
            ExpandableItem(
                id = 6,
                title = "What is a Showdown?",
                description = "A showdown happens when players reveal their cards after the final betting round. The player with the strongest valid hand wins the pot."
            )
        )
    }

    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            items.forEach{ item ->
                val isExpended = item.id in expandedItems

                ExpandableCard(
                    item = item,
                    expanded = isExpended,
                    onClick = {
                        expandedItems =
                            if (isExpended) {
                                expandedItems - item.id
                            } else {
                                expandedItems + item.id
                            }
                    }
                )
                if (isExpended) {
                    AnimatedVisibility(
                        visible = isExpended,
                        enter = expandVertically(
                            animationSpec = tween(800)
                        ) + fadeIn(
                            animationSpec = tween(800)
                        ),
                        exit = shrinkVertically(
                            animationSpec = tween(250)
                        ) + fadeOut(
                            animationSpec = tween(200)
                        )
                    ) {
                        ExpandedCard(
                            item = item
                        )
                    }
                }
            }

        }

    }
}


@Composable
fun ExpandableCard(
    item: ExpandableItem,
    expanded: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val cardShape = RoundedCornerShape(12.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    color = Color(0xFFBDC5E7)
                )
            ) {
                onClick()
            },

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF)
        ),
        border =BorderStroke(
            width = 1.dp,
            color = Color(0x7AD0D5DD)
        ),
        shape = cardShape
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .padding(16.dp),

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

        ) {

            Text(
                text = item.title
            )

            Icon(
                imageVector = if (expanded) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },

                contentDescription = if (expanded) {
                    "Collapse"
                } else {
                    "Expand"
                }
            )
        }
    }
}


@Composable
fun ExpandedCard(
    item: ExpandableItem
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF)
        ),

        border =BorderStroke(
            width = 1.dp,
            color = Color(0x7AD0D5DD)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {

        Text(
            text = item.description,

            modifier = Modifier
                .padding(16.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ExpandableScreenPreview() {
    ExpandableScreen()
}