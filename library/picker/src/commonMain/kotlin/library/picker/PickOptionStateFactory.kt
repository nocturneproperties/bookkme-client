package library.picker

import library.picker.state.PickOptionItem
import library.picker.state.PickOptionState

interface PickOptionStateFactory {
    fun createPickOptionState(): PickOptionState
    fun createPickOptionItem(): PickOptionItem
}