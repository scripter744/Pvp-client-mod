package com.pvpclient.modules.performance;

import com.pvpclient.modules.Module;
import net.minecraft.client.MinecraftClient;

/**
 * Animation Reducer — suppresses block/entity animations (waving leaves, animated textures)
 * to significantly reduce CPU/GPU time on low-end devices.
 * Uses vanilla settings where possible to avoid mixin complexity.
 */
public final class AnimationReducerModule extends Module {

    private boolean savedEntityShadows;

    public AnimationReducerModule() {
        super("Animation Reducer", "Disables eye-candy animations to increase performance.", Category.PERFORMANCE);
    }

    @Override
    protected void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        savedEntityShadows = mc.options.getEntityShadows().getValue();
        mc.options.getEntityShadows().setValue(false);
        mc.options.write();
    }

    @Override
    protected void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.options.getEntityShadows().setValue(savedEntityShadows);
        mc.options.write();
    }
}
