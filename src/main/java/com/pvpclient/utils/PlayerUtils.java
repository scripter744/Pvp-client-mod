package com.pvpclient.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;

/**
 * Utility helpers for reading player state.
 * All methods gracefully return sensible defaults when the player is null.
 */
public final class PlayerUtils {

    private PlayerUtils() {}

    /** Returns the local player, or null if not in-game. */
    public static ClientPlayerEntity getPlayer() {
        return MinecraftClient.getInstance().player;
    }

    /** Returns the player's health as a float in [0, maxHealth]. */
    public static float getHealth() {
        ClientPlayerEntity player = getPlayer();
        return player != null ? player.getHealth() : 0f;
    }

    /** Returns the player's maximum health. */
    public static float getMaxHealth() {
        ClientPlayerEntity player = getPlayer();
        return player != null ? player.getMaxHealth() : 20f;
    }

    /** Returns the player's food (hunger) level in [0, 20]. */
    public static int getFoodLevel() {
        ClientPlayerEntity player = getPlayer();
        return player != null ? player.getHungerManager().getFoodLevel() : 20;
    }

    /**
     * Returns the estimated server-side ping from the player list, or -1 if unavailable.
     */
    public static int getPing() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.getNetworkHandler() == null) return -1;
        var entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return entry != null ? entry.getLatency() : -1;
    }

    /**
     * Returns whether the player's shield is currently raised (blocking).
     */
    public static boolean isShieldRaised() {
        ClientPlayerEntity player = getPlayer();
        if (player == null) return false;
        return player.isBlocking();
    }

    /**
     * Returns the shield item stack held in either hand, or EMPTY if none.
     */
    public static ItemStack getShieldStack() {
        ClientPlayerEntity player = getPlayer();
        if (player == null) return ItemStack.EMPTY;
        if (player.getMainHandStack().isOf(Items.SHIELD)) return player.getMainHandStack();
        if (player.getOffHandStack().isOf(Items.SHIELD)) return player.getOffHandStack();
        return ItemStack.EMPTY;
    }

    /**
     * Returns an armor piece from the player's equipment slots (0=boots, 1=leggings, 2=chestplate, 3=helmet).
     */
    public static ItemStack getArmorPiece(int slot) {
        ClientPlayerEntity player = getPlayer();
        if (player == null) return ItemStack.EMPTY;
        return player.getInventory().armor.get(slot);
    }

    /**
     * Returns the attack cooldown progress in [0, 1].
     * 1.0 means fully charged, 0.0 means just attacked.
     */
    public static float getAttackCooldown() {
        ClientPlayerEntity player = getPlayer();
        if (player == null) return 1f;
        return player.getAttackCooldownProgress(0f);
    }

    /**
     * Returns the distance from the player to a target entity, or -1 if null.
     */
    public static double distanceTo(LivingEntity target) {
        ClientPlayerEntity player = getPlayer();
        if (player == null || target == null) return -1;
        return player.distanceTo(target);
    }
}
