package com.pvpclient.modules.performance;

import com.pvpclient.events.EventBus;
import com.pvpclient.events.TickEvent;
import com.pvpclient.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

/**
 * Entity Culling — skips rendering entities that are not visible to the camera (frustum culling).
 * Works in conjunction with MixinWorldRenderer to suppress invisible entity draw calls.
 * Safe and server-transparent: the server is never notified of entity visibility state.
 */
public final class EntityCullingModule extends Module {

    /** Maximum entities to process per tick to avoid lag spikes. */
    private static final int MAX_PROCESS_PER_TICK = 64;

    /** Entities marked as currently off-screen. Accessed by mixin. */
    public static final Set<UUID> culledEntities = new HashSet<>();

    private final java.util.function.Consumer<TickEvent> tickListener;

    public EntityCullingModule() {
        super("Entity Culling", "Skips rendering entities outside camera view to boost FPS.", Category.PERFORMANCE);
        tickListener = this::onTick;
    }

    @Override
    protected void onEnable() {
        eventBus.subscribe(TickEvent.class, tickListener);
    }

    @Override
    protected void onDisable() {
        eventBus.unsubscribe(TickEvent.class, tickListener);
        culledEntities.clear();
    }

    private void onTick(TickEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null || mc.gameRenderer == null) return;

        culledEntities.clear();

        var camera = mc.gameRenderer.getCamera();
        float yaw   = camera.getYaw();
        float pitch = camera.getPitch();

        int count = 0;
        for (Entity entity : mc.world.getEntities()) {
            if (count++ >= MAX_PROCESS_PER_TICK) break;
            if (entity == mc.player) continue;

            // Compute angle between camera forward and entity direction
            double dx = entity.getX() - camera.getPos().x;
            double dy = entity.getY() - camera.getPos().y;
            double dz = entity.getZ() - camera.getPos().z;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist < 2.0) continue; // Too close — always render

            // Yaw-axis dot product check (horizontal)
            double fwdX = -MathHelper.sin(yaw * MathHelper.RADIANS_PER_DEGREE);
            double fwdZ =  MathHelper.cos(yaw * MathHelper.RADIANS_PER_DEGREE);
            double dot  = (dx / dist) * fwdX + (dz / dist) * fwdZ;

            // If entity is behind (dot < -0.3) and far enough, cull it
            if (dot < -0.3 && dist > 8.0) {
                culledEntities.add(entity.getUuid());
            }
        }
    }
}
