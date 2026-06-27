package library.money.api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class RemoteMoneySerializer : KSerializer<Money> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Money", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Money {
        val string = decoder.decodeString()
        return Money(
            value = string.substring(4, string.length - 1).toDouble(),
            currency = Money.SupportedCurrency.valueOf(string.substring(0, 3))
        )
    }

    override fun serialize(encoder: Encoder, value: Money) {
        encoder.encodeString("${value.currency.code()} ${value.valueToStringWithoutAmountSeparation()}")
    }
}