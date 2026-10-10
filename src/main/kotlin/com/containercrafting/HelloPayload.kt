package com.containercrafting

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

/**
 * Empty payload that exists only so each side can advertise "I know this channel".
 * Fabric exchanges the registered channels on join, so [GridAvailability] needs no handshake.
 */
object HelloPayload : CustomPacketPayload {
    val TYPE: CustomPacketPayload.Type<HelloPayload> =
        CustomPacketPayload.Type(Identifier.fromNamespaceAndPath(ContainerCrafting.MOD_ID, "hello"))

    val CODEC: StreamCodec<RegistryFriendlyByteBuf, HelloPayload> = StreamCodec.unit(this)

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}
