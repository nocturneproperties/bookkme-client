package me.bookk.core.data

import kotlinx.serialization.protobuf.ProtoBuf


val dataSerializer = ProtoBuf { encodeDefaults = true }