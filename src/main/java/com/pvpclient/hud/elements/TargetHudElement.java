package com.pvpclient.hud.elements;

import com.pvpclient.hud.HudElement;
import com.pvpclient.render.RendererUtils;
import com.pvpclient.utils.PlayerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

/**
 * Shows information about the entity the player is currently looking at:
 * name, health, distance, and armor.
 */
public final class TargetHudElement extends HudElement {

    private static final int PANEL_WIDTH  = 120;
    private static final int PANEL_HEIGHT = 52;

    public TargetHudElement() {
        super("target_hud", 0, 0); // default position set below
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        HitResult crosshairTarget = mc.crosshairTarget;
        if (crosshairTarget == null || crosshairTarget.getType() != HitResult.Type.ENTITY) return;

        if (!(((EntityHitResult) crosshairTarget).getEntity() instanceof LivingEntity target)) return;

        double dist = PlayerUtils.distanceTo(target);
        float hp    = target.getHealth();
        float maxHp = target.getMaxHealth();
        String name = target.getName().getString();

        // Panel background
        RendererUtils.drawRoundedPanel(ctx, x, y, PANEL_WIDTH, PANEL_HEIGHT,
            RendererUtils.BG_COLOR, 0x44FFFFFF);

        // Target name
        RendererUtils.drawString(ctx, name, x + 4, y + 4, RendererUtils.ACCENT_COLOR);

        // Health
        String hpText = "HP: " + (int) hp + " / " + (int) maxHp;
        RendererUtils.drawString(ctx, hpText, x + 4, y + 15, RendererUtils.HEALTH_COLOR);
        RendererUtils.drawProgressBar(ctx, x + 4, y + 25, PANEL_WIDTH - 8, 4, hp, maxHp, RendererUtils.HEALTH_COLOR);

        // Distance
        String distText = "Dist: " + String.format("%.1f", dist) + "m";
        RendererUtils.drawString(ctx, distText, x + 4, y + 32, RendererUtils.TEXT_SECONDARY);

        // Armor points (only for player targets)
        if (target instanceof PlayerEntity tp) {
            String armorText = "Armor: " + tp.getArmor();
            RendererUtils.drawString(ctx, armorText, x + 4, y + 42, RendererUtils.TEXT_SECONDARY);
        }
    }

    @Override public int getWidth()  { return PANEL_WIDTH;  }
    @Override public int getHeight() { return PANEL_HEIGHT; }
}
