package com.pvpclient.modules.pvp;

import com.pvpclient.events.EventBus;
import com.pvpclient.events.TickEvent;
import com.pvpclient.modules.Module;
import com.pvpclient.utils.PlayerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Toggle Sprint — automatically maintains sprinting without holding the sprint key.
 * Only sprints when the player is moving forward and not sneaking.
 * This is considered a quality-of-life feature present in vanilla options and legal clients.
 */
public final class ToggleSprintModule extends Module {

    private final java.util.function.Consumer<TickEvent> tickListener;

    public ToggleSprintModule() {
        super("Toggle Sprint", "Automatically sprints while moving forward.", Category.PVP);
        tickListener = this::onTick;
    }

    @Override
    protected void onEnable() {
        eventBus.subscribe(TickEvent.class, tickListener);
    }

    @Override
    protected void onDisable() {
        eventBus.unsubscribe(TickEvent.class, tickListener);
    }

    private void onTick(TickEvent event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.currentScreen != null) return;

        boolean movingForward = mc.options.forwardKey.isPressed();
        boolean sneaking      = player.isSneaking();
        boolean onGround      = player.isOnGround();

        if (movingForward && !sneaking) {
            player.setSprinting(true);
        }
    }
}
