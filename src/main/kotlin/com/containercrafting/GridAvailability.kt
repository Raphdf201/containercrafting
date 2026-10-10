package com.containercrafting

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

/** Decides, per player and per side, whether a menu gets the crafting grid. */
object GridAvailability {
    /**
     * Set by the client entrypoint. Kept as a hook so common code never touches client-only classes
     * (a dedicated server would crash on ClientPlayNetworking).
     */
    @Volatile
    var clientHasServerMod: () -> Boolean = { false }

    fun isAvailableFor(player: Player): Boolean = try {
        if (player.level().isClientSide()) {
            clientHasServerMod()
        } else {
            player is ServerPlayer && ServerPlayNetworking.canSend(player, HelloPayload.TYPE)
        }
    } catch (_: Throwable) {
        // e.g. fake players without a connection
        false
    }
}
