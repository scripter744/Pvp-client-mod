package com.pvpclient.mixin;

import com.pvpclient.events.EventBus;
import com.pvpclient.events.RenderEvent;
import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.ModuleManager;
import com.pvpclient.modules.pvp.CrosshairEditorModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks into the game renderer to dispatch RenderEvents for HUD and overlay modules.
 */
@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    /** Inject after the vanilla HUD is rendered to draw our custom HUD on top. */
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.currentScreen != null) return;

        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();

        // We need a DrawContext; create a fresh one for HUD rendering
        DrawContext ctx = new DrawContext(mc, mc.getBufferBuilders().getEntityVertexConsumers());

        RenderEvent event = new RenderEvent(ctx, tickDelta, sw, sh);
        EventBus.getInstance().post(event);

        // Render all HUD elements
        HudManager.getInstance().render(ctx, tickDelta);

        ctx.draw();
    }
}
