package com.pvpclient.modules.pvp;

import com.pvpclient.events.EventBus;
import com.pvpclient.events.RenderEvent;
import com.pvpclient.modules.Module;
import com.pvpclient.render.RendererUtils;
import com.pvpclient.utils.PlayerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Attack Cooldown Indicator — draws a clean arc/bar showing the 1.9+ attack cooldown.
 * Replaces the vanilla crosshair indicator with a more visible custom one.
 */
public final class AttackIndicatorModule extends Module {

    private final java.util.function.Consumer<RenderEvent> renderListener;

    public AttackIndicatorModule() {
        super("Attack Indicator", "Shows a visual attack cooldown bar for 1.9+ combat.", Category.PVP);
        renderListener = this::onRender;
    }

    @Override
    protected void onEnable() {
        eventBus.subscribe(RenderEvent.class, renderListener);
    }

    @Override
    protected void onDisable() {
        eventBus.unsubscribe(RenderEvent.class, renderListener);
    }

    private void onRender(RenderEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        float cooldown = PlayerUtils.getAttackCooldown(); // 0 = just attacked, 1 = ready
        DrawContext ctx = event.getContext();
        int cx = event.getScaledWidth() / 2;
        int cy = event.getScaledHeight() / 2;

        // Draw a small bar just below the crosshair
        int barW = 30;
        int barH = 3;
        int bx   = cx - barW / 2;
        int by   = cy + 8;

        // Background
        RendererUtils.drawRect(ctx, bx - 1, by - 1, barW + 2, barH + 2, 0x88000000);

        // Filled portion — transitions from red → yellow → green
        int barColor;
        if (cooldown >= 1f) {
            barColor = 0xFF44DD44;
        } else if (cooldown >= 0.5f) {
            barColor = RendererUtils.HUNGER_COLOR;
        } else {
            barColor = 0xFFFF4444;
        }

        RendererUtils.drawProgressBar(ctx, bx, by, barW, barH, cooldown, 1f, barColor);
    }
}
