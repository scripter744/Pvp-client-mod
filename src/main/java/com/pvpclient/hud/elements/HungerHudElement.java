package com.pvpclient.hud.elements;

import com.pvpclient.hud.HudElement;
import com.pvpclient.render.RendererUtils;
import com.pvpclient.utils.PlayerUtils;
import net.minecraft.client.gui.DrawContext;

/**
 * Displays the player's hunger level with a yellow bar.
 */
public final class HungerHudElement extends HudElement {

    public HungerHudElement() {
        super("hunger_hud", 2, 90);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        int food = PlayerUtils.getFoodLevel();
        String text = "Food: " + food + " / 20";

        RendererUtils.drawRect(ctx, x - 2, y - 1, getWidth() + 4, getHeight() + 2, RendererUtils.BG_COLOR);
        RendererUtils.drawString(ctx, text, x, y, RendererUtils.HUNGER_COLOR);
        RendererUtils.drawProgressBar(ctx, x, y + 11, 80, 4, food, 20, RendererUtils.HUNGER_COLOR);
    }

    @Override public int getWidth()  { return 84; }
    @Override public int getHeight() { return 16; }
}
