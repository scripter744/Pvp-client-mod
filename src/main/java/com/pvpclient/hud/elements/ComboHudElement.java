package com.pvpclient.hud.elements;

import com.pvpclient.events.AttackEvent;
import com.pvpclient.events.EventBus;
import com.pvpclient.hud.HudElement;
import com.pvpclient.render.RendererUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;

/**
 * Tracks and displays the consecutive hit combo count.
 * Combo resets if the player doesn't land a hit within 3 seconds.
 */
public final class ComboHudElement extends HudElement {

    private int combo = 0;
    private long lastHitTime = 0;
    private static final long COMBO_TIMEOUT_MS = 3000L;

    /** Fade-out animation for the combo number (0 = invisible, 1 = fully opaque). */
    private float alpha = 0f;

    public ComboHudElement() {
        super("combo_counter", 0, 0); // will be centered on first render
        EventBus.getInstance().subscribe(AttackEvent.class, this::onAttack);
    }

    private void onAttack(AttackEvent event) {
        if (!(event.getTarget() instanceof LivingEntity)) return;
        combo++;
        lastHitTime = System.currentTimeMillis();
        alpha = 1f;
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        long now = System.currentTimeMillis();

        // Reset combo after timeout
        if (combo > 0 && now - lastHitTime > COMBO_TIMEOUT_MS) {
            combo = 0;
        }

        // Fade out over the last 1 second of the timeout
        if (combo > 0) {
            long timeSince = now - lastHitTime;
            if (timeSince > COMBO_TIMEOUT_MS - 1000) {
                alpha = 1f - (float) (timeSince - (COMBO_TIMEOUT_MS - 1000)) / 1000f;
            } else {
                alpha = 1f;
            }
        } else {
            alpha = 0f;
        }

        if (alpha <= 0f || combo <= 1) return;

        int a = (int) (alpha * 255);
        int color = ((a & 0xFF) << 24) | 0x00E5CC; // ARGB: teal

        String text = combo + " Combo!";
        RendererUtils.drawRect(ctx, x - 4, y - 2, getWidth() + 8, getHeight() + 4,
            ((a / 2) << 24));
        RendererUtils.drawCenteredString(ctx, text, x + getWidth() / 2, y, color);
    }

    @Override public int getWidth()  { return 70; }
    @Override public int getHeight() { return 10; }
}
