package com.pvpclient.modules.pvp;

import com.pvpclient.events.EventBus;
import com.pvpclient.events.TickEvent;
import com.pvpclient.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import org.lwjgl.glfw.GLFW;

/**
 * Toggle Sneak — pressing sneak key toggles sneak mode on/off instead of hold.
 */
public final class ToggleSneakModule extends Module {

    private boolean sneakToggled = false;
    private boolean wasSneakKeyDown = false;

    private final java.util.function.Consumer<TickEvent> tickListener;

    public ToggleSneakModule() {
        super("Toggle Sneak", "Press sneak to toggle sneak instead of holding.", Category.PVP);
        tickListener = this::onTick;
    }

    @Override
    protected void onEnable() {
        eventBus.subscribe(TickEvent.class, tickListener);
    }

    @Override
    protected void onDisable() {
        eventBus.unsubscribe(TickEvent.class, tickListener);
        // Reset sneak state when disabled
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.options.sneakKey.setPressed(false);
        }
    }

    private void onTick(TickEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.currentScreen != null) return;

        boolean sneakKeyDown = mc.options.sneakKey.isPressed();

        // Detect rising edge of sneak key press
        if (sneakKeyDown && !wasSneakKeyDown) {
            sneakToggled = !sneakToggled;
        }
        wasSneakKeyDown = sneakKeyDown;

        // Apply sneak state by overriding key state
        mc.options.sneakKey.setPressed(sneakToggled);
    }
}
