package com.pvpclient.modules.hud;

import com.pvpclient.hud.HudManager;
import com.pvpclient.modules.Module;

public final class PingDisplayModule extends Module {

    public PingDisplayModule() {
        super("Ping Display", "Shows real-time server ping.", Category.HUD);
    }

    @Override protected void onEnable()  { setHudVisible(true);  }
    @Override protected void onDisable() { setHudVisible(false); }

    private void setHudVisible(boolean visible) {
        HudManager.getInstance().getElements().stream()
            .filter(e -> e.getId().equals("ping_display"))
            .forEach(e -> e.setVisible(visible));
    }
}
