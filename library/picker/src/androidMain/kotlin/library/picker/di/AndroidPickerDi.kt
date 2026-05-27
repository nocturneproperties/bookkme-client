package library.picker.di

import library.picker.PickOptionViewModel
import library.picker.PickerScreenArgs
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal actual fun platformPickerDiModule(): Module  = module {
    viewModel { (args: PickerScreenArgs) ->
        PickOptionViewModel(args, get(), get())
    }
}