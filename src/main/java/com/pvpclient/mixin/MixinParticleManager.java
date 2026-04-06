package com.pvpclient.mixin;

import com.pvpclient.modules.ModuleManager;
import com.pvpclient.modules.performance.ParticleLimiterModule;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Limits particle spawning when ParticleLimiterModule is active.
 * Only allows every other particle call through when in reduction mode.
 */
@Mixin(ParticleManager.class)
public abstract class MixinParticleManager {

    private int particleTickCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onParticleTick(CallbackInfo ci) {
        ParticleLimiterModule module = ModuleManager.getInstance().getModule(ParticleLimiterModule.class);
        if (module != null && module.isEnabled()) {
            particleTickCounter++;
            // Allow only every 2nd particle tick (halves particle CPU cost)
            if ((particleTickCounter & 1) == 1) {
                ci.cancel();
            }
        }
    }
}
