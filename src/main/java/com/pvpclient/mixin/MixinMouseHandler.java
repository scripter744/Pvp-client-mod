package com.pvpclient.mixin;

import com.pvpclient.events.EventBus;
import com.pvpclient.events.InputEvent;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Captures mouse button events and dispatches InputEvents to the EventBus.
 * Used by: CPS Counter, ToggleSprint, CrosshairEditor dynamic mode.
 */
@Mixin(Mouse.class)
public abstract class MixinMouseHandler {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        InputEvent.Button evButton = switch (button) {
            case 0 -> InputEvent.Button.LEFT;
            case 1 -> InputEvent.Button.RIGHT;
            case 2 -> InputEvent.Button.MIDDLE;
            default -> null;
        };
        if (evButton == null) return;

        boolean pressed = action == 1; // GLFW_PRESS == 1
        EventBus.getInstance().post(new InputEvent(evButton, pressed));
    }
}
