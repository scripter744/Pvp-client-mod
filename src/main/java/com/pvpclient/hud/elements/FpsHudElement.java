package com.pvpclient.hud.elements;

import com.pvpclient.hud.HudElement;
import com.pvpclient.render.RendererUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Displays the current client FPS.
 * Reads directly from MinecraftClient.currentFps to avoid any computation overhead.
 */
public final class FpsHudElement extends HudElement {

    /** Cached FPS value updated every second to reduce string allocations. */
    private int cachedFps;
    private long lastUpdateMs;

    public FpsHudElement() {
        super("fps_counter", 2, 2);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        long now = System.currentTimeMillis();
        // Update FPS cache at most once per 200ms
        if (now - lastUpdateMs >= 200) {
            cachedFps = MinecraftClient.getCurrentFps();
            lastUpdateMs = now;
        }

        String text = "FPS: " + cachedFps;
        int color = fpsColor(cachedFps);

        // Background panel
        RendererUtils.drawRect(ctx, x - 2, y - 1, getWidth() + 4, getHeight() + 2, RendererUtils.BG_COLOR);
        RendererUtils.drawString(ctx, text, x, y, color);
    }

    @Override
    public int getWidth() {
        return 60;
    }

    @Override
    public int getHeight() {
        return 10;
    }

    /** Returns green for high FPS, yellow for medium, red for low. */
    private int fpsColor(int fps) {
        if (fps >= 60) return 0xFF44DD44;
        if (fps >= 30) return 0xFFFFAA00;
        return 0xFFFF4444;
    }
}
