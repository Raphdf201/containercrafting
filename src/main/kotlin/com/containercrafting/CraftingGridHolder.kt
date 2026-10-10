package com.containercrafting

import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.inventory.ResultContainer

/** Duck interface mixed into every [net.minecraft.world.inventory.AbstractContainerMenu]. */
interface CraftingGridHolder {
    fun hasGrid(): Boolean
    fun craftSlots(): CraftingContainer?
    fun resultSlots(): ResultContainer?
    /** Index of the first of the four grid slots; the result slot is [GRID_SIZE] slots later. */
    fun firstGridSlotIndex(): Int
    fun gridState(): GridState?
    fun setGridState(state: GridState?)
}

class GridState(
    val player: Player,
    val craftSlots: CraftingContainer,
    val resultSlots: ResultContainer,
    val firstGridSlotIndex: Int,
    /** Number of vanilla container slots (chest rows * 9, 5 for hoppers, ...); the 36 player slots follow. */
    val containerSize: Int,
    /** Menu-relative y of the panel's top edge (panel is vertically centered on the GUI). */
    val panelY: Int,
) {
    val resultSlotIndex: Int get() = firstGridSlotIndex + GRID_SIZE
    val playerSlotsEnd: Int get() = firstGridSlotIndex
}

const val GRID_SIZE = 4
