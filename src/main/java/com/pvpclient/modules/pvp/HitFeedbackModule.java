package com.pvpclient.modules.pvp;

import com.pvpclient.events.AttackEvent;
import com.pvpclient.events.EventBus;
import com.pvpclient.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvents;

/**
 * Hit Feedback — plays a satisfying sound on successful hit and optionally
 * triggers a subtle camera shake for tactile feedback.
 * No unfair advantages — purely cosmetic/audio.
 */
public final class HitFeedbackModule extends Module {

    private final java.util.function.Consumer<AttackEvent> attackListener;

    public HitFeedbackModule() {
        super("Hit Feedback", "Plays extra hit sound and visual feedback on attacks.", Category.PVP);
        attackListener = this::onAttack;
    }

    @Override
    protected void onEnable() {
        eventBus.subscribe(AttackEvent.class, attackListener);
    }

    @Override
    protected void onDisable() {
        eventBus.unsubscribe(AttackEvent.class, attackListener);
    }

    private void onAttack(AttackEvent event) {
        if (!(event.getTarget() instanceof LivingEntity)) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        // Play a subtle click/tick sound as additional feedback
        mc.player.playSound(
            SoundEvents.UI_BUTTON_CLICK.value(),
            0.4f,  // volume (quiet enough not to be annoying)
            1.2f   // pitch
        );
    }
}
