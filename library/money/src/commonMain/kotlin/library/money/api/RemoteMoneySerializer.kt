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
        val (currencyCode, amount) = decoder.decodeString().split(' ', limit = 2)
        return Money(
            value = amount.trim().toDouble(),
            currency = Money.SupportedCurrency.valueOf(currencyCode),
        )
    }

    override fun serialize(encoder: Encoder, value: Money) {
        encoder.encodeString(
            "${value.currency.code()} ${value.valueToStringWithoutAmountSeparation()}",
        )
    }
}
