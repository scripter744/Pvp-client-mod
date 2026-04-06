package com.pvpclient.hud.elements;

import com.pvpclient.hud.HudElement;
import com.pvpclient.render.RendererUtils;
import com.pvpclient.utils.PlayerUtils;
import net.minecraft.client.gui.DrawContext;

/**
 * Displays the player's current health as a number and a red progress bar.
 */
public final class HealthHudElement extends HudElement {

    public HealthHudElement() {
        super("health_hud", 2, 70);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        float health    = PlayerUtils.getHealth();
        float maxHealth = PlayerUtils.getMaxHealth();

        String text = "HP: " + (int) health + " / " + (int) maxHealth;

        RendererUtils.drawRect(ctx, x - 2, y - 1, getWidth() + 4, getHeight() + 2, RendererUtils.BG_COLOR);
        RendererUtils.drawString(ctx, text, x, y, RendererUtils.HEALTH_COLOR);
        RendererUtils.drawProgressBar(ctx, x, y + 11, 80, 4, health, maxHealth, RendererUtils.HEALTH_COLOR);
    }

    @Override public int getWidth()  { return 84; }
    @Override public int getHeight() { return 16; }
}
