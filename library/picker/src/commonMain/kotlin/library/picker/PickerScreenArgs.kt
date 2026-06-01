package library.picker

import kotlinx.serialization.Serializable
import me.bookk.core.domain.entity.KeyValueData

@Serializable
class PickerScreenArgs(
    val id: String,
    val title: String,
    val options: List<PickerData>,
    val choice: Choice
) {

    enum class Choice {
        SINGLE,
        MULTIPLE
    }

    @Serializable
    data class PickerData(
        val data: KeyValueData,
        val iconUrl: String? = null
    )
}