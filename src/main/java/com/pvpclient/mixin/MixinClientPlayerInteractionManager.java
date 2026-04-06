package com.pvpclient.mixin;

import com.pvpclient.events.AttackEvent;
import com.pvpclient.events.EventBus;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires an AttackEvent when the player attacks an entity.
 * Used by: ComboCounter, HitFeedback, DamageIndicators, CrosshairEditor.
 */
@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        EventBus.getInstance().post(new AttackEvent(target));
    }
}
