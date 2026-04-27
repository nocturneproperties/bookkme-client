package me.bookk.feature.business.presentation.settings.state

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import library.money.api.Money
import me.bookk.designsystem.uistate.PickerPresentation

data class CurrencyUI(
    override val pickerItemId: String,
    override val displayName: StringDesc,
    val domainValue: Money.SupportedCurrency
) : PickerPresentation()

fun Iterable<Money.SupportedCurrency>.toCurrencyUI():List<CurrencyUI> {
    return mapIndexed { index, supportedCurrency ->
        CurrencyUI(
            pickerItemId = index.toString(),
            displayName = supportedCurrency.name.desc(),
            domainValue = supportedCurrency
        )
    }
}