package com.example.onedeckdungeon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onedeckdungeon.ui.theme.OneDeckDungeonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OneDeckDungeonTheme {
                ODDApp()
            }
        }
    }
}

@Composable
fun DieWithButtonAndImage(
    die: Die,
    modifier: Modifier
) {
    var result by remember { mutableStateOf(1) }
    var rolled by remember { mutableStateOf(false) }
    val imageResource = when (result) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = die.color.displayName,
            color = Color.White,
            modifier = Modifier.drawBehind {
                drawCircle(
                    color = die.color.displayColor,
                    radius = this.size.maxDimension
                )
            },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(painter = painterResource(imageResource), contentDescription = result.toString())
        Spacer(modifier = Modifier.height(16.dp))
        if (!rolled) {
            Button(onClick = { result = (1..6).random(); rolled = true }) {
                Text(stringResource(R.string.roll))
            }
        }
    }
}

@Composable
fun DieSelector(modifier: Modifier, label: String, num: Int, onNumChange: (Int) -> Unit) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            modifier = modifier.width(70.dp),
            value = num.toString(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { value ->
                if (value.length in 1..2) {
                    onNumChange(value.filter { it.isDigit() }.toInt())
                }
            },
            label = { Text(label) }
        )
        Button(onClick = { onNumChange(num + 1) }) {
            Text(text = "test")
        }
    }

    Button(onClick = { onNumChange(num + 1) }) {
        Text(text = "test")
    }
}

@Composable
fun DiceMenu(modifier: Modifier, diceViewModel: DiceViewModel, dice: Map<DieColor, Int>) {
    LazyColumn(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        items(DieColor.values()) { dieColor ->
            DieSelector(
                modifier,
                dieColor.displayName,
                num = dice[dieColor]!!,
                onNumChange = { num ->
                    diceViewModel.addDice(dieColor, num)
                })
            Spacer(Modifier.height(16.dp))
        }
        item {
            Button(onClick = {
            }) { Text(stringResource(R.string.done)) }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ODDApp(
    modifier: Modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)
) {
    val diceViewModel = DiceViewModel()
    val diceState by diceViewModel.uiState.collectAsState()
    OneDeckDungeonTheme {
//        LazyColumn(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
//            items(dice) { die ->
//                DieWithButtonAndImage(die, modifier)
//            }
//        }
        DiceMenu(modifier, diceViewModel, diceState.dice)
    }
}