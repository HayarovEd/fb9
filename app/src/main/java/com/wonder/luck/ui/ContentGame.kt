package com.wonder.luck.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonder.luck.data.egyptAlfa

@Composable
fun ContentGame(
    modifier: Modifier = Modifier,
    gameStatus: GameStatus,
    score: Int,
    bestScore: Int,
    image: Int,
    alfa: List<Int>,
    leftTime: Long,
    onEvent: (MainEvent) -> Unit
) {
    when (gameStatus) {
        GameStatus.Pause -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                OutlinedButton(
                    modifier = modifier
                        .fillMaxWidth()
                        .align(alignment = Alignment.Center),
                    onClick = {
                        onEvent(MainEvent.StartGame)
                    }) {
                    Text(
                        text = "Start game",
                        fontSize = 20.sp
                    )
                }
            }
        }

        GameStatus.Play -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Score: $score",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Best score: $bestScore",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = modifier.height(10.dp))
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = "Time left: $leftTime sec.",
                    color = Color.Green,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = modifier.height(15.dp))
                Image(
                    modifier = modifier.fillMaxWidth(),
                    painter = painterResource(id = image),
                    contentDescription = "",
                    //contentScale = ContentScale.FillWidth
                )
                Spacer(modifier = modifier.height(15.dp))
                LazyVerticalGrid(
                    modifier = modifier
                        .fillMaxWidth(),
                    columns = GridCells.Fixed(6)
                ) {
                    itemsIndexed(alfa) { index, imageAlfa ->
                        Image(
                            modifier = modifier.clickable {
                               onEvent(MainEvent.getAnswer(index))
                            },
                            painter = painterResource(id = imageAlfa),
                            contentDescription = "",
                        )
                    }
                }
                Spacer(modifier = modifier.height(25.dp))
                OutlinedButton(
                    modifier = modifier
                        .fillMaxWidth(),
                    onClick = {
                        onEvent(MainEvent.EndGame)
                    }) {
                    Text(
                        text = "Complete game",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SampleGame () {
    ContentGame(
        gameStatus = GameStatus.Play,
        score = 0,
        bestScore = 0,
        image = egyptAlfa.first(),
        alfa = egyptAlfa,
        leftTime = 120,
        onEvent = {}
    )
}