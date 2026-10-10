package com.containercrafting

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import org.slf4j.LoggerFactory

class ContainerCrafting : ModInitializer {
    override fun onInitialize() {
        PayloadTypeRegistry.serverboundPlay().register(HelloPayload.TYPE, HelloPayload.CODEC)
        PayloadTypeRegistry.clientboundPlay().register(HelloPayload.TYPE, HelloPayload.CODEC)

        // Fabric's canSend() is true only if the remote side registered a *receiver* for the channel,
        // so the server needs a (no-op) C2S receiver; the client registers the S2C one.
        ServerPlayNetworking.registerGlobalReceiver(HelloPayload.TYPE) { _, _ -> }

        LOGGER.info("Container Crafting loaded")
    }

    companion object {
        const val MOD_ID = "containercrafting"
        val LOGGER = LoggerFactory.getLogger(MOD_ID)
    }
}
