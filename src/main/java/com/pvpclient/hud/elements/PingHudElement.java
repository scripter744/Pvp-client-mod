package com.pvpclient.hud.elements;

import com.pvpclient.hud.HudElement;
import com.pvpclient.render.RendererUtils;
import com.pvpclient.utils.PlayerUtils;
import net.minecraft.client.gui.DrawContext;

/**
 * Displays server ping in milliseconds with color coding.
 */
public final class PingHudElement extends HudElement {

    private int cachedPing;
    private long lastUpdateMs;

    public PingHudElement() {
        super("ping_display", 2, 14);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        long now = System.currentTimeMillis();
        if (now - lastUpdateMs >= 1000) {
            cachedPing = PlayerUtils.getPing();
            lastUpdateMs = now;
        }

        String text = cachedPing < 0 ? "Ping: N/A" : "Ping: " + cachedPing + "ms";
        int color = pingColor(cachedPing);

        RendererUtils.drawRect(ctx, x - 2, y - 1, getWidth() + 4, getHeight() + 2, RendererUtils.BG_COLOR);
        RendererUtils.drawString(ctx, text, x, y, color);
    }

    @Override public int getWidth() { return 72; }
    @Override public int getHeight() { return 10; }

    private int pingColor(int ping) {
        if (ping < 0) return RendererUtils.TEXT_SECONDARY;
        if (ping <= 80) return 0xFF44DD44;
        if (ping <= 150) return 0xFFFFAA00;
        return 0xFFFF4444;
    }
}
