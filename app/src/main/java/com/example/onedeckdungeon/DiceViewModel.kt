package com.example.onedeckdungeon

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DiceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DiceState())

    val uiState: StateFlow<DiceState> = _uiState.asStateFlow()

    fun addDice(dieColor: DieColor, num: Int) {
        // Copy map to ensure state flow updates
        val newDice = _uiState.value.dice.toMutableMap()
        newDice[dieColor] = num
        _uiState.update {
            currentState -> currentState.copy(dice = newDice)
        }
    }
}

data class DiceState(
    val dice: MutableMap<DieColor, Int> = DieColor.values().associateWith { 0 }.toMutableMap()
)