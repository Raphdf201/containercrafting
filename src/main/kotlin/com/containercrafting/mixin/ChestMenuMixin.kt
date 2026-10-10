package com.containercrafting.mixin

import com.containercrafting.GridLogic
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ChestMenu::class)
abstract class ChestMenuMixin {
    // The only constructor that adds slots; every factory and other constructor delegates to it.
    @Inject(
        method = ["<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;I)V"],
        at = [At("RETURN")],
    )
    private fun containercrafting_addGrid(
        menuType: MenuType<*>, containerId: Int, inventory: Inventory, container: Container, rows: Int, ci: CallbackInfo,
    ) {
        // Same height ContainerScreen uses: 114 + rows * 18.
        GridLogic.attach((this as Any) as AbstractContainerMenu, inventory.player, rows * 9, 114 + rows * 18)
    }
}
