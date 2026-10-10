package com.containercrafting.client.mixin

import com.containercrafting.GridLogic
import com.containercrafting.PanelLayout
import com.containercrafting.client.PanelTexture
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderPipelines
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * Draws the panel and keeps clicks on it from counting as "outside the GUI" (which drops the held item).
 * Both hooks are gated on the menu actually having a grid, so ContainerScreen, ShulkerBoxScreen and HopperScreen
 * are covered here and every other container screen is untouched.
 */
@Mixin(AbstractContainerScreen::class)
abstract class AbstractContainerScreenMixin {
    // Drawn right before the slots, inside the pose already translated to (leftPos, topPos).
    @Inject(method = ["extractSlots"], at = [At("HEAD")])
    private fun containercrafting_drawPanel(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, ci: CallbackInfo) {
        val state = GridLogic.stateOf(screen().getMenu()) ?: return
        graphics.blit(
            RenderPipelines.GUI_TEXTURED, PanelTexture.ID,
            PanelLayout.PANEL_X, state.panelY, 0.0F, 0.0F,
            PanelLayout.WIDTH, PanelLayout.HEIGHT, PanelLayout.WIDTH, PanelLayout.HEIGHT,
        )
    }

    @Inject(method = ["hasClickedOutside"], at = [At("HEAD")], cancellable = true)
    private fun containercrafting_clickInPanel(
        mx: Double, my: Double, xo: Int, yo: Int, cir: CallbackInfoReturnable<Boolean>,
    ) {
        val state = GridLogic.stateOf(screen().getMenu()) ?: return
        val left = xo + PanelLayout.PANEL_X
        val top = yo + state.panelY
        if (mx >= left && mx < left + PanelLayout.WIDTH && my >= top && my < top + PanelLayout.HEIGHT) {
            cir.returnValue = false
        }
    }

    private fun screen(): AbstractContainerScreen<*> = (this as Any) as AbstractContainerScreen<*>
}
