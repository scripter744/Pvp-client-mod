package com.pvpclient.hud.elements;

import com.pvpclient.hud.HudElement;
import com.pvpclient.render.RendererUtils;
import com.pvpclient.utils.PlayerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Shows the player's current XYZ block coordinates.
 */
public final class CoordinatesHudElement extends HudElement {

    public CoordinatesHudElement() {
        super("coordinates", 2, 26);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        ClientPlayerEntity player = PlayerUtils.getPlayer();
        if (player == null) return;

        int bx = (int) Math.floor(player.getX());
        int by = (int) Math.floor(player.getY());
        int bz = (int) Math.floor(player.getZ());

        String text = "XYZ: " + bx + " / " + by + " / " + bz;

        RendererUtils.drawRect(ctx, x - 2, y - 1, getWidth() + 4, getHeight() + 2, RendererUtils.BG_COLOR);
        RendererUtils.drawString(ctx, text, x, y, RendererUtils.TEXT_COLOR);
    }

    @Override public int getWidth() { return 140; }
    @Override public int getHeight() { return 10; }
}
