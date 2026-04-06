package com.pvpclient.modules.pvp;

import com.pvpclient.events.EventBus;
import com.pvpclient.events.RenderEvent;
import com.pvpclient.events.TickEvent;
import com.pvpclient.modules.Module;
import com.pvpclient.render.RendererUtils;
import com.pvpclient.utils.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;

import java.util.*;

/**
 * Damage Indicators — floating numbers that rise and fade above entities when they take damage.
 * Purely visual; does not modify any game data.
 */
public final class DamageIndicatorsModule extends Module {

    private static final int MAX_INDICATORS = 20; // cap for performance

    /** A single damage number instance. */
    private static final class DamageNumber {
        final double worldX, worldY, worldZ;
        final float amount;
        long spawnTime;
        float offsetY;

        DamageNumber(double x, double y, double z, float amount) {
            this.worldX    = x;
            this.worldY    = y + 0.5;
            this.worldZ    = z;
            this.amount    = amount;
            this.spawnTime = System.currentTimeMillis();
            this.offsetY   = 0f;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - spawnTime > 1500;
        }

        float alpha() {
            long age = System.currentTimeMillis() - spawnTime;
            if (age < 800) return 1f;
            return 1f - (age - 800f) / 700f;
        }
    }

    private final List<DamageNumber> indicators = new ArrayList<>();
    /** Track previous HP per entity UUID to detect damage. */
    private final Map<UUID, Float> prevHealth = new HashMap<>();

    private final java.util.function.Consumer<TickEvent>   tickListener;
    private final java.util.function.Consumer<RenderEvent> renderListener;

    public DamageIndicatorsModule() {
        super("Damage Indicators", "Shows floating damage numbers above hurt entities.", Category.PVP);
        tickListener   = this::onTick;
        renderListener = this::onRender;
    }

    @Override
    protected void onEnable() {
        eventBus.subscribe(TickEvent.class, tickListener);
        eventBus.subscribe(RenderEvent.class, renderListener);
    }

    @Override
    protected void onDisable() {
        eventBus.unsubscribe(TickEvent.class, tickListener);
        eventBus.unsubscribe(RenderEvent.class, renderListener);
        indicators.clear();
        prevHealth.clear();
    }

    private void onTick(TickEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;

        // Scan nearby entities for health changes
        for (LivingEntity entity : mc.world.getEntitiesByClass(LivingEntity.class,
                mc.player.getBoundingBox().expand(20), e -> true)) {

            UUID id = entity.getUuid();
            float hp = entity.getHealth();
            Float prev = prevHealth.get(id);

            if (prev != null && hp < prev) {
                float damage = prev - hp;
                if (indicators.size() < MAX_INDICATORS) {
                    indicators.add(new DamageNumber(
                        entity.getX(), entity.getY() + entity.getHeight(), entity.getZ(), damage));
                }
            }
            prevHealth.put(id, hp);
        }

        // Remove dead entities from tracking
        prevHealth.entrySet().removeIf(e -> mc.world.getEntityById(
            mc.world.getEntities().stream().filter(ent -> ent.getUuid().equals(e.getKey()))
                   .mapToInt(ent -> ent.getId()).findFirst().orElse(-1)) == null);
    }

    private void onRender(RenderEvent event) {
        indicators.removeIf(DamageNumber::isExpired);

        // Project world positions to screen (simplified; uses camera projection)
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        DrawContext ctx = event.getContext();

        for (DamageNumber dn : indicators) {
            float alpha = dn.alpha();
            if (alpha <= 0f) continue;

            // Rise over time
            dn.offsetY += 0.3f;

            // Project entity world position to screen (2D approximation)
            int[] screenPos = worldToScreen(dn.worldX, dn.worldY + dn.offsetY * 0.02, dn.worldZ,
                mc, event.getScaledWidth(), event.getScaledHeight());

            if (screenPos == null) continue;

            int a = (int) (alpha * 255);
            int color = ((a & 0xFF) << 24) | 0xFF4444; // Red-ish damage color, semi-transparent

            String dmgText = "-" + String.format("%.1f", dn.amount);
            RendererUtils.drawCenteredString(ctx, dmgText, screenPos[0], screenPos[1], color);
        }
    }

    /**
     * Very simplified world-to-screen projection.
     * Returns null if the point is behind the camera.
     */
    private int[] worldToScreen(double wx, double wy, double wz,
                                 MinecraftClient mc, int screenW, int screenH) {
        net.minecraft.client.render.Camera camera = mc.gameRenderer.getCamera();
        net.minecraft.util.math.Vec3d camPos = camera.getPos();

        double dx = wx - camPos.x;
        double dy = wy - camPos.y;
        double dz = wz - camPos.z;

        // Rotate into camera space using yaw and pitch
        float yaw   = (float) Math.toRadians(camera.getYaw());
        float pitch = (float) Math.toRadians(camera.getPitch());

        double cosYaw   = Math.cos(yaw);
        double sinYaw   = Math.sin(yaw);
        double cosPitch = Math.cos(pitch);
        double sinPitch = Math.sin(pitch);

        double camX =  dx * cosYaw - dz * sinYaw;
        double camZ =  dx * sinYaw + dz * cosYaw;
        double camY =  dy * cosPitch - camZ * sinPitch;
        double camW =  dy * sinPitch + camZ * cosPitch;

        if (camW <= 0.1) return null; // behind the camera

        double fov = Math.toRadians(mc.options.getFov().getValue());
        double aspectRatio = (double) screenW / screenH;

        int sx = (int) (screenW / 2 + (camX / (camW * Math.tan(fov / 2))) * screenW / 2);
        int sy = (int) (screenH / 2 - (camY / (camW * Math.tan(fov / 2) / aspectRatio)) * screenH / 2);

        // Reject out-of-view positions
        if (sx < 0 || sx > screenW || sy < 0 || sy > screenH) return null;

        return new int[]{sx, sy};
    }
}
