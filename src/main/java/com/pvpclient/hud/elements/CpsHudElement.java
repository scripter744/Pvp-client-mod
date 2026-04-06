package com.pvpclient.hud.elements;

import com.pvpclient.events.EventBus;
import com.pvpclient.events.InputEvent;
import com.pvpclient.hud.HudElement;
import com.pvpclient.render.RendererUtils;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Counts left-click (LMB) and right-click (RMB) clicks per second separately.
 * Uses a sliding window over the last 1000ms to compute accurate CPS.
 */
public final class CpsHudElement extends HudElement {

    /** Timestamps of recent LMB clicks within the last second. */
    private final Deque<Long> lmbClicks = new ArrayDeque<>();
    /** Timestamps of recent RMB clicks within the last second. */
    private final Deque<Long> rmbClicks = new ArrayDeque<>();

    private final java.util.function.Consumer<InputEvent> inputListener;

    public CpsHudElement() {
        super("cps_counter", 2, 50);
        inputListener = this::onInput;
        EventBus.getInstance().subscribe(InputEvent.class, inputListener);
    }

    private void onInput(InputEvent event) {
        if (!event.isPressed()) return;
        long now = System.currentTimeMillis();
        if (event.getButton() == InputEvent.Button.LEFT) {
            lmbClicks.addLast(now);
        } else if (event.getButton() == InputEvent.Button.RIGHT) {
            rmbClicks.addLast(now);
        }
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        long now = System.currentTimeMillis();
        pruneOld(lmbClicks, now);
        pruneOld(rmbClicks, now);

        String text = "LMB: " + lmbClicks.size() + " | RMB: " + rmbClicks.size();

        RendererUtils.drawRect(ctx, x - 2, y - 1, getWidth() + 4, getHeight() + 2, RendererUtils.BG_COLOR);
        RendererUtils.drawString(ctx, text, x, y, RendererUtils.TEXT_COLOR);
    }

    /** Remove clicks older than 1 second from the front of the deque. */
    private void pruneOld(Deque<Long> deque, long now) {
        while (!deque.isEmpty() && now - deque.peekFirst() > 1000) {
            deque.pollFirst();
        }
    }

    @Override public int getWidth() { return 110; }
    @Override public int getHeight() { return 10; }
}
