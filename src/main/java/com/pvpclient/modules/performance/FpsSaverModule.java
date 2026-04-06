package com.pvpclient.modules.performance;

import com.pvpclient.events.EventBus;
import com.pvpclient.events.TickEvent;
import com.pvpclient.modules.Module;
import net.minecraft.client.MinecraftClient;

/**
 * FPS Saver — monitors frame rate and automatically lowers render distance and
 * particle quality when FPS drops below a configurable threshold.
 * Settings are restored when FPS recovers.
 */
public final class FpsSaverModule extends Module {

    /** FPS threshold at which performance mode kicks in. */
    private static final int FPS_THRESHOLD_LOW    = 30;
    private static final int FPS_THRESHOLD_RECOVER = 50;

    private boolean performanceModeActive = false;
    private int savedRenderDistance = 8;

    private long lastCheckMs = 0;
    private static final long CHECK_INTERVAL_MS = 2000; // check every 2 seconds

    private final java.util.function.Consumer<TickEvent> tickListener;

    public FpsSaverModule() {
        super("FPS Saver", "Auto-lowers settings when FPS drops to keep the game playable.", Category.PERFORMANCE);
        tickListener = this::onTick;
    }

    @Override
    protected void onEnable() {
        eventBus.subscribe(TickEvent.class, tickListener);
    }

    @Override
    protected void onDisable() {
        eventBus.unsubscribe(TickEvent.class, tickListener);
        // Restore settings if we left performance mode active
        if (performanceModeActive) {
            restoreSettings();
        }
    }

    private void onTick(TickEvent event) {
        long now = System.currentTimeMillis();
        if (now - lastCheckMs < CHECK_INTERVAL_MS) return;
        lastCheckMs = now;

        int fps = MinecraftClient.getCurrentFps();
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        if (!performanceModeActive && fps < FPS_THRESHOLD_LOW) {
            applyPerformanceSettings(mc);
        } else if (performanceModeActive && fps >= FPS_THRESHOLD_RECOVER) {
            restoreSettings();
        }
    }

    private void applyPerformanceSettings(MinecraftClient mc) {
        performanceModeActive = true;
        savedRenderDistance = mc.options.getViewDistance().getValue();

        // Halve render distance, minimum 2
        int newDist = Math.max(2, savedRenderDistance / 2);
        mc.options.getViewDistance().setValue(newDist);
        mc.options.getParticles().setValue(net.minecraft.client.option.ParticlesMode.MINIMAL);
        mc.options.write();
    }

    private void restoreSettings() {
        performanceModeActive = false;
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.options.getViewDistance().setValue(savedRenderDistance);
        mc.options.getParticles().setValue(net.minecraft.client.option.ParticlesMode.ALL);
        mc.options.write();
    }
}
