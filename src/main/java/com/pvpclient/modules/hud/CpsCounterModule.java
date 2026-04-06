package com.pvpclient.modules.hud;

import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.Module;

public final class CpsCounterModule extends Module {

    public CpsCounterModule() {
        super("CPS Counter", "Tracks left and right clicks per second.", Category.HUD);
    }

    @Override protected void onEnable()  { setHudVisible(true);  }
    @Override protected void onDisable() { setHudVisible(false); }

    private void setHudVisible(boolean visible) {
        HudManager.getInstance().getElements().stream()
            .filter(e -> e.getId().equals("cps_counter"))
            .forEach(e -> e.setVisible(visible));
    }
}
