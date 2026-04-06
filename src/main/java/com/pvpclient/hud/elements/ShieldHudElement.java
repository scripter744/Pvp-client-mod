package com.pvpclient.hud.elements;

import com.pvpclient.hud.HudElement;
import com.pvpclient.render.RendererUtils;
import com.pvpclient.utils.PlayerUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Shows whether the shield is raised (blocking) and its cooldown after being disabled.
 */
public final class ShieldHudElement extends HudElement {

    public ShieldHudElement() {
        super("shield_status", 2, 120);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        ItemStack shield = PlayerUtils.getShieldStack();
        if (shield.isEmpty()) return; // No shield in hand — nothing to show

        boolean raised = PlayerUtils.isShieldRaised();
        String statusText = raised ? "SHIELD: UP" : "SHIELD: DOWN";
        int statusColor = raised ? 0xFF44DD44 : 0xFFAAAAAA;

        RendererUtils.drawRect(ctx, x - 2, y - 1, getWidth() + 4, getHeight() + 2, RendererUtils.BG_COLOR);
        RendererUtils.drawString(ctx, statusText, x, y, statusColor);

        // Show item icon
        ctx.drawItem(shield, x + 80, y - 3);

        // Cooldown bar when shield is on cooldown
        ClientPlayerEntity player = PlayerUtils.getPlayer();
        if (player != null) {
            float cooldown = player.getItemCooldownManager().getCooldownProgress(Items.SHIELD, delta);
            if (cooldown > 0f) {
                RendererUtils.drawProgressBar(ctx, x, y + 11, 80, 4, 1f - cooldown, 1f, 0xFFFF4444);
                RendererUtils.drawString(ctx, "Cooldown", x, y + 16, RendererUtils.TEXT_SECONDARY);
            }
        }
    }

    @Override public int getWidth()  { return 100; }
    @Override public int getHeight() { return 26; }
}
