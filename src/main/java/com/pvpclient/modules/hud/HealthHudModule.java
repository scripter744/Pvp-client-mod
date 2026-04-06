package com.pvpclient.modules.hud;

import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.Module;

public final class HealthHudModule extends Module {

    public HealthHudModule() {
        super("Health HUD", "Displays current health as a bar and number.", Category.HUD);
    }

    @Override protected void onEnable()  { setHudVisible(true);  }
    @Override protected void onDisable() { setHudVisible(false); }

    private void setHudVisible(boolean visible) {
        HudManager.getInstance().getElements().stream()
            .filter(e -> e.getId().equals("health_hud"))
            .forEach(e -> e.setVisible(visible));
    }
}
