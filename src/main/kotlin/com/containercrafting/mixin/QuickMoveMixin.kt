package com.containercrafting.mixin

import com.containercrafting.GridLogic
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.HopperMenu
import net.minecraft.world.inventory.ShulkerBoxMenu
import net.minecraft.world.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/** One handler for all three menus; each of them overrides quickMoveStack with the same shape. */
@Mixin(ChestMenu::class, HopperMenu::class, ShulkerBoxMenu::class)
abstract class QuickMoveMixin {
    @Inject(method = ["quickMoveStack"], at = [At("HEAD")], cancellable = true)
    private fun containercrafting_quickMove(player: Player, slotIndex: Int, cir: CallbackInfoReturnable<ItemStack>) {
        val result = GridLogic.quickMove((this as Any) as AbstractContainerMenu, player, slotIndex)
        if (result != null) cir.returnValue = result
    }
}
