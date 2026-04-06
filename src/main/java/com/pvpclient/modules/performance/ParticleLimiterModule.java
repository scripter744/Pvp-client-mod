package com.pvpclient.modules.performance;

import com.pvpclient.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ParticlesMode;

/**
 * Particle Limiter — automatically sets particle quality to DECREASED or MINIMAL.
 * Restores original setting when disabled.
 */
public final class ParticleLimiterModule extends Module {

    private ParticlesMode savedMode;

    public ParticleLimiterModule() {
        super("Particle Limiter", "Reduces particle effects to save GPU processing time.", Category.PERFORMANCE);
    }

    @Override
    protected void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        savedMode = mc.options.getParticles().getValue();
        mc.options.getParticles().setValue(ParticlesMode.DECREASED);
        mc.options.write();
    }

    @Override
    protected void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (savedMode != null) {
            mc.options.getParticles().setValue(savedMode);
            mc.options.write();
        }
    }
}
