package com.pvpclient.hud.elements;

import com.pvpclient.hud.HudElement;
import com.pvpclient.render.RendererUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Displays the player's current attack reach (visual only — reflects vanilla value).
 */
public final class ReachHudElement extends HudElement {

    public ReachHudElement() {
        super("reach_display", 2, 62);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        // Vanilla 1.21 default reach in Creative = 5.0, Survival = 3.0
        double reach = mc.player.isCreative() ? 5.0 : 3.0;
        String text = "Reach: " + String.format("%.1f", reach) + "b";

        RendererUtils.drawRect(ctx, x - 2, y - 1, getWidth() + 4, getHeight() + 2, RendererUtils.BG_COLOR);
        RendererUtils.drawString(ctx, text, x, y, RendererUtils.TEXT_COLOR);
    }

    @Override public int getWidth()  { return 70; }
    @Override public int getHeight() { return 10; }
}
