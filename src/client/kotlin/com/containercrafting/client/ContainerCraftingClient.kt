package com.containercrafting.client

import com.containercrafting.GridAvailability
import com.containercrafting.HelloPayload
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

class ContainerCraftingClient : ClientModInitializer {
    override fun onInitializeClient() {
        // Registering a receiver is what makes the server see "this client has the mod" (see ContainerCrafting).
        ClientPlayNetworking.registerGlobalReceiver(HelloPayload.TYPE) { _, _ -> }

        // true only if the server announced the channel; false on vanilla/other servers.
        GridAvailability.clientHasServerMod = { ClientPlayNetworking.canSend(HelloPayload.TYPE) }
    }
}
