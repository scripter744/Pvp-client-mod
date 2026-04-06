package com.pvpclient.events;

import net.minecraft.entity.Entity;

/**
 * Fired when the player performs a melee attack on an entity.
 */
public final class AttackEvent {

    private final Entity target;

    public AttackEvent(Entity target) {
        this.target = target;
    }

    public Entity getTarget() {
        return target;
    }
}
