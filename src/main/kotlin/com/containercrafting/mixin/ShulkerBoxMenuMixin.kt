package com.containercrafting.mixin

import com.containercrafting.GridLogic
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ShulkerBoxMenu
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ShulkerBoxMenu::class)
abstract class ShulkerBoxMenuMixin {
    @Inject(
        method = ["<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;)V"],
        at = [At("RETURN")],
    )
    private fun containercrafting_addGrid(containerId: Int, inventory: Inventory, container: Container, ci: CallbackInfo) {
        // ShulkerBoxScreen is 176x167.
        GridLogic.attach((this as Any) as AbstractContainerMenu, inventory.player, 27, 167)
    }
}
