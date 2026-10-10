package com.containercrafting.client

import net.minecraft.resources.Identifier

/** Lives outside the mixin package because mixin classes may not hold static members. */
object PanelTexture {
    val ID: Identifier = Identifier.fromNamespaceAndPath("containercrafting", "textures/gui/crafting_panel.png")
}
