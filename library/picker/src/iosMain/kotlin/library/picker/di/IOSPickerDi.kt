package library.picker.di

import eu.advapay.mobilebank.core.UsedInSwift
import library.picker.PickOptionViewModel
import library.picker.PickerScreenArgs
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

internal actual fun platformPickerDiModule(): Module = module {
    factory { (args: PickerScreenArgs) ->
        PickOptionViewModel(args, get(), get())
    }
}

@UsedInSwift
fun pickOptionVM(args: PickerScreenArgs) =
    KoinPlatform.getKoin().get<PickOptionViewModel> { parametersOf(args) }
