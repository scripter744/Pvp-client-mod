package com.pvpclient.hud.elements;

import com.pvpclient.hud.HudElement;
import com.pvpclient.render.RendererUtils;
import com.pvpclient.utils.PlayerUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 * Displays each armor piece with its durability percentage and a colored bar.
 * Pieces are shown top-to-bottom: helmet, chestplate, leggings, boots.
 */
public final class ArmorHudElement extends HudElement {

    private static final int PIECE_HEIGHT = 14;
    private static final int PIECE_GAP    = 2;
    private static final int BAR_WIDTH    = 40;
    private static final int BAR_HEIGHT   = 3;

    public ArmorHudElement() {
        super("armor_hud", 2, 100);
    }

    @Override
    public void render(DrawContext ctx, float delta) {
        // Slot indices: 3=helmet, 2=chestplate, 1=leggings, 0=boots
        for (int slot = 3; slot >= 0; slot--) {
            ItemStack stack = PlayerUtils.getArmorPiece(slot);
            if (stack.isEmpty()) continue;

            int ry = y + (3 - slot) * (PIECE_HEIGHT + PIECE_GAP);

            // Background panel
            RendererUtils.drawRect(ctx, x - 2, ry - 1, getWidth() + 4, PIECE_HEIGHT + 2, RendererUtils.BG_COLOR);

            // Item icon (16×16)
            ctx.drawItem(stack, x, ry - 3);

            // Durability text
            if (stack.isDamageable()) {
                float durFraction = 1f - (float) stack.getDamage() / stack.getMaxDamage();
                int pct = (int) (durFraction * 100);
                String durText = pct + "%";
                RendererUtils.drawString(ctx, durText, x + 18, ry, durabilityColor(pct));

                // Durability bar below text
                RendererUtils.drawProgressBar(ctx, x + 18, ry + 9, BAR_WIDTH, BAR_HEIGHT, durFraction, 1f);
            } else {
                RendererUtils.drawString(ctx, "Unbreakable", x + 18, ry, RendererUtils.TEXT_SECONDARY);
            }
        }
    }

    private int durabilityColor(int pct) {
        if (pct > 50) return 0xFF44DD44;
        if (pct > 25) return 0xFFFFAA00;
        return 0xFFFF4444;
    }

    @Override public int getWidth()  { return 65; }
    @Override public int getHeight() { return 4 * (PIECE_HEIGHT + PIECE_GAP) - PIECE_GAP; }
}
