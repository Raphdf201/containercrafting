package com.containercrafting

/**
 * Geometry of the crafting panel. Menu slots and the client texture share these numbers so they cannot drift.
 * All x/y values are relative to the container GUI's top-left corner (so x is negative: the panel sits to the left).
 */
object PanelLayout {
    const val WIDTH = 84
    const val HEIGHT = 62
    private const val GAP = 2

    /** Left edge of the panel. */
    const val PANEL_X = -(WIDTH + GAP)

    // Offsets of slot *item* origins inside the panel (the slot background starts 1px earlier).
    private const val GRID_ITEM_X = 6
    private const val GRID_ITEM_Y = 14
    private const val RESULT_ITEM_X = 63
    private const val RESULT_ITEM_Y = 23

    fun panelY(guiHeight: Int): Int = (guiHeight - HEIGHT) / 2

    fun gridSlotX(column: Int): Int = PANEL_X + GRID_ITEM_X + column * 18
    fun gridSlotY(panelY: Int, row: Int): Int = panelY + GRID_ITEM_Y + row * 18
    fun resultSlotX(): Int = PANEL_X + RESULT_ITEM_X
    fun resultSlotY(panelY: Int): Int = panelY + RESULT_ITEM_Y
}
