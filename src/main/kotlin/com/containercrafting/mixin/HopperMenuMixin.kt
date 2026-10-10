package com.containercrafting.mixin

import com.containercrafting.GridLogic
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.HopperMenu
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(HopperMenu::class)
abstract class HopperMenuMixin {
    // The 2-arg constructor delegates to this one, so injecting here covers both exactly once.
    @Inject(
        method = ["<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;)V"],
        at = [At("RETURN")],
    )
    private fun containercrafting_addGrid(containerId: Int, inventory: Inventory, hopper: Container, ci: CallbackInfo) {
        // HopperScreen is only 133 tall; the 62px panel still fits.
        GridLogic.attach((this as Any) as AbstractContainerMenu, inventory.player, HopperMenu.CONTAINER_SIZE, 133)
    }
}
