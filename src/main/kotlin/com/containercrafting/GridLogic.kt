package com.containercrafting

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Prediction
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ResultContainer
import net.minecraft.world.inventory.ResultSlot
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.TransientCraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingRecipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeType

/** All behaviour behind the mixins. Every entry point is a no-op for menus without a grid. */
object GridLogic {
    private fun AbstractContainerMenu.holder(): CraftingGridHolder = this as CraftingGridHolder

    fun stateOf(menu: AbstractContainerMenu): GridState? = menu.holder().gridState()

    /**
     * Appends the grid + result slots *after* every vanilla slot so vanilla slot indices are unchanged.
     * [guiHeight] is the height of the screen that will show this menu, used to center the panel vertically.
     */
    fun attach(menu: AbstractContainerMenu, player: Player, containerSize: Int, guiHeight: Int) {
        if (!GridAvailability.isAvailableFor(player)) return

        val craftSlots = TransientCraftingContainer(menu, 2, 2)
        val resultSlots = ResultContainer()
        val first = menu.slots.size
        val panelY = PanelLayout.panelY(guiHeight)

        for (row in 0 until 2) {
            for (col in 0 until 2) {
                menu.addSlot(Slot(craftSlots, col + row * 2, PanelLayout.gridSlotX(col), PanelLayout.gridSlotY(panelY, row)))
            }
        }
        menu.addSlot(ResultSlot(player, craftSlots, resultSlots, 0, PanelLayout.resultSlotX(), PanelLayout.resultSlotY(panelY)))

        menu.holder().setGridState(GridState(player, craftSlots, resultSlots, first, containerSize, panelY))
    }

    /**
     * Server-only. Re-implements the vanilla CraftingMenu helper because that one is protected and hard-codes
     * slot 0 as the result slot, which here would overwrite the first container slot.
     */
    fun onSlotsChanged(menu: AbstractContainerMenu, changed: Container) {
        val state = stateOf(menu) ?: return
        if (changed !== state.craftSlots) return
        val player = state.player as? ServerPlayer ?: return
        val level = player.level()

        val input = state.craftSlots.asCraftInput()
        var result = ItemStack.EMPTY
        val noHint: RecipeHolder<CraftingRecipe>? = null
        val recipe = level.server.recipeManager.getRecipeFor(RecipeType.CRAFTING, input, level, noHint)
        if (recipe.isPresent) {
            val holder = recipe.get()
            if (state.resultSlots.setRecipeUsed(player, holder)) {
                val assembled = holder.value().assemble(input)
                if (assembled.isItemEnabled(level.enabledFeatures())) result = assembled
            }
        }

        state.resultSlots.setItem(0, result)
        menu.setRemoteSlot(state.resultSlotIndex, result)
        player.connection.send(
            ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), state.resultSlotIndex, result)
        )
    }

    /** Hand grid contents back to the player (inventory first, drop if full). */
    fun onRemoved(menu: AbstractContainerMenu, player: Player) {
        val state = stateOf(menu) ?: return
        menu.clearContainer(player, state.craftSlots)
    }

    /** Vanilla blocks double-click "pick up all" from crafting results; we have to do the same. */
    fun canTakeForPickAll(menu: AbstractContainerMenu, target: Slot): Boolean {
        val state = stateOf(menu) ?: return true
        return target.container !== state.resultSlots
    }

    /**
     * Shift-click for menus with a grid. Returns null when the menu has no grid (vanilla handles it).
     *
     * Vanilla's quickMoveStack uses `slots.size()` as the upper bound when moving container items into the
     * player inventory, which would now include the grid, so container <-> inventory moves are redone here
     * with the bound set to the end of the player inventory. Nothing is ever moved *into* the grid.
     */
    fun quickMove(menu: AbstractContainerMenu, player: Player, index: Int): ItemStack? {
        val state = stateOf(menu) ?: return null
        val slot = menu.slots.getOrNull(index) ?: return ItemStack.EMPTY
        if (!slot.hasItem()) return ItemStack.EMPTY

        val stack = slot.item
        val clicked = stack.copy()
        val containerEnd = state.containerSize
        val invEnd = state.playerSlotsEnd
        val isResult = index == state.resultSlotIndex

        val moved = when {
            isResult -> {
                stack.item.onCraftedBy(stack, player)
                menu.moveItemStackTo(stack, containerEnd, invEnd, true)
            }
            index >= state.firstGridSlotIndex -> menu.moveItemStackTo(stack, containerEnd, invEnd, false)
            index < containerEnd -> menu.moveItemStackTo(stack, containerEnd, invEnd, true)
            else -> menu.moveItemStackTo(stack, 0, containerEnd, false)
        }
        if (!moved) return ItemStack.EMPTY

        if (isResult) slot.onQuickCraft(stack, clicked)

        if (stack.isEmpty) slot.setByPlayer(ItemStack.EMPTY) else slot.setChanged()

        if (isResult) {
            // Mirrors CraftingMenu: the caller loops while the result slot still matches, crafting repeatedly.
            if (stack.count == clicked.count) return ItemStack.EMPTY
            slot.onTake(player, stack)
            player.drop(stack, false, Prediction.PREDICTED)
        }
        return clicked
    }
}
