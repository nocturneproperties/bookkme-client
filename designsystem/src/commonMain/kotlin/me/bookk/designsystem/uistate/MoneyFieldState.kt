package me.bookk.designsystem.uistate

import library.money.api.Money
import me.bookk.designsystem.uistate.simple.TextIcon

interface MoneyFieldState : ViewState {
    var currentValue: Money
    val textState: TextFieldState
    var currency: TextIcon?
}