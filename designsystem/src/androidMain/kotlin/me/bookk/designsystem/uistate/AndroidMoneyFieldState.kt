package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import library.money.api.Money
import me.bookk.designsystem.uistate.simple.TextIcon

class AndroidMoneyFieldState : AndroidViewState(true), MoneyFieldState {
    override var currentValue: Money by mutableStateOf(Money(0, Money.SupportedCurrency.EUR))
    override val textState = AndroidTextFieldState()
    override var currency: TextIcon? by mutableStateOf(null)
}