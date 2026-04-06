package com.pvpclient.modules.hud;

import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.Module;

/**
 * Module that controls the FPS Counter HUD element.
 */
public final class FpsCounterModule extends Module {

    public FpsCounterModule() {
        super("FPS Counter", "Shows current frames per second.", Category.HUD);
    }

    @Override
    protected void onEnable() {
        HudManager.getInstance().getElements().stream()
            .filter(e -> e.getId().equals("fps_counter"))
            .forEach(e -> e.setVisible(true));
    }

    @Override
    protected void onDisable() {
        HudManager.getInstance().getElements().stream()
            .filter(e -> e.getId().equals("fps_counter"))
            .forEach(e -> e.setVisible(false));
    }
}
