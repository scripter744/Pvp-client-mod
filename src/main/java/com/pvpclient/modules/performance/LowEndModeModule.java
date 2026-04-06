package com.pvpclient.modules.performance;

import com.pvpclient.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ParticlesMode;

/**
 * Low-End Mode — applies a comprehensive set of quality reductions specifically
 * designed for mobile launchers (Zalith / PojavLauncher) and low-spec devices.
 *
 * Changes applied:
 * - Render distance → 4 chunks
 * - Particles → MINIMAL
 * - Entity shadows → OFF
 * - Mipmap levels → 0
 * - Smooth lighting → OFF
 * - Entity distance → 50%
 */
public final class LowEndModeModule extends Module {

    private int savedRenderDistance;
    private ParticlesMode savedParticles;
    private boolean savedEntityShadows;
    private int savedMipmapLevels;
    private boolean savedSmoothLighting;
    private double savedEntityDistance;

    public LowEndModeModule() {
        super("Low-End Mode", "Applies maximum optimizations for mobile and low-spec devices.", Category.PERFORMANCE);
    }

    @Override
    protected void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();

        savedRenderDistance  = mc.options.getViewDistance().getValue();
        savedParticles       = mc.options.getParticles().getValue();
        savedEntityShadows   = mc.options.getEntityShadows().getValue();
        savedMipmapLevels    = mc.options.getMipmapLevels().getValue();
        savedSmoothLighting  = mc.options.getSmoothLighting().getValue();
        savedEntityDistance  = mc.options.getEntityDistanceScaling().getValue();

        mc.options.getViewDistance().setValue(4);
        mc.options.getParticles().setValue(ParticlesMode.MINIMAL);
        mc.options.getEntityShadows().setValue(false);
        mc.options.getMipmapLevels().setValue(0);
        mc.options.getSmoothLighting().setValue(false);
        mc.options.getEntityDistanceScaling().setValue(0.5);
        mc.options.write();

        // Force a texture reload for mipmap changes
        mc.reloadResources();
    }

    @Override
    protected void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();

        mc.options.getViewDistance().setValue(savedRenderDistance);
        mc.options.getParticles().setValue(savedParticles);
        mc.options.getEntityShadows().setValue(savedEntityShadows);
        mc.options.getMipmapLevels().setValue(savedMipmapLevels);
        mc.options.getSmoothLighting().setValue(savedSmoothLighting);
        mc.options.getEntityDistanceScaling().setValue(savedEntityDistance);
        mc.options.write();
        mc.reloadResources();
    }
}
