package com.pvpclient.events;

import net.minecraft.client.gui.DrawContext;

/**
 * Fired during the HUD render phase.
 * Provides the DrawContext for rendering overlays.
 */
public final class RenderEvent {

    private final DrawContext context;
    private final float tickDelta;
    private final int scaledWidth;
    private final int scaledHeight;

    public RenderEvent(DrawContext context, float tickDelta, int scaledWidth, int scaledHeight) {
        this.context = context;
        this.tickDelta = tickDelta;
        this.scaledWidth = scaledWidth;
        this.scaledHeight = scaledHeight;
    }

    public DrawContext getContext() {
        return context;
    }

    public float getTickDelta() {
        return tickDelta;
    }

    public int getScaledWidth() {
        return scaledWidth;
    }

    public int getScaledHeight() {
        return scaledHeight;
    }
}
