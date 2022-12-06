package com.example.onedeckdungeon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

    /*
    use coloured versions of the dice to negate the use of text down left side?
    4x2 grid for each colour (max 8 dice per | 6 heroic in play at once)
    "-" " Roll" "+" buttons under each section, amounts stick between rolls as levels
    gained but not lost
    */

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = die.color.displayName,
            color = Color.Red, //text colour
            modifier = Modifier.drawBehind {
                // draws circle behind text, circle is colour of the dice type
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
            // input box width
            modifier = modifier.width(70.dp),
            value = num.toString(),
            // Sets keyboard screen to numbers rather than text
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = { value ->
                // limits to two digit values for input (can +1 button into 3 digits)
                if (value.length in 1..2) {
                    onNumChange(value.filter { it.isDigit() }.toInt())
                }
            },
            label = { Text(label) }
        )
    }

    @Composable
    fun PlusOne() {
        // Increase num value by one
        Button(onClick = { onNumChange(num + 1) }) {
            // Button text
            Text(text = "+1")
        }
    }
    @Composable
    fun MinusOne() {
        // Decrease num value by one
        Button(onClick = { onNumChange(num - 1) }) {
            // Button text
            Text(text = "-1")
        }
    }
    /*
    if dice amount below one, only show "+1" button so dice cant be negative
    if dice amount between one and eight show both "+1" & "-1" buttons
    if dice amount above eight only show "-1" as eight is maximum dice amount in game

    PlusOne and MinusOne function to clean repetitive code, unsure if correct placement
    but unsure how to access DieSelector values if created outside of this function.
     */
    if (num in 1..7) {
        PlusOne()
        MinusOne()
    } else if (num < 1) {
        PlusOne()
    } else {
        MinusOne()
    }

}

@Composable
fun DiceMenu(
    diceViewModel: DiceViewModel,
    dice: Map<DieColor, Int>,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            Button(onClick = onDone) { Text(stringResource(R.string.done)) }
        }

    }
}

@Composable
fun RollDice(
    dice: List<Die>, onDone: () -> Unit, modifier: Modifier = Modifier
) {
    LazyColumn(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        items(dice) { die ->
            DieWithButtonAndImage(die, modifier)
        }
        item {
            Button(onClick = onDone) { Text(stringResource(R.string.done)) }
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
    val navController = rememberNavController()
    OneDeckDungeonTheme {
        NavHost(navController = navController, startDestination = Screens.MENU.name) {
            composable(route = Screens.MENU.name) {
                DiceMenu(
                    diceViewModel,
                    diceState.dice,
                    { navController.navigate(Screens.ROLL.name) },
                    modifier
                )
            }
            composable(route = Screens.ROLL.name) {
                RollDice(
                    diceViewModel.diceList(),
                    { navController.navigate(Screens.MENU.name) },
                    modifier
                )
            }
        }
    }
}