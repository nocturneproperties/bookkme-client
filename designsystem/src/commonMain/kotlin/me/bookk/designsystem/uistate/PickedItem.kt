package me.bookk.designsystem.uistate

interface PickedItem<T : PickerPresentation> {
    val item: T
    var quantity: Int
}

fun <T : PickerPresentation> OptionsMultiPickerState<T>.quantityOf(item: T): Int {
    return pickedItemOf(item)?.quantity ?: 0
}

fun <T : PickerPresentation> OptionsMultiPickerState<T>.selectedUnits(): List<T> {
    return selectedItems.flatMap { picked -> List(picked.quantity) { picked.item } }
}

fun <T : PickerPresentation> OptionsMultiPickerState<T>.increaseQuantity(item: T) {
    val picked = pickedItemOf(item)
    if (picked == null) {
        replaceSelected(selectedItems + createPickedItem(item, 1))
    } else {
        picked.quantity += 1
    }
}

fun <T : PickerPresentation> OptionsMultiPickerState<T>.decreaseQuantity(item: T) {
    val picked = pickedItemOf(item) ?: return
    if (picked.quantity > 1) {
        picked.quantity -= 1
    } else {
        replaceSelected(selectedItems - picked)
    }
}

fun <T : PickerPresentation> OptionsMultiPickerState<T>.pick(items: List<T>) {
    val added = items
        .distinctBy { it.pickerItemId }
        .filter { quantityOf(it) == 0 }
        .map { createPickedItem(it, 1) }
    replaceSelected(selectedItems + added)
}

fun <T : PickerPresentation> OptionsMultiPickerState<T>.unpick(items: List<T>) {
    val removedIds = items.map { it.pickerItemId }.toSet()
    replaceSelected(selectedItems.filterNot { it.item.pickerItemId in removedIds })
}

fun <T : PickerPresentation> OptionsMultiPickerState<T>.replacePicked(items: List<T>) {
    replaceSelected(items.map { createPickedItem(it, 1) })
}

private fun <T : PickerPresentation> OptionsMultiPickerState<T>.pickedItemOf(
    item: T,
): PickedItem<T>? {
    return selectedItems.firstOrNull { it.item.pickerItemId == item.pickerItemId }
}
