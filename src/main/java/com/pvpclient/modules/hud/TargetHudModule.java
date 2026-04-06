package com.pvpclient.modules.hud;

import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.Module;

public final class TargetHudModule extends Module {

    public TargetHudModule() {
        super("Target HUD", "Shows target name, health, distance, and armor.", Category.HUD);
    }

    @Override protected void onEnable()  { setHudVisible(true);  }
    @Override protected void onDisable() { setHudVisible(false); }

    private void setHudVisible(boolean visible) {
        HudManager.getInstance().getElements().stream()
            .filter(e -> e.getId().equals("target_hud"))
            .forEach(e -> e.setVisible(visible));
    }
}
