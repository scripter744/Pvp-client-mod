package com.pvpclient.hud.elements;

import com.pvpclient.hud.HudElement;
import com.pvpclient.render.RendererUtils;
import com.pvpclient.utils.MathUtils;
import com.pvpclient.utils.PlayerUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Shows the cardinal/intercardinal direction the player is facing (N, NE, E, …).
 */
public final class DirectionHudElement extends HudElement {

    public DirectionHudElement() {
        super("direction", 2, 38);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        ClientPlayerEntity player = PlayerUtils.getPlayer();
        if (player == null) return;

        String dir = MathUtils.yawToDirection(player.getYaw());
        String text = "Facing: " + dir;

        RendererUtils.drawRect(ctx, x - 2, y - 1, getWidth() + 4, getHeight() + 2, RendererUtils.BG_COLOR);
        RendererUtils.drawString(ctx, text, x, y, RendererUtils.ACCENT_COLOR);
    }

    @Override public int getWidth() { return 80; }
    @Override public int getHeight() { return 10; }
}
