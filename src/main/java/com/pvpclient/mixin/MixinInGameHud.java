package com.pvpclient.mixin;

import com.pvpclient.modules.ModuleManager;
import com.pvpclient.modules.pvp.CrosshairEditorModule;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Suppresses the vanilla crosshair when our CrosshairEditorModule is active.
 */
@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshair(DrawContext context, float tickDelta, CallbackInfo ci) {
        CrosshairEditorModule module = ModuleManager.getInstance().getModule(CrosshairEditorModule.class);
        if (module != null && module.isEnabled()) {
            ci.cancel(); // Suppress the vanilla crosshair; our module draws a custom one
        }
    }
}
