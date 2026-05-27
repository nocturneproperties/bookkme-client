package library.picker

import library.picker.state.AndroidPickOptionItem
import library.picker.state.AndroidPickOptionState
import library.picker.state.PickOptionItem
import library.picker.state.PickOptionState

class AndroidPickOptionStateFactory : PickOptionStateFactory {
    override fun createPickOptionState(): PickOptionState {
        return AndroidPickOptionState()
    }

    override fun createPickOptionItem(): PickOptionItem {
        return AndroidPickOptionItem()
    }
}