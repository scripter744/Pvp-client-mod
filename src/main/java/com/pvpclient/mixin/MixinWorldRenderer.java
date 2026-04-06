package com.pvpclient.mixin;

import com.pvpclient.modules.ModuleManager;
import com.pvpclient.modules.performance.EntityCullingModule;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels render calls for entities marked as culled by EntityCullingModule.
 */
@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    private void onRenderEntity(Entity entity, double cameraX, double cameraY, double cameraZ,
                                 float tickDelta, Object vertexConsumerProvider,
                                 Object outlineVertexConsumerProvider, CallbackInfo ci) {
        EntityCullingModule module = ModuleManager.getInstance().getModule(EntityCullingModule.class);
        if (module != null && module.isEnabled()) {
            if (EntityCullingModule.culledEntities.contains(entity.getUuid())) {
                ci.cancel();
            }
        }
    }
}
