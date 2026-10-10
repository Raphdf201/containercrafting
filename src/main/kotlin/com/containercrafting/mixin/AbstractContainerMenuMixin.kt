package com.containercrafting.mixin

import com.containercrafting.CraftingGridHolder
import com.containercrafting.GridLogic
import com.containercrafting.GridState
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.inventory.ResultContainer
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * Hosts the grid state on every menu (null = no grid) and the hooks that Chest/Hopper/ShulkerBox menus
 * inherit rather than override: slotsChanged, removed (via their super call) and canTakeItemForPickAll.
 * Every hook is gated on the state, so menus without a grid behave exactly like vanilla.
 */
@Mixin(AbstractContainerMenu::class)
abstract class AbstractContainerMenuMixin : CraftingGridHolder {
    @field:Unique
    private var containercraftingState: GridState? = null

    override fun gridState(): GridState? = containercraftingState

    override fun setGridState(state: GridState?) {
        containercraftingState = state
    }

    override fun hasGrid(): Boolean = containercraftingState != null
    override fun craftSlots(): CraftingContainer? = containercraftingState?.craftSlots
    override fun resultSlots(): ResultContainer? = containercraftingState?.resultSlots
    override fun firstGridSlotIndex(): Int = containercraftingState?.firstGridSlotIndex ?: -1

    @Inject(method = ["slotsChanged"], at = [At("HEAD")])
    private fun containercrafting_slotsChanged(container: Container, ci: CallbackInfo) {
        if (containercraftingState != null) GridLogic.onSlotsChanged(self(), container)
    }

    @Inject(method = ["removed"], at = [At("TAIL")])
    private fun containercrafting_removed(player: Player, ci: CallbackInfo) {
        if (containercraftingState != null) GridLogic.onRemoved(self(), player)
    }

    @Inject(method = ["canTakeItemForPickAll"], at = [At("HEAD")], cancellable = true)
    private fun containercrafting_canTakeForPickAll(carried: ItemStack, target: Slot, cir: CallbackInfoReturnable<Boolean>) {
        if (containercraftingState != null && !GridLogic.canTakeForPickAll(self(), target)) cir.returnValue = false
    }

    @Unique
    private fun self(): AbstractContainerMenu = (this as Any) as AbstractContainerMenu
}
